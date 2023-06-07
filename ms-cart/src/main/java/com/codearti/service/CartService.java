package com.codearti.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codearti.configuration.error.ResourceNotFoundException;
import com.codearti.model.dto.CartRequestDeleteDto;
import com.codearti.model.dto.CartRequestDto;
import com.codearti.model.dto.CartResponseDto;
import com.codearti.model.dto.ProductResponseDto;
import com.codearti.model.entity.CartEntity;
import com.codearti.model.entity.CartItemEntity;
import com.codearti.model.mapper.CartMapper;
import com.codearti.repository.CartRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartService {
	private static final Logger LOGGER=org.slf4j.LoggerFactory.getLogger(CartService.class);
	
	private final CartRepository cartRepository;
	
	private final CartMapper mapper;
	
	private final ProductService productService;
	
	@Transactional(readOnly = true)
	public CartResponseDto findByCustomerId(Long customerId) {
		log.info("findByCustomerId");
		return cartRepository.findByCustomerId(customerId)
				.map(mapper::entityToResponse).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
	}
	
	@Transactional
	public CartResponseDto addItem(Long customerId, CartRequestDto cartRequestDto) {
		log.info("addItem");

		//ms-gateway-server:8080/api/cart/v1/333/item	
		/*
		  customerId=333 
		 
		  cartRequestDto=
		  	[{
        	"product_id": 1,
        	"quantity": 5
    		},
    		{
        	"product_id": 2,
        	"quantity": 3
    		},
    		{
        	"product_id": 3,
        	"quantity": 3
    		}]
		*/
	
		//Retorna una entidad de tipo CartEntity
		//Object
		Optional<CartEntity> cartEntityResult = cartRepository.findByCustomerId(customerId);
		if(cartEntityResult.isEmpty()) {
			//ms-gateway-server:8080/api/cart/v1/100000/item
			//Regresa FALSE cuando retorna el objeto CartEntity con valores porque el carritoList 333 si existe
			//Regresa TRUE cuando retorna el objeto CartEntity vacio porque el carritoList 10000 no existe
			throw new ResourceNotFoundException("Resource not found");
		}

		//cartEntity obtiene los valores de cartEntityResult
		CartEntity cartEntity = cartEntityResult.get();
		
		//Recorriendo el cuerpo de la Lista agregada
		cartRequestDto.getItems().forEach(p -> {
			
			//Object
			Optional<CartItemEntity> cartItemEntityOptional = cartEntity
					.getItems().stream().filter(pt -> pt.getProductId() == p.getProductId()).findFirst();
			
			//isPresent devuelve TRUE si hay valor en un Optional
			//La base de datos esta vacia por tanto el cartItemEntityOptional no obtiene valores y el !(IsPresent) devuelve TRUE
			CompletableFuture<ProductResponseDto> productCF = productService.findByIdResilience(p.getProductId());
			ProductResponseDto product = null;
			try {
				//Se ejecuta este bloque de codigo
				product = productCF.get(); //puede que devuelva null o un producto
			} catch (InterruptedException | ExecutionException e) {
				//Si el bloque de codigo presenta errores de estos 2 tipos  
				log.warn("productCF.get(): " + e.getMessage(), e);
			}
			if(!(cartItemEntityOptional.isPresent())) {
				//ProductResponseDto product = productService.findById(p.getProductId());
				
				//Para el Circuit Breaker se activa cuando el ProductService No Funciona
				//findByIdResilience Trae un producto del carro y se itera por el forEach de la linea 78
				//Se busca un producto con el id p.getProductId() del cuerpo cartRequestDto y si se ingresa un Id no existente devolvera null
				/*CompletableFuture<ProductResponseDto> productCF = productService.findByIdResilience(p.getProductId());
				ProductResponseDto product = null;*/
				/*try {
					//Se ejecuta este bloque de codigo
					product = productCF.get(); //puede que devuelva null o un producto
				} catch (InterruptedException | ExecutionException e) {
					//Si el bloque de codigo presenta errores de estos 2 tipos  
					log.warn("productCF.get(): " + e.getMessage(), e);
				}*/
				if(product == null) {
					//Si en inserta un producto con Id no existente
					throw new ResourceNotFoundException("Product not found with id: " + p.getProductId());
				}else {
					log.info("ms-product port: " + product.getPort());
					cartEntity.getItems().add(mapper.responseToEntity(product, p.getQuantity()));
				}
			}else {
				/*try {
					//Se ejecuta este bloque de codigo
					product = productCF.get(); //puede que devuelva null o un producto
				} catch (InterruptedException | ExecutionException e) {
					//Si el bloque de codigo presenta errores de estos 2 tipos  
					log.warn("productCF.get(): " + e.getMessage(), e);
				}*/
				if(product == null) {
					//Si en inserta un producto con Id no existente
					throw new ResourceNotFoundException("Product not found with id: " + p.getProductId());
				}else {
					//ProductResponseDto products=product;
					log.info("ms-product port: " + product.getPort());
					//cartEntity.getItems().add(mapper.responseToEntity(product, p.getQuantity()));
					cartEntity.getItems().forEach((pa)->{
						if(pa.getId()==p.getProductId()) {
							pa.setQuantity(p.getQuantity());
							pa.setSubTotalPersist();
							//pa.setPostUpdate();
							//pa.setSubTotal(new BigDecimal(0));
						}
					});

					//cartEntity.getItems().replaceAll(pa->pa.getId()==p.getProductId()? pa:mapper.responseToEntity(products, p.getQuantity()));
				}
			}
			
		});

		CartEntity cartEntityRpta=cartRepository.save(cartEntity);
		log.info("Datos ");
		
		CartResponseDto rpta=mapper.entityToResponse(cartEntityRpta);

		return rpta;
		

	}
	
	@Transactional
	public CartResponseDto removeItem(Long customerId, CartRequestDeleteDto cartRequestDeleteDto) {
		log.info("deleteItem");
		
		Optional<CartEntity> cartEntityResult = cartRepository.findByCustomerId(customerId);
		if(cartEntityResult.isEmpty()) {
			throw new ResourceNotFoundException("Resource not found");
		}
		
		CartEntity cartEntity = cartEntityResult.get();
		
		cartRequestDeleteDto.getItems().forEach(p -> {
			cartEntity.getItems().removeIf(pt -> pt.getProductId() == p.getProductId());
			
			CompletableFuture<ProductResponseDto> productCF = productService.findByIdResilience(p.getProductId());
			ProductResponseDto product = null;
			try {
				//Se ejecuta este bloque de codigo
				product = productCF.get(); //puede que devuelva null o un producto
			} catch (InterruptedException | ExecutionException e) {
				//Si el bloque de codigo presenta errores de estos 2 tipos  
				log.warn("productCF.get(): " + e.getMessage(), e);
			}
			if(product == null) {
				//Si en inserta un producto con Id no existente
				throw new ResourceNotFoundException("Product not found with id: " + p.getProductId());
			}
		});

		cartRepository.save(cartEntity);
		
		return mapper.entityToResponse(cartEntity);
	}

}