package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.payments.*;
import com.brokerhub.brokerageapp.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BrokerHub/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/firms")
    public ResponseEntity<ApiResponseDTO<List<String>>> getAllFirmNames() {
        try {
            Long brokerId = 1L;
            return paymentService.getAllFirmNames(brokerId);
        } catch (Exception e) {
            log.error("Error in getAllFirmNames", e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve firms", "Internal server error"));
        }
    }

    @GetMapping("/{brokerId}/brokerage")
    public ResponseEntity<ApiResponseDTO<List<BrokeragePaymentDTO>>> getAllBrokeragePayments(
            @PathVariable Long brokerId) {
        try {
            return paymentService.getAllBrokeragePayments(brokerId);
        } catch (Exception e) {
            log.error("Error in getAllBrokeragePayments for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve brokerage payments", "Internal server error"));
        }
    }

    @GetMapping("/{brokerId}/brokerage/search")
    public ResponseEntity<ApiResponseDTO<List<BrokeragePaymentDTO>>> searchBrokeragePaymentsByFirm(
            @PathVariable Long brokerId,
            @RequestParam String firmName) {
        try {
            return paymentService.searchBrokeragePaymentsByFirm(brokerId, firmName);
        } catch (Exception e) {
            log.error("Error in searchBrokeragePaymentsByFirm for broker: {}, firmName: {}", 
                    brokerId, firmName, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve brokerage payments", "Internal server error"));
        }
    }

    @PostMapping("/{brokerId}/brokerage/{paymentId}/part-payment")
    public ResponseEntity<ApiResponseDTO<AddPartPaymentResponseDTO>> addPartPayment(
            @PathVariable Long brokerId,
            @PathVariable Long paymentId,
            @Valid @RequestBody AddPartPaymentRequestDTO request) {
        try {
            return paymentService.addPartPayment(brokerId, paymentId, request);
        } catch (Exception e) {
            log.error("Error in addPartPayment for broker: {}, payment: {}", brokerId, paymentId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to add part payment", "Internal server error"));
        }
    }

    @GetMapping("/{brokerId}/pending")
    public ResponseEntity<ApiResponseDTO<List<PendingPaymentDTO>>> getAllPendingPayments(
            @PathVariable Long brokerId) {
        try {
            return paymentService.getAllPendingPayments(brokerId);
        } catch (Exception e) {
            log.error("Error in getAllPendingPayments for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve pending payments", "Internal server error"));
        }
    }

    @GetMapping("/{brokerId}/pending/search")
    public ResponseEntity<ApiResponseDTO<List<PendingPaymentDTO>>> searchPendingPaymentsByBuyerFirm(
            @PathVariable Long brokerId,
            @RequestParam String buyerFirm) {
        try {
            return paymentService.searchPendingPaymentsByBuyerFirm(brokerId, buyerFirm);
        } catch (Exception e) {
            log.error("Error in searchPendingPaymentsByBuyerFirm for broker: {}, buyerFirm: {}", 
                    brokerId, buyerFirm, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve pending payments", "Internal server error"));
        }
    }

    @GetMapping("/{brokerId}/receivable")
    public ResponseEntity<ApiResponseDTO<List<ReceivablePaymentDTO>>> getAllReceivablePayments(
            @PathVariable Long brokerId) {
        try {
            return paymentService.getAllReceivablePayments(brokerId);
        } catch (Exception e) {
            log.error("Error in getAllReceivablePayments for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve receivable payments", "Internal server error"));
        }
    }

    @GetMapping("/{brokerId}/receivable/search")
    public ResponseEntity<ApiResponseDTO<List<ReceivablePaymentDTO>>> searchReceivablePaymentsBySellerFirm(
            @PathVariable Long brokerId,
            @RequestParam String sellerFirm) {
        try {
            return paymentService.searchReceivablePaymentsBySellerFirm(brokerId, sellerFirm);
        } catch (Exception e) {
            log.error("Error in searchReceivablePaymentsBySellerFirm for broker: {}, sellerFirm: {}", 
                    brokerId, sellerFirm, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve receivable payments", "Internal server error"));
        }
    }

    @PostMapping("/{brokerId}/clear-cache")
    public ResponseEntity<ApiResponseDTO<String>> clearCache(@PathVariable Long brokerId) {
        try {
            paymentService.refreshPaymentCache(brokerId);
            return ResponseEntity.ok(ApiResponseDTO.success("Cache cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to clear cache", "Internal server error"));
        }
    }

    @GetMapping("/{brokerId}/dashboard")
    public ResponseEntity<ApiResponseDTO<PaymentDashboardDTO>> getPaymentDashboardStatistics(
            @PathVariable Long brokerId) {
        try {
            return paymentService.getPaymentDashboardStatistics(brokerId);
        } catch (Exception e) {
            log.error("Error in getPaymentDashboardStatistics for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve dashboard statistics", "Internal server error"));
        }
    }

    @GetMapping("/{brokerId}/summary")
    public ResponseEntity<ApiResponseDTO<PaymentSummaryDTO>> getPaymentSummaryByStatus(
            @PathVariable Long brokerId) {
        try {
            return paymentService.getPaymentSummaryByStatus(brokerId);
        } catch (Exception e) {
            log.error("Error in getPaymentSummaryByStatus for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve payment summary", "Internal server error"));
        }
    }
}