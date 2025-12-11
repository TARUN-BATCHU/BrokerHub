package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ApiResponse;
import com.brokerhub.brokerageapp.dto.dashboard.*;
import com.brokerhub.brokerageapp.entity.PaymentMethod;
import com.brokerhub.brokerageapp.service.BrokerageDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/brokerage-dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BrokerageDashboardController {

    private final BrokerageDashboardService dashboardService;

    @GetMapping("/{brokerId}")
    public ResponseEntity<ApiResponse<BrokerageDashboardDTO>> getDashboard(@PathVariable Long brokerId) {
        BrokerageDashboardDTO dashboard = dashboardService.getBrokerageDashboard(brokerId);
        return ResponseEntity.ok(ApiResponse.<BrokerageDashboardDTO>builder()
                .success(true)
                .message("Dashboard data retrieved successfully")
                .data(dashboard)
                .build());
    }

    @GetMapping("/{brokerId}/merchants")
    public ResponseEntity<ApiResponse<List<MerchantBrokerageDTO>>> getMerchantsBrokerage(@PathVariable Long brokerId) {
        List<MerchantBrokerageDTO> merchants = dashboardService.getMerchantsBrokerage(brokerId);
        return ResponseEntity.ok(ApiResponse.<List<MerchantBrokerageDTO>>builder()
                .success(true)
                .message("Merchants brokerage data retrieved successfully")
                .data(merchants)
                .build());
    }

    @PutMapping("/{brokerId}/payment-status")
    public ResponseEntity<ApiResponse<String>> updatePaymentStatus(
            @PathVariable Long brokerId,
            @Valid @RequestBody UpdatePaymentStatusRequestDTO request) {
        dashboardService.updatePaymentStatus(brokerId, request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Payment status updated successfully")
                .build());
    }

    @PutMapping("/{brokerId}/brokerage-amount")
    public ResponseEntity<ApiResponse<String>> updateBrokerageAmount(
            @PathVariable Long brokerId,
            @Valid @RequestBody UpdateBrokerageRequestDTO request) {
        dashboardService.updateBrokerageAmount(brokerId, request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Brokerage amount updated successfully")
                .build());
    }

    @GetMapping("/{brokerId}/merchant/{merchantId}/history")
    public ResponseEntity<ApiResponse<List<MerchantBrokerageDTO.PaymentHistoryDTO>>> getPaymentHistory(
            @PathVariable Long brokerId,
            @PathVariable Long merchantId) {
        List<MerchantBrokerageDTO.PaymentHistoryDTO> history = dashboardService.getPaymentHistory(brokerId, merchantId);
        return ResponseEntity.ok(ApiResponse.<List<MerchantBrokerageDTO.PaymentHistoryDTO>>builder()
                .success(true)
                .message("Payment history retrieved successfully")
                .data(history)
                .build());
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<ApiResponse<List<PaymentMethod>>> getPaymentMethods() {
        List<PaymentMethod> methods = List.of(PaymentMethod.values());
        return ResponseEntity.ok(ApiResponse.<List<PaymentMethod>>builder()
                .success(true)
                .message("Payment methods retrieved successfully")
                .data(methods)
                .build());
    }

    @PostMapping("/{brokerId}/calculate-brokerage")
    public ResponseEntity<ApiResponse<String>> calculateBrokerage(@PathVariable Long brokerId) {
        dashboardService.calculateBrokerageForBroker(brokerId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Brokerage calculation completed successfully")
                .build());
    }

    @GetMapping("/{brokerId}/city/{city}/analytics")
    public ResponseEntity<ApiResponse<CityBrokerageAnalyticsDTO>> getCityAnalytics(
            @PathVariable Long brokerId,
            @PathVariable String city) {
        CityBrokerageAnalyticsDTO analytics = dashboardService.getCityBrokerageAnalytics(brokerId, city);
        return ResponseEntity.ok(ApiResponse.<CityBrokerageAnalyticsDTO>builder()
                .success(true)
                .message("City analytics retrieved successfully")
                .data(analytics)
                .build());
    }
}