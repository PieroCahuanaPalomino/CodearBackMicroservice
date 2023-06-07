package com.codearti.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.codearti.configuration.error.ResourceNotFoundException;
import com.codearti.model.dto.ProductCreateRequestDto;
import com.codearti.model.dto.ProductResponseDto;
import com.codearti.model.dto.ProductUpdateRequestDto;
import com.codearti.model.dto.ProductUpdateStockRequestDto;
import com.codearti.model.entity.CategoryEntity;
import com.codearti.model.entity.DeletedProduct;
import com.codearti.model.entity.ProductEntity;
import com.codearti.model.entity.ProductStatus;
import com.codearti.model.mapper.ProductMapper;
import com.codearti.repository.ProductRepository;
import com.codearti.service.ProductService;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor //construye constructores de las variables que tienen FINAL;
public class ProductServiceImpl implements ProductService{
		
		private final ProductRepository repository;
		
		private final ProductMapper mapper;
		
		@Transactional(readOnly = true) //SOLO LECTURA
		public List<ProductResponseDto> findAll(ProductStatus status,int port){
			log.info("findAll");
			var list = repository.findAll(status);
			log.info("finded");
			return list.stream().map(p -> mapper.entityToResponse(p, port)).collect(Collectors.toList());
		}
		
		@Transactional(readOnly = true)
		public ProductResponseDto findById(Long id, int port) {
			log.info("findById");
			return repository.findById(id).
					filter(p -> p.getDeleted() == DeletedProduct.CREATED) //Solo trae los que tengan deleted=0
					.map(p -> mapper.entityToResponse(p, port)).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
		}
		
		@Transactional(readOnly = true)
		public List<ProductResponseDto> findByIdCategory(Long id, int port) {
			log.info("findByCategory");
			var list = repository.findByCategoryAndDeleted(CategoryEntity.builder().id(id).build(), DeletedProduct.CREATED);
			return list.stream().map(p -> mapper.entityToResponse(p, port)).collect(Collectors.toList());
		}
		
		@Transactional
		public ProductResponseDto create(ProductCreateRequestDto productRequest, int port) {
			log.info("create");
			ProductEntity productEntity = mapper.requestToEntity(productRequest);
			repository.save(productEntity);
			log.info("saved");
			return mapper.entityToResponse(productEntity, port);
		}
		
		@Transactional
		public ProductResponseDto update(Long id, ProductUpdateRequestDto productRequest, int port) {
			log.info("update");
			ProductEntity productEntity = getProductById(id);
			//BEANUTILS FUNCIONA COMO UN MAPEADOR COPIA Y PEGA LAS PROPIEDADES DE LOS OBJETOS
			//ProductUpdateRequestDto TIENE PROPIEDAD LONG categoryId
			//ProductEntity TIENE PROPIEDAD LA CLASE category
			//POR TANTO NO SE PUEDEN COPIAR Y PEGAR XQ SON DIFERENTES. EN ESTAS OCASIONES USAMOS LOS SETTERS.
			BeanUtils.copyProperties(productRequest, productEntity);
			productEntity.setCategory(CategoryEntity.builder().id(productRequest.getCategoryId()).build());
			repository.save(productEntity);
			log.info("updated");
			return mapper.entityToResponse(productEntity, port);
		}
		
		@Transactional
		public ProductResponseDto updateStock(Long id, ProductUpdateStockRequestDto productRequest, int port) {
			log.info("updateStock");
			ProductEntity productEntity = getProductById(id);
			productEntity.setStock(productEntity.getStock() + productRequest.getStock());
			repository.save(productEntity);
			log.info("updated");
			return mapper.entityToResponse(productEntity, port);
		}
		
		@Transactional
		public void delete(Long id, int port) {
			log.info("delete");
			ProductEntity productEntity = getProductById(id);
			productEntity.setDeleted(DeletedProduct.DELETED);
			repository.save(productEntity);
			log.info("deleted");
		}

		//METODO PARA DETECTAR SI EL ID EXISTE
		private ProductEntity getProductById(Long id) {
			Optional<ProductEntity> productEntityOp = repository.findById(id).
					filter(p -> p.getDeleted() == DeletedProduct.CREATED);
			if(!productEntityOp.isPresent()) {
				throw new ResourceNotFoundException("Resource not found");
			}
			return productEntityOp.get();
		}
}
