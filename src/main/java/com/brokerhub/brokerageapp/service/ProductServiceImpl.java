package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BulkUploadResponseDTO;
import com.brokerhub.brokerageapp.dto.ProductBasicInfoDTO;
import com.brokerhub.brokerageapp.dto.ProductDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.Product;
import com.brokerhub.brokerageapp.repository.ProductRepository;
import com.brokerhub.brokerageapp.utils.ProductExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCacheService productCacheService;

    @Autowired
    TenantContextService tenantContextService;

    public ResponseEntity<String> createProduct(Product product) {
        // Validate input parameters
        if (product == null || product.getProductName() == null) {
            return ResponseEntity.badRequest().body("Invalid product data");
        }

        // Get current broker for multi-tenant isolation
        Broker currentBroker = tenantContextService.getCurrentBroker();
        Long currentBrokerId = currentBroker.getBrokerId();

        // Set the broker for multi-tenant isolation
        product.setBroker(currentBroker);

        // Check if a product with the same name, quality, and quantity already exists for this broker
        List<Product> existingProducts = productRepository.findByBrokerBrokerIdAndProductNameAndQualityAndQuantity(
            currentBrokerId, product.getProductName(), product.getQuality(), product.getQuantity());

        if (!existingProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Product with the same name, quality, and quantity already exists");
        }

        // Save the product
        productRepository.save(product);

        // Clear product caches after creating new product
        productCacheService.clearProductCaches();

        return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");
    }

    public Object updateProduct(Product product) {
        boolean productExists = productRepository.findById(product.getProductId()).isPresent();
        if(productExists) {
            Product updatedProduct = productRepository.save(product);

            // Clear product caches after updating product
            productCacheService.clearProductCaches();

            return updatedProduct;
        }
        return HttpStatus.NOT_FOUND;
    }

    public ResponseEntity deleteProduct(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()) {
            productRepository.deleteById(productId);

            // Clear product caches after deleting product
            productCacheService.clearProductCaches();

            return ResponseEntity.status(HttpStatus.OK).body("product deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no product found to delete");
    }

    public List<Product> getAllProducts(Pageable pageable) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        List<Product> allProducts = productRepository.findByBrokerBrokerId(currentBrokerId);

        // Apply pagination manually since we're using a custom query
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allProducts.size());

        if (start >= allProducts.size()) {
            return null;
        }

        List<Product> paginatedProducts = allProducts.subList(start, end);
        if(paginatedProducts.size()>=1){
            return paginatedProducts;
        }
        return null;
    }

    public List<Product> getAllProductsByName(String productName){
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        List<Product> products = productRepository.findByBrokerBrokerIdAndProductName(currentBrokerId, productName);
        if(products.size()>=1){
            return products;
        }
        return null;
    }

    // ==================== OPTIMIZED CACHED METHODS ====================

    @Override
    public List<String> getProductNames() {
        // Optimized: Use cache service with Redis caching (1 hour TTL)
        // This fetches only product names from database instead of full Product entities
        return productCacheService.getAllProductNames();
    }

    @Override
    public List<String> getDistinctProductNames() {
        // Optimized: Use cache service to get distinct product names
        return productCacheService.getDistinctProductNames();
    }

    @Override
    public List<HashMap<String, Long>> getProductNamesAndIds() {
        // Optimized: Use cache service instead of fetching all products
        return productCacheService.getProductNamesAndIds();
    }

    @Override
    public List<ProductBasicInfoDTO> getBasicProductInfo() {
        // Optimized: Use cache service to get basic product information
        return productCacheService.getAllBasicProductInfo();
    }

    @Override
    public List<ProductBasicInfoDTO> getProductNamesAndQualities() {
        // Optimized: Use cache service to get product names and qualities
        return productCacheService.getProductNamesAndQualities();
    }

    @Override
    public List<HashMap<String, Long>> getProductNamesAndQualitiesAndQuantitiesWithIds() {
        // Optimized: Use cache service to get product names, qualities, quantities with IDs
        return productCacheService.getProductNamesAndQualitiesAndQuantitiesWithIds();
    }

    @Override
    public BulkUploadResponseDTO bulkUploadProducts(MultipartFile file) {
        List<String> errorMessages = new ArrayList<>();
        int totalRecords = 0;
        int successfulRecords = 0;
        int failedRecords = 0;

        try {
            // Validate file format
            if (!ProductExcelUtil.hasExcelFormat(file)) {
                return BulkUploadResponseDTO.builder()
                        .totalRecords(0)
                        .successfulRecords(0)
                        .failedRecords(0)
                        .errorMessages(Arrays.asList("Please upload a valid Excel file (.xlsx)"))
                        .message("Invalid file format")
                        .build();
            }

            // Parse Excel file
            List<ProductDTO> productDTOs = ProductExcelUtil.excelToProductDTOs(file.getInputStream());
            totalRecords = productDTOs.size();

            if (totalRecords == 0) {
                return BulkUploadResponseDTO.builder()
                        .totalRecords(0)
                        .successfulRecords(0)
                        .failedRecords(0)
                        .errorMessages(Arrays.asList("No valid product records found in the Excel file"))
                        .message("No data to process")
                        .build();
            }

            // Get current broker
            Broker currentBroker = tenantContextService.getCurrentBroker();

            // Process each product
            for (int i = 0; i < productDTOs.size(); i++) {
                ProductDTO productDTO = productDTOs.get(i);
                int rowNumber = i + 2; // +2 because Excel rows start from 1 and we skip header

                try {
                    // Only validate product name as required
                    if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()) {
                        errorMessages.add("Row " + rowNumber + ": Product name is required");
                        failedRecords++;
                        continue;
                    }

                    // Create product entity
                    Product product = Product.builder()
                            .broker(currentBroker)
                            .productName(productDTO.getProductName().trim())
                            .productBrokerage(productDTO.getProductBrokerage() != null ? productDTO.getProductBrokerage() : 0.0f)
                            .quantity(productDTO.getQuantity() != null ? productDTO.getQuantity() : 0)
                            .price(productDTO.getPrice() != null ? productDTO.getPrice() : 0)
                            .quality(productDTO.getQuality() != null ? productDTO.getQuality().trim() : null)
                            .imgLink(productDTO.getImgLink() != null ? productDTO.getImgLink().trim() : null)
                            .build();

                    // Save product (duplicates are allowed for products)
                    productRepository.save(product);
                    successfulRecords++;

                } catch (Exception e) {
                    errorMessages.add("Row " + rowNumber + ": Error processing product - " + e.getMessage());
                    failedRecords++;
                }
            }

        } catch (IOException e) {
            return BulkUploadResponseDTO.builder()
                    .totalRecords(0)
                    .successfulRecords(0)
                    .failedRecords(0)
                    .errorMessages(Arrays.asList("Error reading Excel file: " + e.getMessage()))
                    .message("File processing failed")
                    .build();
        } catch (Exception e) {
            return BulkUploadResponseDTO.builder()
                    .totalRecords(totalRecords)
                    .successfulRecords(successfulRecords)
                    .failedRecords(failedRecords)
                    .errorMessages(Arrays.asList("Unexpected error: " + e.getMessage()))
                    .message("Bulk upload failed")
                    .build();
        }

        // Clear product caches if any products were successfully created
        if (successfulRecords > 0) {
            productCacheService.clearProductCaches();
        }

        // Prepare response message
        String message;
        if (failedRecords == 0) {
            message = "All products uploaded successfully";
        } else if (successfulRecords == 0) {
            message = "No products were uploaded";
        } else {
            message = "Partial success: " + successfulRecords + " products uploaded, " + failedRecords + " failed";
        }

        return BulkUploadResponseDTO.builder()
                .totalRecords(totalRecords)
                .successfulRecords(successfulRecords)
                .failedRecords(failedRecords)
                .errorMessages(errorMessages)
                .message(message)
                .build();
    }
}

