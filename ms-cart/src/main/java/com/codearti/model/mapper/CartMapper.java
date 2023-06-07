package com.codearti.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.codearti.model.dto.CartItemResponseDto;
import com.codearti.model.dto.CartResponseDto;
import com.codearti.model.dto.ProductResponseDto;
import com.codearti.model.entity.CartEntity;
import com.codearti.model.entity.CartItemEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

	@Mapping(source = "entity.customerId", target = "customerId")
	@Mapping(source = "entity.creationDate", target = "creationDate")
	@Mapping(source = "entity.items", target = "items", qualifiedByName = "entityToResponseForItems")
	CartResponseDto entityToResponse(CartEntity entity);
	
	@Mapping(source = "entity.productId", target = "productId")
	@Mapping(source = "entity.name", target = "name")
	@Mapping(source = "entity.price", target = "price")
	@Mapping(source = "entity.quantity", target = "quantity")
	@Mapping(source = "entity.subTotal", target = "subTotal")
	@Mapping(source = "entity.creationDate", target = "creationDate")
	CartItemResponseDto entityToResponse(CartItemEntity entity);
	
	@Named("entityToResponseForItems")
    default List<CartItemResponseDto> entityToResponseForItems(List<CartItemEntity> items) {
		//return items.stream().map(p -> entityToResponse(p)).toList();
		return items.stream().map(p -> entityToResponse(p)).collect(Collectors.toList());
    }
	
	@Mapping(source = "response.id", target = "productId")
	@Mapping(source = "response.name", target = "name")
	@Mapping(source = "response.price", target = "price")
	@Mapping(source = "quantity", target = "quantity")
	CartItemEntity responseToEntity(ProductResponseDto response, Integer quantity);

}