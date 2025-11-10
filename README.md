# Product Stock Tracker

A Spring Boot application that reads product stock data from a CSV file and stores it in an H2 in-memory database using Apache Commons CSV for robust parsing.

## Prerequisites
- **Java 11+** (compatible with Java 11 through 17)
- **Spring Boot 2.7+** (I used version 2.7.18 for creating this solution)
- **Maven** (for building the project)

## How to Build
From project root directory:
```bash
mvn clean install
```

## How to Run
After building, run the application:
```bash
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar
```

Alternative: Run using your IDE by executing the main class `ProductStockTrackerApplication`

## How to Test the API
Test the products endpoint:
```bash
curl http://localhost:8080/products
```

The application automatically imports products from CSV on startup, so data could see in the api response.

## Where to Place the CSV File
The CSV file should be located at:
```
src/main/resources/static/
```


## Assumptions and Design Decisions

### 1. CSV Import Approach
- I assumed the stocks.csv file is a one-time import, and as per the requirements I should manually create it and placed it somewhere in the project. Since this doesn’t change often, I used @PostConstruct to import the data when the application starts. This avoids the need for a another endpoint or a job scheduler.

### 2. Service Class Decision
- To follow the SOLID principles, I created two separate service classes. One (ProductService) handles product-related logic, while the other (ProductCsvParse) focuses only on parsing the CSV file. This keeps each class focused on a single responsibility.

### 3. CSV Libray Approach
- Since I read in the business requirements that Apache Commons CSV is commonly used (I also agree on this :D ), I used it.

### 4. CSV File location
- I assumed the CSV file would be placed in the src/main/resources/static/ directory. This is a common location for static files in Spring Boot projects.


---

# Integration Test Documentation


## Test Scenarios

### 1. Correct CSV File Test
**Objective**: Verify the application starts correctly and imports CSV data

**Stocks.csv**:
```csv
sku,name,stockQuantity
TEST001,Test Product 1,10
TEST002,Test Product 2,8
TEST003,Test Product 3,5
```

**Expected Results**:
- Log shows: "X products have been imported into the database"
- No any log related csv validation error messages in startup logs

**Actual Results**:
```
2025-11-10 18:36:28.850  INFO 8184 --- [           main] c.c.ecommerce.service.ProductService     : Application started, importing products from CSV file: src/main/resources/static/stocks.csv
2025-11-10 18:36:28.918  INFO 8184 --- [           main] c.c.ecommerce.service.ProductService     : 3 products have been imported into the database.
```

**Comments**: WORKING AS EXPECTED

### 2 Get /producsts API Endpoint Test
**Objective**: verify that the imported products will be in show in the response


CURL
```bash
curl -X GET http://localhost:8080/products
```
**Expected Response**: HTTP 200 with JSON array of products

**Actual Response**:
```json
[
  {
    "id": 1,
    "sku": "TEST001",
    "name": "Test Product 1",
    "stockQuantity": 10
  },
  {
    "id": 2,
    "sku": "TEST002",
    "name": "Test Product 2",
    "stockQuantity": 8
  },
  {
    "id": 3,
    "sku": "TEST003",
    "name": "Test Product 3",
    "stockQuantity": 5
  }
]
```
**Comments**: WORKING AS EXPECTED

### 3. [ VALIDATION ] Missing CSV File Test
**Objective**: Verify that if csv file path is not valid, should log an error

**Setup**: I put wrong file path instead of: src/main/resources/static/


**Expected Results**:
- Log shows: "CSV file not found..."

**Actual Results**:
```
2025-11-10 18:48:34.047  WARN 24852 --- [           main] c.c.ecommerce.service.ProductCsvParser   : CSV file not found at: src/main/resources/statsdic/stocks.csv. Returning empty list.
2025-11-10 18:48:34.047  WARN 24852 --- [           main] c.c.ecommerce.service.ProductService     : No valid products found in CSV file.```
```
**Comments**: WORKING AS EXPECTED


### 4. [ VALIDATION ] Product has zero stock
**Objective**: Verify that if csv record has 0 stock, should log an error

**Stocks.csv**:
```csv
sku,name,stockQuantity
TEST001,Test Product 1,10
TEST002,Test Product 2,0
TEST003,Test Product 3,0
```
**Expected Results**:
- Log shows: "SKUs is out of stock"

**Actual Results**:
```
2025-11-10 18:53:01.709  INFO 30780 --- [           main] c.c.ecommerce.service.ProductCsvParser   : Product TEST002 is out of stock.
2025-11-10 18:53:01.709  INFO 30780 --- [           main] c.c.ecommerce.service.ProductCsvParser   : Product TEST003 is out of stock.
```
**Comments**: WORKING AS EXPECTED

### 5. [ VALIDATION ] Has Duplicate SKU
**Objective**: Verify that if csv record has duplicate sku, should log an error

**Stocks.csv**:
```csv
sku,name,stockQuantity
TEST001,Test Product 1,10
TEST002,Test Product 2,8
TEST002,Test Product 3,5
TEST004,Test Product 4,15
TEST004,Test Product 5,1
```
**Expected Results**:
- Log shows: "Duplicate SKUs. Skipping..."
- Log shows the correct number of import of products, duplicate is excluded.
  **Actual Results**:
```
2025-11-10 18:55:15.241  WARN 31172 --- [           main] c.c.ecommerce.service.ProductCsvParser   : Duplicate SKU at line 4: TEST002. Skipping.
2025-11-10 18:55:15.241  WARN 31172 --- [           main] c.c.ecommerce.service.ProductCsvParser   : Duplicate SKU at line 6: TEST004. Skipping.
2025-11-10 18:55:15.298  INFO 31172 --- [           main] c.c.ecommerce.service.ProductService     : 3 products have been imported into the database.
```
**Comments**: WORKING AS EXPECTED

--- 
