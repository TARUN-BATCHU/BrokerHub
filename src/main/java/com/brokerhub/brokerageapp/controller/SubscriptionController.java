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
@RequestMapping("/api")
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
        try {
            User currentUser = securityContextUtil.getCurrentUser();
            CurrentSubscriptionDTO subscription = subscriptionService.getCurrentSubscription(currentUser);
            return ResponseEntity.ok(subscription);
        } catch (ResourceNotFoundException e) {
            SubscriptionErrorDTO error = new SubscriptionErrorDTO("NO_SUBSCRIPTION", e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    @PostMapping("/subscriptions/request")
    public ResponseEntity<ApiResponse> requestSubscription(@Valid @RequestBody SubscriptionRequestDTO request) {
        User currentUser = securityContextUtil.getCurrentUser();
        subscriptionService.requestSubscription(currentUser, request);
        return ResponseEntity.ok(new ApiResponse("success", "Subscription request submitted. Please complete payment.", null));
    }
}