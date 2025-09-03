package com.brokerhub.brokerageapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/network-test")
public class NetworkTestController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Backend is accessible");
        response.put("timestamp", LocalDateTime.now());
        response.put("clientIP", getClientIP(request));
        response.put("serverTime", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Echo successful");
        response.put("receivedPayload", payload);
        response.put("clientIP", getClientIP(request));
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}