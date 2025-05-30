package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.payments.*;
import com.brokerhub.brokerageapp.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for payment-related operations.
 * Provides endpoints for managing brokerage payments, pending payments, and receivable payments.
 * 
 * Base URL: /BrokerHub/payments
 * 
 * Authentication: Basic Authentication required
 * Username: tarun
 * Password: securePassword123
 */
@RestController
@RequestMapping("/BrokerHub/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ==================== FIRM NAMES API ====================

    /**
     * Get All Firm Names (for search dropdown)
     * 
     * GET /BrokerHub/payments/firms
     * 
     * @return List of unique firm names across all payment types
     */
    @GetMapping("/firms")
    public ResponseEntity<ApiResponseDTO<List<String>>> getAllFirmNames() {
        try {
            log.info("Request received: GET /BrokerHub/payments/firms");
            
            // For demo purposes, using a default broker ID
            // In production, this should come from authentication context
            Long brokerId = 1L;
            
            return paymentService.getAllFirmNames(brokerId);
            
        } catch (Exception e) {
            log.error("Error in getAllFirmNames", e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve firms", "Internal server error"));
        }
    }

    // ==================== BROKERAGE PAYMENTS APIs ====================

    /**
     * Get All Brokerage Payments
     * 
     * GET /BrokerHub/payments/{brokerId}/brokerage
     * 
     * @param brokerId Broker ID
     * @return List of brokerage payments for the broker
     */
    @GetMapping("/{brokerId}/brokerage")
    public ResponseEntity<ApiResponseDTO<List<BrokeragePaymentDTO>>> getAllBrokeragePayments(
            @PathVariable Long brokerId) {
        try {
            log.info("Request received: GET /BrokerHub/payments/{}/brokerage", brokerId);
            
            return paymentService.getAllBrokeragePayments(brokerId);
            
        } catch (Exception e) {
            log.error("Error in getAllBrokeragePayments for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve brokerage payments", "Internal server error"));
        }
    }

    /**
     * Search Brokerage Payments by Firm
     * 
     * GET /BrokerHub/payments/{brokerId}/brokerage/search?firmName={firmName}
     * 
     * @param brokerId Broker ID
     * @param firmName Firm name to search for (case-insensitive partial match)
     * @return Filtered list of brokerage payments
     */
    @GetMapping("/{brokerId}/brokerage/search")
    public ResponseEntity<ApiResponseDTO<List<BrokeragePaymentDTO>>> searchBrokeragePaymentsByFirm(
            @PathVariable Long brokerId,
            @RequestParam String firmName) {
        try {
            log.info("Request received: GET /BrokerHub/payments/{}/brokerage/search?firmName={}", 
                    brokerId, firmName);
            
            return paymentService.searchBrokeragePaymentsByFirm(brokerId, firmName);
            
        } catch (Exception e) {
            log.error("Error in searchBrokeragePaymentsByFirm for broker: {}, firmName: {}", 
                    brokerId, firmName, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve brokerage payments", "Internal server error"));
        }
    }

    /**
     * Add Part Payment to Brokerage
     * 
     * POST /BrokerHub/payments/{brokerId}/brokerage/{paymentId}/part-payment
     * 
     * @param brokerId Broker ID
     * @param paymentId Brokerage payment ID
     * @param request Part payment request details
     * @return Updated payment information
     */
    @PostMapping("/{brokerId}/brokerage/{paymentId}/part-payment")
    public ResponseEntity<ApiResponseDTO<AddPartPaymentResponseDTO>> addPartPayment(
            @PathVariable Long brokerId,
            @PathVariable Long paymentId,
            @Valid @RequestBody AddPartPaymentRequestDTO request) {
        try {
            log.info("Request received: POST /BrokerHub/payments/{}/brokerage/{}/part-payment with amount: {}", 
                    brokerId, paymentId, request.getFormattedAmount());
            
            return paymentService.addPartPayment(brokerId, paymentId, request);
            
        } catch (Exception e) {
            log.error("Error in addPartPayment for broker: {}, payment: {}", brokerId, paymentId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to add part payment", "Internal server error"));
        }
    }

    // ==================== PENDING PAYMENTS APIs ====================

    /**
     * Get All Pending Payments
     * 
     * GET /BrokerHub/payments/{brokerId}/pending
     * 
     * @param brokerId Broker ID
     * @return List of pending payments for the broker
     */
    @GetMapping("/{brokerId}/pending")
    public ResponseEntity<ApiResponseDTO<List<PendingPaymentDTO>>> getAllPendingPayments(
            @PathVariable Long brokerId) {
        try {
            log.info("Request received: GET /BrokerHub/payments/{}/pending", brokerId);
            
            return paymentService.getAllPendingPayments(brokerId);
            
        } catch (Exception e) {
            log.error("Error in getAllPendingPayments for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve pending payments", "Internal server error"));
        }
    }

    /**
     * Search Pending Payments by Buyer Firm
     * 
     * GET /BrokerHub/payments/{brokerId}/pending/search?buyerFirm={buyerFirm}
     * 
     * @param brokerId Broker ID
     * @param buyerFirm Buyer firm name to search for (case-insensitive partial match)
     * @return Filtered list of pending payments
     */
    @GetMapping("/{brokerId}/pending/search")
    public ResponseEntity<ApiResponseDTO<List<PendingPaymentDTO>>> searchPendingPaymentsByBuyerFirm(
            @PathVariable Long brokerId,
            @RequestParam String buyerFirm) {
        try {
            log.info("Request received: GET /BrokerHub/payments/{}/pending/search?buyerFirm={}", 
                    brokerId, buyerFirm);
            
            return paymentService.searchPendingPaymentsByBuyerFirm(brokerId, buyerFirm);
            
        } catch (Exception e) {
            log.error("Error in searchPendingPaymentsByBuyerFirm for broker: {}, buyerFirm: {}", 
                    brokerId, buyerFirm, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve pending payments", "Internal server error"));
        }
    }

    // ==================== RECEIVABLE PAYMENTS APIs ====================

    /**
     * Get All Receivable Payments
     * 
     * GET /BrokerHub/payments/{brokerId}/receivable
     * 
     * @param brokerId Broker ID
     * @return List of receivable payments for the broker
     */
    @GetMapping("/{brokerId}/receivable")
    public ResponseEntity<ApiResponseDTO<List<ReceivablePaymentDTO>>> getAllReceivablePayments(
            @PathVariable Long brokerId) {
        try {
            log.info("Request received: GET /BrokerHub/payments/{}/receivable", brokerId);
            
            return paymentService.getAllReceivablePayments(brokerId);
            
        } catch (Exception e) {
            log.error("Error in getAllReceivablePayments for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve receivable payments", "Internal server error"));
        }
    }

    /**
     * Search Receivable Payments by Seller Firm
     * 
     * GET /BrokerHub/payments/{brokerId}/receivable/search?sellerFirm={sellerFirm}
     * 
     * @param brokerId Broker ID
     * @param sellerFirm Seller firm name to search for (case-insensitive partial match)
     * @return Filtered list of receivable payments
     */
    @GetMapping("/{brokerId}/receivable/search")
    public ResponseEntity<ApiResponseDTO<List<ReceivablePaymentDTO>>> searchReceivablePaymentsBySellerFirm(
            @PathVariable Long brokerId,
            @RequestParam String sellerFirm) {
        try {
            log.info("Request received: GET /BrokerHub/payments/{}/receivable/search?sellerFirm={}", 
                    brokerId, sellerFirm);
            
            return paymentService.searchReceivablePaymentsBySellerFirm(brokerId, sellerFirm);
            
        } catch (Exception e) {
            log.error("Error in searchReceivablePaymentsBySellerFirm for broker: {}, sellerFirm: {}", 
                    brokerId, sellerFirm, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve receivable payments", "Internal server error"));
        }
    }

    // ==================== UTILITY ENDPOINTS ====================

    /**
     * Refresh Payment Cache
     * 
     * POST /BrokerHub/payments/{brokerId}/refresh-cache
     * 
     * @param brokerId Broker ID
     * @return Success message
     */
    @PostMapping("/{brokerId}/refresh-cache")
    public ResponseEntity<ApiResponseDTO<String>> refreshPaymentCache(@PathVariable Long brokerId) {
        try {
            log.info("Request received: POST /BrokerHub/payments/{}/refresh-cache", brokerId);
            
            paymentService.refreshPaymentCache(brokerId);
            
            return ResponseEntity.ok(ApiResponseDTO.success("Payment cache refreshed successfully"));
            
        } catch (Exception e) {
            log.error("Error in refreshPaymentCache for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to refresh cache", "Internal server error"));
        }
    }

    /**
     * Update Overdue Payment Statuses
     * 
     * POST /BrokerHub/payments/{brokerId}/update-overdue-status
     * 
     * @param brokerId Broker ID
     * @return Number of payments updated
     */
    @PostMapping("/{brokerId}/update-overdue-status")
    public ResponseEntity<ApiResponseDTO<String>> updateOverduePaymentStatuses(@PathVariable Long brokerId) {
        try {
            log.info("Request received: POST /BrokerHub/payments/{}/update-overdue-status", brokerId);
            
            int updatedCount = paymentService.updateOverduePaymentStatuses(brokerId);
            
            return ResponseEntity.ok(ApiResponseDTO.success(
                    String.format("Updated %d overdue payment statuses", updatedCount)));
            
        } catch (Exception e) {
            log.error("Error in updateOverduePaymentStatuses for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to update overdue statuses", "Internal server error"));
        }
    }

    /**
     * Get Payment Dashboard Statistics
     * 
     * GET /BrokerHub/payments/{brokerId}/dashboard
     * 
     * @param brokerId Broker ID
     * @return Payment dashboard statistics
     */
    @GetMapping("/{brokerId}/dashboard")
    public ResponseEntity<ApiResponseDTO<PaymentDashboardDTO>> getPaymentDashboardStatistics(
            @PathVariable Long brokerId) {
        try {
            log.info("Request received: GET /BrokerHub/payments/{}/dashboard", brokerId);
            
            return paymentService.getPaymentDashboardStatistics(brokerId);
            
        } catch (Exception e) {
            log.error("Error in getPaymentDashboardStatistics for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve dashboard statistics", "Internal server error"));
        }
    }

    /**
     * Get Payment Summary by Status
     * 
     * GET /BrokerHub/payments/{brokerId}/summary
     * 
     * @param brokerId Broker ID
     * @return Payment summary grouped by status
     */
    @GetMapping("/{brokerId}/summary")
    public ResponseEntity<ApiResponseDTO<PaymentSummaryDTO>> getPaymentSummaryByStatus(
            @PathVariable Long brokerId) {
        try {
            log.info("Request received: GET /BrokerHub/payments/{}/summary", brokerId);
            
            return paymentService.getPaymentSummaryByStatus(brokerId);
            
        } catch (Exception e) {
            log.error("Error in getPaymentSummaryByStatus for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve payment summary", "Internal server error"));
        }
    }

    /**
     * Generate Payment Data from Ledger
     * 
     * POST /BrokerHub/payments/{brokerId}/generate-from-ledger/{financialYearId}
     * 
     * @param brokerId Broker ID
     * @param financialYearId Financial year ID
     * @return Success message
     */
    @PostMapping("/{brokerId}/generate-from-ledger/{financialYearId}")
    public ResponseEntity<ApiResponseDTO<String>> generatePaymentDataFromLedger(
            @PathVariable Long brokerId,
            @PathVariable Long financialYearId) {
        try {
            log.info("Request received: POST /BrokerHub/payments/{}/generate-from-ledger/{}", 
                    brokerId, financialYearId);
            
            return paymentService.generatePaymentDataFromLedger(brokerId, financialYearId);
            
        } catch (Exception e) {
            log.error("Error in generatePaymentDataFromLedger for broker: {}, financialYear: {}", 
                    brokerId, financialYearId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to generate payment data", "Internal server error"));
        }
    }
}
