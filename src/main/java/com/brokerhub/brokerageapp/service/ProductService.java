package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.ProductBasicInfoDTO;
import com.brokerhub.brokerageapp.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

public interface ProductService {
    ResponseEntity<String> createProduct(Product product);

    Object updateProduct(Product product);

    ResponseEntity deleteProduct(Long productId);

    List<Product> getAllProducts(Pageable pageable);

    List<Product> getAllProductsByName(String productName);

    // Optimized methods using cache
    List<String> getProductNames();

    List<String> getDistinctProductNames();

    List<HashMap<String, Long>> getProductNamesAndIds();

    List<ProductBasicInfoDTO> getBasicProductInfo();

    List<ProductBasicInfoDTO> getProductNamesAndQualities();

    List<HashMap<String, Long>> getProductNamesAndQualitiesAndQuantitiesWithIds();
}
