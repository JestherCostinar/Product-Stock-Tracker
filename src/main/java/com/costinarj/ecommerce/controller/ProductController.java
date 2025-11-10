package com.costinarj.ecommerce.controller;

import com.costinarj.ecommerce.entity.Product;
import com.costinarj.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The {@code ProductController} is the RESTFUL Web Service controller which provide method to retrieve product entity
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        ResponseEntity<List<Product>> response;

        try {
            List<Product> products = productService.getAllProducts();
            logger.info("Retrieved {} products from database", products.size());
            response = ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error retrieving products: {}", e.getMessage(), e);
            response = ResponseEntity.internalServerError().build();
        }

        return response;
    }
}
