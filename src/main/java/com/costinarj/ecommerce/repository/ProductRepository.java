package com.costinarj.ecommerce.repository;

import com.costinarj.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The {@code ProductRepository} is an interface than extends JpaRepository to provides CRUD operations for Product entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

}
