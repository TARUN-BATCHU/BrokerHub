package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.ProductBasicInfoDTO;
import com.brokerhub.brokerageapp.entity.Product;
import com.brokerhub.brokerageapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCacheService productCacheService;


    public ResponseEntity<String> createProduct(Product product) {
        // Validate input parameters
        if (product == null || product.getProductName() == null) {
            return ResponseEntity.badRequest().body("Invalid product data");
        }

        // Check if a product with the same name, quality, and quantity already exists
        List<Product> existingProducts = productRepository.findByProductNameAndQualityAndQuantity(product.getProductName(), product.getQuality(), product.getQuantity());

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
        List<Product> products = productRepository.findAll(pageable).getContent();
        if(products.size()>=1){
            return products;
        }
        return null;
    }

    public List<Product> getAllProductsByName(String productName){
        List<Product> products = productRepository.findByProductName(productName);
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
}

