package com.codearti;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.codearti.model.dto.CartResponseDto;
import com.codearti.model.entity.CartEntity;
import com.codearti.model.entity.CartItemEntity;
import com.codearti.model.mapper.CartMapper;
import com.codearti.repository.CartRepository;
import com.codearti.service.CartService;
import com.codearti.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
	
	@Mock
	private CartRepository productRepository;
	
	@Mock
	private ProductService productService;
	
	private CartMapper mapper = Mappers.getMapper(CartMapper.class);
	
	//@InjectMocks
	private CartService cartService;
	
	@BeforeEach
	public void setup() {
		
		cartService = new CartService(productRepository, mapper, productService); //SI usamos InjecjtMocks esta linea se borra
		
		CartItemEntity cartItemEntity1 = CartItemEntity.builder()
				.productId(3L)
				.name("Teclado")
				.price(BigDecimal.valueOf(300))
				.quantity(3)
				//.cart(cartEntity)
				.build();
		
		CartItemEntity cartItemEntity2 = CartItemEntity.builder()
				.productId(2L)
				.name("Monitor")
				.price(BigDecimal.valueOf(200))
				.quantity(2)
				//.cart(cartEntity)
				.build();
		
		CartEntity cartEntity = CartEntity.builder()
				.customerId(1L)
				.items(List.of(cartItemEntity1, cartItemEntity2))
				.build();
		
		Mockito.when(productRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));
	}
	
	@Test
	public void whenValidGetId_ThenReturnCart() {
		CartResponseDto cartResponseDto = cartService.findByCustomerId(1L);
		assertEquals(1L, cartResponseDto.getCustomerId());
		assertEquals(2, cartResponseDto.getItems().size());
		assertEquals(3L, cartResponseDto.getItems().get(0).getProductId());
		assertEquals("Teclado", cartResponseDto.getItems().get(0).getName());
		assertEquals(BigDecimal.valueOf(300), cartResponseDto.getItems().get(0).getPrice());
		assertEquals(3, cartResponseDto.getItems().get(0).getQuantity());
	}

}