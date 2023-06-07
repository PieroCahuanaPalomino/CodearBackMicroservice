package com.codearti.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_item")
public class CartItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long productId;
	
	private String name;
	
	private BigDecimal price;
	
	private Integer quantity;
	
	private BigDecimal subTotal;
	
	@CreationTimestamp
	private LocalDateTime creationDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id", insertable = false, updatable = false)
    private CartEntity cart;
	
	@PrePersist
	public void setSubTotalPersist() {
		subTotal = price.multiply(BigDecimal.valueOf(quantity));
	}

}
