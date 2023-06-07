package com.codearti.repository;

import java.util.List;
import java.util.Optional;

import com.codearti.model.entity.CartEntity;

import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<CartEntity, Long> {
	
	Optional<CartEntity> findByCustomerId(Long customerId);

}
