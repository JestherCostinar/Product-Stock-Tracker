package com.costinarj.ecommerce.service;

import com.costinarj.ecommerce.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@code ProductCsvParser} a parser class that handles parsing and validating CSV data to create Product entities.
 */
@Component
public class ProductCsvParser {

    private static final Logger log = LoggerFactory.getLogger(ProductCsvParser.class);

    /**
     * parse the CSV file to create a list of Product objects using Apache Commons CSV.
     *
     * @param filePath path of the csv file.
     * @return List of products
     */
    public List<Product> importFromCsv(final String filePath) {
        if (!Files.exists(Paths.get(filePath))) {
            log.warn("CSV file not found at: {}. Returning empty list.", filePath);
            return new ArrayList<>();
        }

        return parseCSV(filePath);
    }

    private List<Product> parseCSV(final String filePath) {

        List<Product> products = new ArrayList<>();
        Set<String> seenSkus = new HashSet<>();

        try (Reader reader = new FileReader(filePath);
                CSVParser csvParser = CSVFormat.DEFAULT.parse(reader)) {

            boolean isFirstRecord = true;

            for (CSVRecord csvRecord : csvParser) {
                long lineNumber = csvRecord.getRecordNumber();

                // Skip header(sku,name,stockQuantity)
                if (isFirstRecord) {
                    isFirstRecord = false;
                    continue;
                }

                // Validate empty records
                if (isRecordEmpty(csvRecord)) {
                    log.warn("Empty line at {}. Skipping.", lineNumber);
                    continue;
                }

                // Validate column count
                if (csvRecord.size() != 3) {
                    log.warn("Invalid CSV format at line {}: Expected 3 columns, found {}. Skipping.",
                            lineNumber, csvRecord.size());
                    continue;
                }

                try {
                    String sku = csvRecord.get(0).trim();
                    String name = csvRecord.get(1).trim();
                    int stockQuantity = Integer.parseInt(csvRecord.get(2).trim());

                    if (seenSkus.contains(sku)) {
                        log.warn("Duplicate SKU at line {}: {}. Skipping.", lineNumber, sku);
                        continue;
                    }

                    seenSkus.add(sku);
                    Product product = new Product(sku, name, stockQuantity);
                    products.add(product);

                    if (stockQuantity == 0) {
                        log.info("Product {} is out of stock.", sku);
                    }

                } catch (NumberFormatException e) {
                    log.warn("Invalid number at line {}: '{}'. Skipping.",
                            lineNumber, csvRecord.get(2).trim());
                }
            }

        } catch (IOException e) {
            log.error("Error reading or parsing the CSV file: {}", filePath, e);
        }

        return products;
    }

    private boolean isRecordEmpty(CSVRecord csvRecord) {
        boolean isEmpty = true;

        for (String value : csvRecord) {
            if (value != null && !value.trim().isEmpty()) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }
}
