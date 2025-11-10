package com.costinarj.ecommerce.service;

import com.costinarj.ecommerce.entity.Product;
import com.costinarj.ecommerce.repository.ProductRepository;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The {@code ProductService} is a class manages which provide the method to import the product from csv and retrieve list of product
 */
@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private static final String CSV_FILE_PATH = "src/main/resources/static/stocks.csv";

    private final ProductRepository productRepository;

    private final ProductCsvParser productCsvParser;

    public ProductService(ProductRepository productRepository, ProductCsvParser productCsvParser) {
        this.productRepository = productRepository;
        this.productCsvParser = productCsvParser;
    }

    /**
     * This will trigger when the application starts. It imports product data from a CSV file.
     */
    @PostConstruct
    public void importProductsFromCSV() {
        log.info("Application started, importing products from CSV file: {}", CSV_FILE_PATH);

        List<Product> products = productCsvParser.importFromCsv(CSV_FILE_PATH);

        if (products.isEmpty()) {
            log.warn("No valid products found in CSV file.");
        } else {
            productRepository.saveAll(products);
            log.info("{} products have been imported into the database.", products.size());
        }
    }

    /**
     * Retrieves all products from the database.
     *
     * @return List of all products.
     */
    public List<Product> getAllProducts() {
        log.info("Fetching all products from the database.");
        return productRepository.findAll();
    }
}
