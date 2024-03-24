package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Product;
import com.brokerhub.brokerageapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;


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

        return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");
    }

    public Object updateProduct(Product product) {
        boolean productExists = productRepository.findById(product.getProductId()).isPresent();
        if(productExists) {
            return productRepository.save(product);
        }
        return HttpStatus.NOT_FOUND;
    }

    public ResponseEntity deleteProduct(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()) {
            productRepository.deleteById(productId);
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
}

