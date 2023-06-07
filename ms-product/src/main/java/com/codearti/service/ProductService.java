package com.codearti.service;

import java.util.List;

import com.codearti.model.dto.ProductCreateRequestDto;
import com.codearti.model.dto.ProductResponseDto;
import com.codearti.model.dto.ProductUpdateRequestDto;
import com.codearti.model.dto.ProductUpdateStockRequestDto;
import com.codearti.model.entity.ProductStatus;

public interface ProductService {

	public List<ProductResponseDto> findAll(ProductStatus status,int port);
	public ProductResponseDto findById(Long id, int port);
	public List<ProductResponseDto> findByIdCategory(Long id, int port);
	public ProductResponseDto create(ProductCreateRequestDto productRequest, int port);
	public ProductResponseDto update(Long id, ProductUpdateRequestDto productRequest, int port);
	public ProductResponseDto updateStock(Long id, ProductUpdateStockRequestDto productRequest, int port);
	public void delete(Long id, int port);

}
