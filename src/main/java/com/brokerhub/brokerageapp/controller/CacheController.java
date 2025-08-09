package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ApiResponse;
import com.brokerhub.brokerageapp.service.BrokerageCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/BrokerHub/Cache")
@Slf4j
public class CacheController {
    
    @Autowired
    private BrokerageCacheService brokerageCacheService;
    
    @DeleteMapping("/brokerage")
    public ResponseEntity<ApiResponse<String>> clearBrokerageCache() {
        try {
            brokerageCacheService.evictAllBrokerageCache();
            return ResponseEntity.ok(ApiResponse.success("Cache cleared successfully", "All brokerage cache cleared"));
        } catch (Exception e) {
            log.error("Error clearing brokerage cache", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to clear cache: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/brokerage/user/{userId}")
    public ResponseEntity<ApiResponse<String>> clearUserBrokerageCache(@PathVariable Long userId) {
        try {
            brokerageCacheService.evictUserBrokerageCache(userId);
            return ResponseEntity.ok(ApiResponse.success("User cache cleared successfully", "User brokerage cache cleared"));
        } catch (Exception e) {
            log.error("Error clearing user brokerage cache", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to clear user cache: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/brokerage/city/{city}")
    public ResponseEntity<ApiResponse<String>> clearCityBrokerageCache(@PathVariable String city) {
        try {
            brokerageCacheService.evictCityBrokerageCache(city);
            return ResponseEntity.ok(ApiResponse.success("City cache cleared successfully", "City brokerage cache cleared"));
        } catch (Exception e) {
            log.error("Error clearing city brokerage cache", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to clear city cache: " + e.getMessage()));
        }
    }
}