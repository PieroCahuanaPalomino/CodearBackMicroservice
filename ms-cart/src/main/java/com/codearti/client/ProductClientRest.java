package com.codearti.client;

import java.util.List;

import com.codearti.model.dto.ProductResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-product"/*, url = "http://localhost:8001"*/, path = "/v1")
public interface ProductClientRest {
	
	@GetMapping
	public List<ProductResponseDto> findAll();
	
	@GetMapping("/{id}")
	public ProductResponseDto findById(@PathVariable Long id);

}