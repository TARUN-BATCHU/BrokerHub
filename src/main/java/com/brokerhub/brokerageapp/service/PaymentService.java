package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.payments.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Service interface for payment-related operations.
 * Provides methods for managing brokerage payments, pending payments, and receivable payments.
 */
public interface PaymentService {

    // ==================== FIRM NAMES API ====================
    
    /**
     * Get all firm names for search dropdown
     * @param brokerId Broker ID
     * @return Response with list of firm names
     */
    ResponseEntity<ApiResponseDTO<List<String>>> getAllFirmNames(Long brokerId);

    // ==================== BROKERAGE PAYMENTS APIs ====================
    
    /**
     * Get all brokerage payments for a broker
     * @param brokerId Broker ID
     * @return Response with list of brokerage payments
     */
    ResponseEntity<ApiResponseDTO<List<BrokeragePaymentDTO>>> getAllBrokeragePayments(Long brokerId);

    /**
     * Search brokerage payments by firm name
     * @param brokerId Broker ID
     * @param firmName Firm name to search for
     * @return Response with filtered brokerage payments
     */
    ResponseEntity<ApiResponseDTO<List<BrokeragePaymentDTO>>> searchBrokeragePaymentsByFirm(Long brokerId, String firmName);

    /**
     * Add part payment to a brokerage payment
     * @param brokerId Broker ID
     * @param paymentId Brokerage payment ID
     * @param request Part payment request details
     * @return Response with updated payment information
     */
    ResponseEntity<ApiResponseDTO<AddPartPaymentResponseDTO>> addPartPayment(Long brokerId, Long paymentId, AddPartPaymentRequestDTO request);

    // ==================== PENDING PAYMENTS APIs ====================
    
    /**
     * Get all pending payments for a broker
     * @param brokerId Broker ID
     * @return Response with list of pending payments
     */
    ResponseEntity<ApiResponseDTO<List<PendingPaymentDTO>>> getAllPendingPayments(Long brokerId);

    /**
     * Search pending payments by buyer firm name
     * @param brokerId Broker ID
     * @param buyerFirm Buyer firm name to search for
     * @return Response with filtered pending payments
     */
    ResponseEntity<ApiResponseDTO<List<PendingPaymentDTO>>> searchPendingPaymentsByBuyerFirm(Long brokerId, String buyerFirm);

    // ==================== RECEIVABLE PAYMENTS APIs ====================
    
    /**
     * Get all receivable payments for a broker
     * @param brokerId Broker ID
     * @return Response with list of receivable payments
     */
    ResponseEntity<ApiResponseDTO<List<ReceivablePaymentDTO>>> getAllReceivablePayments(Long brokerId);

    /**
     * Search receivable payments by seller firm name
     * @param brokerId Broker ID
     * @param sellerFirm Seller firm name to search for
     * @return Response with filtered receivable payments
     */
    ResponseEntity<ApiResponseDTO<List<ReceivablePaymentDTO>>> searchReceivablePaymentsBySellerFirm(Long brokerId, String sellerFirm);

    // ==================== UTILITY METHODS ====================
    
    /**
     * Refresh payment data cache
     * @param brokerId Broker ID
     */
    void refreshPaymentCache(Long brokerId);

    /**
     * Update overdue payment statuses
     * @param brokerId Broker ID
     * @return Number of payments updated
     */
    int updateOverduePaymentStatuses(Long brokerId);

    /**
     * Generate payment data from ledger records
     * This method should be called periodically to sync payment data with ledger
     * @param brokerId Broker ID
     * @param financialYearId Financial year ID
     * @return Success message
     */
    ResponseEntity<ApiResponseDTO<String>> generatePaymentDataFromLedger(Long brokerId, Long financialYearId);

    /**
     * Get payment dashboard statistics
     * @param brokerId Broker ID
     * @return Payment statistics for dashboard
     */
    ResponseEntity<ApiResponseDTO<PaymentDashboardDTO>> getPaymentDashboardStatistics(Long brokerId);

    /**
     * Get payment summary by status
     * @param brokerId Broker ID
     * @return Payment summary grouped by status
     */
    ResponseEntity<ApiResponseDTO<PaymentSummaryDTO>> getPaymentSummaryByStatus(Long brokerId);

    /**
     * Export payment data to Excel
     * @param brokerId Broker ID
     * @param paymentType Type of payment (BROKERAGE, PENDING, RECEIVABLE)
     * @return Excel file as byte array
     */
    ResponseEntity<byte[]> exportPaymentDataToExcel(Long brokerId, String paymentType);

    /**
     * Get payment alerts (overdue, due soon, etc.)
     * @param brokerId Broker ID
     * @return List of payment alerts
     */
    ResponseEntity<ApiResponseDTO<List<PaymentAlertDTO>>> getPaymentAlerts(Long brokerId);

    /**
     * Send payment reminders via email/SMS
     * @param brokerId Broker ID
     * @param paymentType Type of payment
     * @param reminderType Type of reminder (EMAIL, SMS, BOTH)
     * @return Success message
     */
    ResponseEntity<ApiResponseDTO<String>> sendPaymentReminders(Long brokerId, String paymentType, String reminderType);

    /**
     * Get payment trends and analytics
     * @param brokerId Broker ID
     * @param days Number of days to analyze
     * @return Payment trends data
     */
    ResponseEntity<ApiResponseDTO<PaymentTrendsDTO>> getPaymentTrends(Long brokerId, Integer days);

    /**
     * Validate payment data integrity
     * @param brokerId Broker ID
     * @return Validation report
     */
    ResponseEntity<ApiResponseDTO<PaymentValidationReportDTO>> validatePaymentDataIntegrity(Long brokerId);
}
