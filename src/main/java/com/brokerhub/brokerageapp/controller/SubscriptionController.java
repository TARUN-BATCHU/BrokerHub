package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ApiResponse;
import com.brokerhub.brokerageapp.dto.subscription.*;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.exception.ResourceNotFoundException;
import com.brokerhub.brokerageapp.security.SecurityContextUtil;
import com.brokerhub.brokerageapp.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BrokerHub/api")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SecurityContextUtil securityContextUtil;

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllActivePlans() {
        List<SubscriptionPlanDTO> plans = subscriptionService.getAllActivePlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/subscriptions/current")
    public ResponseEntity<?> getCurrentSubscription() {
        // This endpoint requires authentication - broker must be logged in
        return ResponseEntity.status(401).body(new SubscriptionErrorDTO("AUTHENTICATION_REQUIRED", "Broker authentication required"));
    }

    @PostMapping("/subscriptions/request")
    public ResponseEntity<ApiResponse> requestSubscription(@Valid @RequestBody SubscriptionRequestDTO request) {
        // This endpoint requires authentication - broker must be logged in  
        return ResponseEntity.status(401).body(new ApiResponse("error", "Broker authentication required", null));
    }
}