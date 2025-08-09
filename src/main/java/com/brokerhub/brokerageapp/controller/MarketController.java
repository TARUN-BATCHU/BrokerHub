package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.market.*;
import com.brokerhub.brokerageapp.service.market.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getMarketProducts() {
        List<MarketProductDTO> products = marketService.getMarketProducts();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "data", products
        ));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Map<String, Object>> addMarketProduct(
            @Valid @RequestBody MarketProductDTO productDTO) {
        MarketProductDTO savedProduct = marketService.addMarketProduct(productDTO);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Product added successfully",
            "data", savedProduct
        ));
    }

    @GetMapping("/seller-requests")
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Map<String, Object>> getSellerRequests() {
        List<SellerRequestDTO> requests = marketService.getSellerRequests();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "data", requests
        ));
    }

    @PostMapping("/seller-requests")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> submitSellerRequest(
            @Valid @RequestBody SellerRequestDTO requestDTO) {
        SellerRequestDTO savedRequest = marketService.submitSellerRequest(requestDTO);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Seller request submitted successfully",
            "data", Map.of(
                "requestId", savedRequest.getRequestId(),
                "status", savedRequest.getStatus()
            )
        ));
    }

    @PutMapping("/seller-requests/{requestId}/accept")
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Map<String, Object>> acceptSellerRequest(
            @PathVariable String requestId) {
        SellerRequestDTO acceptedRequest = marketService.acceptSellerRequest(requestId);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Seller request accepted successfully",
            "data", Map.of(
                "requestId", acceptedRequest.getRequestId(),
                "status", acceptedRequest.getStatus()
            )
        ));
    }

    @GetMapping("/buyer-requests")
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Map<String, Object>> getBuyerRequests() {
        List<BuyerRequestDTO> requests = marketService.getBuyerRequests();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "data", requests
        ));
    }

    @PostMapping("/buyer-requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Object>> submitBuyerRequest(
            @Valid @RequestBody BuyerRequestDTO requestDTO) {
        BuyerRequestDTO savedRequest = marketService.submitBuyerRequest(requestDTO);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Buyer request submitted successfully",
            "data", Map.of(
                "requestId", savedRequest.getRequestId(),
                "status", savedRequest.getStatus()
            )
        ));
    }

    @PutMapping("/buyer-requests/{requestId}/accept")
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Map<String, Object>> acceptBuyerRequest(
            @PathVariable String requestId) {
        BuyerRequestDTO acceptedRequest = marketService.acceptBuyerRequest(requestId);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Buyer request accepted successfully",
            "data", Map.of(
                "requestId", acceptedRequest.getRequestId(),
                "status", acceptedRequest.getStatus()
            )
        ));
    }
}