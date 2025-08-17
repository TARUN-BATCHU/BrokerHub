package com.market.controller;

import com.market.entity.MarketProduct;
import com.market.entity.SellerRequest;
import com.market.entity.BuyerRequest;
import com.market.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
public class MarketController {
    @Autowired
    private MarketService marketService;

    // Market Product Endpoints
    @GetMapping("/products")
    public ResponseEntity<?> getMarketProducts() {
        try {
            List<MarketProduct> products = marketService.getAllProducts();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/broker/{brokerId}/products")
    public ResponseEntity<?> getBrokerProducts(@PathVariable Long brokerId) {
        try {
            List<MarketProduct> products = marketService.getBrokerProducts(brokerId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> addMarketProduct(@RequestBody MarketProduct product) {
        try {
            MarketProduct savedProduct = marketService.addProduct(product);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Product added successfully");
            response.put("data", savedProduct);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Seller Request Endpoints
    @GetMapping("/seller-requests")
    public ResponseEntity<?> getSellerRequests(@RequestParam Long brokerId) {
        try {
            List<SellerRequest> requests = marketService.getSellerRequests(brokerId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", requests);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/seller-requests")
    public ResponseEntity<?> submitSellerRequest(@RequestBody SellerRequest request) {
        try {
            SellerRequest savedRequest = marketService.submitSellerRequest(request);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Seller request submitted successfully");
            response.put("data", savedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/seller-requests/{requestId}/accept")
    public ResponseEntity<?> acceptSellerRequest(@PathVariable Long requestId) {
        try {
            SellerRequest acceptedRequest = marketService.acceptSellerRequest(requestId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Seller request accepted successfully");
            response.put("data", acceptedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Buyer Request Endpoints
    @GetMapping("/buyer-requests")
    public ResponseEntity<?> getBuyerRequests(@RequestParam Long productId) {
        try {
            List<BuyerRequest> requests = marketService.getBuyerRequests(productId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", requests);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/buyer-requests")
    public ResponseEntity<?> submitBuyerRequest(@RequestBody BuyerRequest request) {
        try {
            BuyerRequest savedRequest = marketService.submitBuyerRequest(request);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Buyer request submitted successfully");
            response.put("data", savedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/buyer-requests/{requestId}/accept")
    public ResponseEntity<?> acceptBuyerRequest(@PathVariable Long requestId) {
        try {
            BuyerRequest acceptedRequest = marketService.acceptBuyerRequest(requestId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Buyer request accepted successfully");
            response.put("data", acceptedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}