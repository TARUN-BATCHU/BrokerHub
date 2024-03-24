package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    ResponseEntity<String> createProduct(Product product);

    Object updateProduct(Product product);

    ResponseEntity deleteProduct(Long prductId);

    List<Product> getAllProducts(Pageable pageable);

    public List<Product> getAllProductsByName(String productName);
}
