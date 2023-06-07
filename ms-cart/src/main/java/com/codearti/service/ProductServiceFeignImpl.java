package com.codearti.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.codearti.client.ProductClientRest;
import com.codearti.model.dto.ProductResponseDto;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class ProductServiceFeignImpl implements ProductService {
	
	private final ProductClientRest client;
	
	private final BeanFactory beanFactory;

	@Override
	public List<ProductResponseDto> findAll() {
		log.info("findAll");
		return client.findAll();
	}

	@Override
	public ProductResponseDto findById(Long id) {
		log.info("findById feign");
		try {
			return client.findById(id);
		}catch (FeignException e) {
			if(e.status() == HttpStatus.NOT_FOUND.value()) {
				return null;
			}else {
				throw e;
			}
		}
	}

	@CircuitBreaker(name="product", fallbackMethod = "findByIdResilienceFallBackMethod")
	@Override
	public CompletableFuture<ProductResponseDto> findByIdResilience(Long id) {
		log.info("findByIdResilience");
		return CompletableFuture.supplyAsync(()-> {
			try {
				return client.findById(id);
			} catch (FeignException e) {
				if(e.status() == HttpStatus.NOT_FOUND.value()) {
					return null;
				}else{
					throw e;
				}
			}	
		}
		//issue: https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/integrations.html#sleuth-async-executor-service-integration
		, new TraceableExecutorService(beanFactory, Executors.newFixedThreadPool(2), "findProductByIdResilience"));
		
	}
	
	@Override
	public CompletableFuture<ProductResponseDto> findByIdResilienceFallBackMethod(Long id, Throwable t) {
		log.info("findByIdResilienceFallBackMethod: " + t.getMessage());
		try {
			ProductResponseDto product = ProductResponseDto.builder()
			.id(id)
			.name("TEMPORAL")
			.stock(0D)
			.price(BigDecimal.ZERO)
			.build();
			return CompletableFuture.supplyAsync(()-> product, 
					//issue: https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/integrations.html#sleuth-async-executor-service-integration
					new TraceableExecutorService(beanFactory, Executors.newFixedThreadPool(2), "findProductByIdResilienceFallBackMethod"));
		}catch (FeignException e) {
			if(e.status() == HttpStatus.NOT_FOUND.value()) {
				return null;
			}else{
				throw e;
			}
		}
	}

}
