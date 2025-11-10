package com.costinarj.ecommerce.service;

import com.costinarj.ecommerce.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductCsvParserTest {

    @Test
    void testCsvParsing(@TempDir Path tempDir) throws IOException {
        
        // Given
        ProductCsvParser parser = new ProductCsvParser();
        String csvContent = """
                sku,name,stockQuantity
                ABC123,Wireless Mouse,10
                XYZ999,Keyboard,5
                """;
        
        Path csvFile = tempDir.resolve("test_products.csv");
        Files.writeString(csvFile, csvContent);

        // When
        List<Product> products = parser.importFromCsv(csvFile.toString());

        // Then
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("ABC123", products.get(0).getSku());
        assertEquals("Wireless Mouse", products.get(0).getName());
        assertEquals(10, products.get(0).getStockQuantity());
    }
}