package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.entity.Product;
import com.brokerhub.brokerageapp.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
