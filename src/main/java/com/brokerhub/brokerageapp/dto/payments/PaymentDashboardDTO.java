package com.brokerhub.brokerageapp.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for payment dashboard statistics.
 * Provides overview of all payment-related metrics for the dashboard.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDashboardDTO {
    
    // ==================== BROKERAGE PAYMENTS ====================
    
    /**
     * Total brokerage amount (paid + pending)
     */
    private BigDecimal totalBrokerageAmount;
    
    /**
     * Total brokerage amount paid
     */
    private BigDecimal totalBrokeragePaid;
    
    /**
     * Total brokerage amount pending
     */
    private BigDecimal totalBrokeragePending;
    
    /**
     * Number of merchants with pending brokerage
     */
    private Long merchantsWithPendingBrokerage;
    
    /**
     * Number of overdue brokerage payments
     */
    private Long overdueBrokeragePayments;
    
    // ==================== PENDING PAYMENTS ====================
    
    /**
     * Total pending payment amount (buyers owe to sellers)
     */
    private BigDecimal totalPendingPaymentAmount;
    
    /**
     * Number of buyers with pending payments
     */
    private Long buyersWithPendingPayments;
    
    /**
     * Number of overdue pending payments
     */
    private Long overduePendingPayments;
    
    /**
     * Average pending payment amount
     */
    private BigDecimal averagePendingPaymentAmount;
    
    // ==================== RECEIVABLE PAYMENTS ====================
    
    /**
     * Total receivable payment amount (sellers are owed)
     */
    private BigDecimal totalReceivablePaymentAmount;
    
    /**
     * Number of sellers with receivable payments
     */
    private Long sellersWithReceivablePayments;
    
    /**
     * Number of overdue receivable payments
     */
    private Long overdueReceivablePayments;
    
    /**
     * Average receivable payment amount
     */
    private BigDecimal averageReceivablePaymentAmount;
    
    // ==================== OVERALL STATISTICS ====================
    
    /**
     * Total number of active payment records
     */
    private Long totalActivePayments;
    
    /**
     * Total amount in circulation (pending + receivable)
     */
    private BigDecimal totalAmountInCirculation;
    
    /**
     * Payment completion percentage for brokerage
     */
    private BigDecimal brokerageCompletionPercentage;
    
    /**
     * Number of critical payments (high amount and overdue)
     */
    private Long criticalPaymentsCount;
    
    /**
     * Number of payments due soon (within 7 days)
     */
    private Long paymentsDueSoonCount;
    
    // ==================== RECENT ACTIVITY ====================
    
    /**
     * Number of payments received in last 7 days
     */
    private Long recentPaymentsCount;
    
    /**
     * Amount received in last 7 days
     */
    private BigDecimal recentPaymentsAmount;
    
    /**
     * Number of new pending payments in last 7 days
     */
    private Long newPendingPaymentsCount;

    /**
     * Calculate brokerage completion percentage
     */
    public void calculateBrokerageCompletionPercentage() {
        if (totalBrokerageAmount != null && totalBrokerageAmount.compareTo(BigDecimal.ZERO) > 0 && totalBrokeragePaid != null) {
            this.brokerageCompletionPercentage = totalBrokeragePaid
                    .divide(totalBrokerageAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.brokerageCompletionPercentage = BigDecimal.ZERO;
        }
    }

    /**
     * Calculate total amount in circulation
     */
    public void calculateTotalAmountInCirculation() {
        BigDecimal pending = totalPendingPaymentAmount != null ? totalPendingPaymentAmount : BigDecimal.ZERO;
        BigDecimal receivable = totalReceivablePaymentAmount != null ? totalReceivablePaymentAmount : BigDecimal.ZERO;
        BigDecimal brokeragePending = totalBrokeragePending != null ? totalBrokeragePending : BigDecimal.ZERO;
        
        this.totalAmountInCirculation = pending.add(receivable).add(brokeragePending);
    }

    /**
     * Get formatted total brokerage amount
     */
    public String getFormattedTotalBrokerageAmount() {
        return totalBrokerageAmount != null ? "₹" + totalBrokerageAmount.toString() : "₹0";
    }

    /**
     * Get formatted total amount in circulation
     */
    public String getFormattedTotalAmountInCirculation() {
        return totalAmountInCirculation != null ? "₹" + totalAmountInCirculation.toString() : "₹0";
    }

    /**
     * Get formatted brokerage completion percentage
     */
    public String getFormattedBrokerageCompletionPercentage() {
        return brokerageCompletionPercentage != null ? brokerageCompletionPercentage.toString() + "%" : "0%";
    }

    /**
     * Check if there are critical payments
     */
    public boolean hasCriticalPayments() {
        return criticalPaymentsCount != null && criticalPaymentsCount > 0;
    }

    /**
     * Check if there are payments due soon
     */
    public boolean hasPaymentsDueSoon() {
        return paymentsDueSoonCount != null && paymentsDueSoonCount > 0;
    }

    /**
     * Get overall health score (0-100)
     */
    public Integer getOverallHealthScore() {
        int score = 100;
        
        // Deduct points for overdue payments
        if (overdueBrokeragePayments != null && overdueBrokeragePayments > 0) {
            score -= Math.min(overdueBrokeragePayments * 5, 30);
        }
        if (overduePendingPayments != null && overduePendingPayments > 0) {
            score -= Math.min(overduePendingPayments * 3, 20);
        }
        if (overdueReceivablePayments != null && overdueReceivablePayments > 0) {
            score -= Math.min(overdueReceivablePayments * 3, 20);
        }
        
        // Deduct points for critical payments
        if (criticalPaymentsCount != null && criticalPaymentsCount > 0) {
            score -= Math.min(criticalPaymentsCount * 10, 30);
        }
        
        return Math.max(score, 0);
    }

    /**
     * Get health status based on score
     */
    public String getHealthStatus() {
        int score = getOverallHealthScore();
        if (score >= 80) {
            return "EXCELLENT";
        } else if (score >= 60) {
            return "GOOD";
        } else if (score >= 40) {
            return "FAIR";
        } else if (score >= 20) {
            return "POOR";
        } else {
            return "CRITICAL";
        }
    }
}
