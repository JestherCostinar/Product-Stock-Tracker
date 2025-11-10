package com.costinarj.ecommerce.service;

import com.costinarj.ecommerce.entity.Product;
import com.costinarj.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCsvParser productCsvParser;

    @InjectMocks
    private ProductService productService;

    @Test
    void testGetAllProducts() {
        // Given
        List<Product> testProducts = Arrays.asList(
                new Product("ABC123", "Wireless Mouse", 10),
                new Product("XYZ999", "Keyboard", 5)
        );
        when(productRepository.findAll()).thenReturn(testProducts);

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ABC123", result.get(0).getSku());
        verify(productRepository, times(1)).findAll();
    }
}