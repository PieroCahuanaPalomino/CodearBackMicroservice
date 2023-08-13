package com.codearti.events;

import com.codearti.model.dto.ProductResponseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductCreatedEvent extends Event<ProductResponseDto> {

}