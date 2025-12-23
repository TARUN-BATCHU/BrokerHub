package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ApiResponse;
import com.brokerhub.brokerageapp.dto.subscription.ActivateSubscriptionDTO;
import com.brokerhub.brokerageapp.dto.subscription.SubscriptionErrorDTO;
import com.brokerhub.brokerageapp.exception.InvalidOperationException;
import com.brokerhub.brokerageapp.exception.ResourceNotFoundException;
import com.brokerhub.brokerageapp.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/activate")
    public ResponseEntity<?> activateSubscription(@Valid @RequestBody ActivateSubscriptionDTO request) {
        try {
            subscriptionService.activateSubscription(request);
            return ResponseEntity.ok(new ApiResponse("success", "Subscription activated successfully", null));
        } catch (InvalidOperationException e) {
            SubscriptionErrorDTO error = new SubscriptionErrorDTO("ACTIVE_SUBSCRIPTION_EXISTS", e.getMessage());
            return ResponseEntity.status(400).body(error);
        } catch (ResourceNotFoundException e) {
            SubscriptionErrorDTO error = new SubscriptionErrorDTO("RESOURCE_NOT_FOUND", e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    @PostMapping("/expire")
    public ResponseEntity<ApiResponse> expireSubscription(@RequestParam Long userId) {
        subscriptionService.expireSubscription(userId);
        return ResponseEntity.ok(new ApiResponse("success", "Subscription expired", null));
    }
}