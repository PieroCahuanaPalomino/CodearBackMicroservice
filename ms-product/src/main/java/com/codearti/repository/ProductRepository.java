package com.codearti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codearti.model.entity.CategoryEntity;
import com.codearti.model.entity.DeletedProduct;
import com.codearti.model.entity.ProductEntity;
import com.codearti.model.entity.ProductStatus;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity,Long>{
	
	//.CREATED HACE REFERENCIA AL 0
	//status de la base de datos sea nulo o status dado por parametro sea igual al status de la base de datos

	//ms-gateway-server:8080/api/product/v1
	//Por defecto el STATUS es NULL por tanto solo brindara los productos que tengan un deleted = 0 - CREATED
	
	//ms-gateway-server:8080/api/product/v1?status=NEW
	//En la base de datos solo coincide en los productos 1,2,3 donde el CREATED = 0 Y (STATUS = O - NEW ).
	
	//ms-gateway-server:8080/api/product/v1?status=OLD
	//En la base de datos no hay coincidencias donde el CREATED = 0 Y (STATUS = 1 - OLD ).
	@Query("from ProductEntity where deleted = com.codearti.model.entity.DeletedProduct.CREATED and ((:status is null) or (status = :status))")
	List<ProductEntity> findAll(@Param("status") ProductStatus status);
	
	List<ProductEntity> findByCategoryAndDeleted(CategoryEntity category, DeletedProduct deleted);
}
