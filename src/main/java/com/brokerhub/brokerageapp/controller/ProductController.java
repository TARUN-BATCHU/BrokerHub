package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ProductBasicInfoDTO;
import com.brokerhub.brokerageapp.entity.Product;
import com.brokerhub.brokerageapp.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/BrokerHub/Product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/createProduct")
    public ResponseEntity createProduct(@Valid @RequestBody Product product){
        return productService.createProduct(product);
    }

    @PutMapping("/updateProduct")
    public Object updateProduct(@Valid @RequestBody Product product){
        return productService.updateProduct(product);
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity deleteProduct(@RequestParam Long productId){
        return productService.deleteProduct(productId);
    }

    @GetMapping("/allProducts")
    public List<Product> getAllProducts(Pageable pageable){
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/allProducts/")
    public List<Product> getAllProductsByName(@RequestParam String productName){
        return productService.getAllProductsByName(productName);
    }

    // ==================== OPTIMIZED CACHED ENDPOINTS ====================

    @GetMapping("/getProductNames")
    public ResponseEntity<List<String>> getProductNames() {
        List<String> productNames = productService.getProductNames();
        return ResponseEntity.ok(productNames);
    }

    @GetMapping("/getDistinctProductNames")
    public ResponseEntity<List<String>> getDistinctProductNames() {
        List<String> distinctProductNames = productService.getDistinctProductNames();
        return ResponseEntity.ok(distinctProductNames);
    }

    @GetMapping("/getProductNamesAndIds")
    public ResponseEntity<List<HashMap<String, Long>>> getProductNamesAndIds() {
        List<HashMap<String, Long>> productNamesAndIds = productService.getProductNamesAndIds();
        return ResponseEntity.ok(productNamesAndIds);
    }

    @GetMapping("/getBasicProductInfo")
    public ResponseEntity<List<ProductBasicInfoDTO>> getBasicProductInfo() {
        List<ProductBasicInfoDTO> basicProductInfo = productService.getBasicProductInfo();
        return ResponseEntity.ok(basicProductInfo);
    }

    @GetMapping("/getProductNamesAndQualities")
    public ResponseEntity<List<ProductBasicInfoDTO>> getProductNamesAndQualities() {
        List<ProductBasicInfoDTO> productNamesAndQualities = productService.getProductNamesAndQualities();
        return ResponseEntity.ok(productNamesAndQualities);
    }

    @GetMapping("/getProductNamesAndQualitiesAndQuantitesWithId")
    public ResponseEntity<List<Map<String,Long>>> getProductNamesAndQualitiesAndQuantitesWithId() {
        List<HashMap<String, Long>> productInfo = productService.getProductNamesAndQualitiesAndQuantitiesWithIds();
        return ResponseEntity.ok(productInfo.stream()
                .map(hashMap -> (Map<String, Long>) hashMap)
                .collect(java.util.stream.Collectors.toList()));
    }
}
