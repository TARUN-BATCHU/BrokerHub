package com.brokerhub.brokerageapp.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for payment summary grouped by status.
 * Provides breakdown of payments by their current status.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSummaryDTO {
    
    /**
     * Summary of brokerage payments by status
     */
    private List<PaymentStatusSummaryDTO> brokeragePaymentSummary;
    
    /**
     * Summary of pending payments by status
     */
    private List<PaymentStatusSummaryDTO> pendingPaymentSummary;
    
    /**
     * Summary of receivable payments by status
     */
    private List<PaymentStatusSummaryDTO> receivablePaymentSummary;
    
    /**
     * Overall totals across all payment types
     */
    private PaymentTotalsDTO overallTotals;

    /**
     * DTO for payment status summary
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentStatusSummaryDTO {
        
        /**
         * Payment status
         */
        private String status;
        
        /**
         * Number of payments with this status
         */
        private Long count;
        
        /**
         * Total amount for payments with this status
         */
        private BigDecimal totalAmount;
        
        /**
         * Percentage of total payments
         */
        private BigDecimal percentage;
        
        /**
         * Status description
         */
        private String statusDescription;

        /**
         * Get formatted total amount
         */
        public String getFormattedTotalAmount() {
            return totalAmount != null ? "₹" + totalAmount.toString() : "₹0";
        }

        /**
         * Get formatted percentage
         */
        public String getFormattedPercentage() {
            return percentage != null ? percentage.toString() + "%" : "0%";
        }

        /**
         * Check if this status is critical
         */
        public boolean isCritical() {
            return "OVERDUE".equals(status) || "CRITICAL".equals(status);
        }

        /**
         * Check if this status needs attention
         */
        public boolean needsAttention() {
            return isCritical() || "DUE_SOON".equals(status);
        }
    }

    /**
     * DTO for overall payment totals
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentTotalsDTO {
        
        /**
         * Total number of all payments
         */
        private Long totalPaymentsCount;
        
        /**
         * Total amount across all payments
         */
        private BigDecimal totalPaymentsAmount;
        
        /**
         * Total number of overdue payments
         */
        private Long totalOverdueCount;
        
        /**
         * Total overdue amount
         */
        private BigDecimal totalOverdueAmount;
        
        /**
         * Total number of payments due soon
         */
        private Long totalDueSoonCount;
        
        /**
         * Total amount due soon
         */
        private BigDecimal totalDueSoonAmount;
        
        /**
         * Total number of paid/completed payments
         */
        private Long totalPaidCount;
        
        /**
         * Total paid amount
         */
        private BigDecimal totalPaidAmount;

        /**
         * Get formatted total payments amount
         */
        public String getFormattedTotalPaymentsAmount() {
            return totalPaymentsAmount != null ? "₹" + totalPaymentsAmount.toString() : "₹0";
        }

        /**
         * Get formatted total overdue amount
         */
        public String getFormattedTotalOverdueAmount() {
            return totalOverdueAmount != null ? "₹" + totalOverdueAmount.toString() : "₹0";
        }

        /**
         * Get formatted total due soon amount
         */
        public String getFormattedTotalDueSoonAmount() {
            return totalDueSoonAmount != null ? "₹" + totalDueSoonAmount.toString() : "₹0";
        }

        /**
         * Get overdue percentage
         */
        public BigDecimal getOverduePercentage() {
            if (totalPaymentsCount != null && totalPaymentsCount > 0 && totalOverdueCount != null) {
                return BigDecimal.valueOf(totalOverdueCount)
                        .divide(BigDecimal.valueOf(totalPaymentsCount), 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, java.math.RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }

        /**
         * Get completion percentage
         */
        public BigDecimal getCompletionPercentage() {
            if (totalPaymentsCount != null && totalPaymentsCount > 0 && totalPaidCount != null) {
                return BigDecimal.valueOf(totalPaidCount)
                        .divide(BigDecimal.valueOf(totalPaymentsCount), 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, java.math.RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }

        /**
         * Get formatted overdue percentage
         */
        public String getFormattedOverduePercentage() {
            return getOverduePercentage().toString() + "%";
        }

        /**
         * Get formatted completion percentage
         */
        public String getFormattedCompletionPercentage() {
            return getCompletionPercentage().toString() + "%";
        }
    }

    /**
     * Get total number of critical payments across all types
     */
    public Long getTotalCriticalPayments() {
        long total = 0;
        
        if (brokeragePaymentSummary != null) {
            total += brokeragePaymentSummary.stream()
                    .filter(PaymentStatusSummaryDTO::isCritical)
                    .mapToLong(s -> s.getCount() != null ? s.getCount() : 0)
                    .sum();
        }
        
        if (pendingPaymentSummary != null) {
            total += pendingPaymentSummary.stream()
                    .filter(PaymentStatusSummaryDTO::isCritical)
                    .mapToLong(s -> s.getCount() != null ? s.getCount() : 0)
                    .sum();
        }
        
        if (receivablePaymentSummary != null) {
            total += receivablePaymentSummary.stream()
                    .filter(PaymentStatusSummaryDTO::isCritical)
                    .mapToLong(s -> s.getCount() != null ? s.getCount() : 0)
                    .sum();
        }
        
        return total;
    }

    /**
     * Get total amount of critical payments across all types
     */
    public BigDecimal getTotalCriticalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (brokeragePaymentSummary != null) {
            total = total.add(brokeragePaymentSummary.stream()
                    .filter(PaymentStatusSummaryDTO::isCritical)
                    .map(s -> s.getTotalAmount() != null ? s.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        
        if (pendingPaymentSummary != null) {
            total = total.add(pendingPaymentSummary.stream()
                    .filter(PaymentStatusSummaryDTO::isCritical)
                    .map(s -> s.getTotalAmount() != null ? s.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        
        if (receivablePaymentSummary != null) {
            total = total.add(receivablePaymentSummary.stream()
                    .filter(PaymentStatusSummaryDTO::isCritical)
                    .map(s -> s.getTotalAmount() != null ? s.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        
        return total;
    }

    /**
     * Check if there are any critical payments
     */
    public boolean hasCriticalPayments() {
        return getTotalCriticalPayments() > 0;
    }
}
