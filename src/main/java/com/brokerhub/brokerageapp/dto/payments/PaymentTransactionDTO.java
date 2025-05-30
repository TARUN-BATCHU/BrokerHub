package com.brokerhub.brokerageapp.dto.payments;

import com.brokerhub.brokerageapp.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for individual payment transaction information.
 * Used in API responses for transaction details within pending/receivable payments.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransactionDTO {
    
    /**
     * Unique transaction identifier
     */
    private String id;
    
    /**
     * Transaction date
     */
    private LocalDate date;
    
    /**
     * Seller firm name
     */
    private String sellerFirm;
    
    /**
     * Seller owner name
     */
    private String sellerOwner;
    
    /**
     * Buyer firm name
     */
    private String buyerFirm;
    
    /**
     * Buyer owner name
     */
    private String buyerOwner;
    
    /**
     * Product name
     */
    private String product;
    
    /**
     * Product quality
     */
    private String quality;
    
    /**
     * Number of bags
     */
    private Long bags;
    
    /**
     * Rate per bag
     */
    private BigDecimal ratePerBag;
    
    /**
     * Total transaction amount
     */
    private BigDecimal totalAmount;
    
    /**
     * Amount already paid
     */
    private BigDecimal paidAmount;
    
    /**
     * Pending amount
     */
    private BigDecimal pendingAmount;
    
    /**
     * Due date for payment
     */
    private LocalDate dueDate;
    
    /**
     * Payment status
     */
    private PaymentStatus status;
    
    /**
     * Additional notes
     */
    private String notes;
    
    /**
     * Days overdue (calculated field)
     */
    private Long daysOverdue;
    
    /**
     * Days until due (calculated field)
     */
    private Long daysUntilDue;

    /**
     * Calculate days overdue
     */
    public void calculateDaysOverdue() {
        if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
            this.daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        } else {
            this.daysOverdue = 0L;
        }
    }

    /**
     * Calculate days until due
     */
    public void calculateDaysUntilDue() {
        if (dueDate != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            this.daysUntilDue = days >= 0 ? days : 0;
        }
    }

    /**
     * Get formatted total amount
     */
    public String getFormattedTotalAmount() {
        return totalAmount != null ? "₹" + totalAmount.toString() : "₹0";
    }

    /**
     * Get formatted pending amount
     */
    public String getFormattedPendingAmount() {
        return pendingAmount != null ? "₹" + pendingAmount.toString() : "₹0";
    }

    /**
     * Get formatted paid amount
     */
    public String getFormattedPaidAmount() {
        return paidAmount != null ? "₹" + paidAmount.toString() : "₹0";
    }

    /**
     * Get formatted rate per bag
     */
    public String getFormattedRatePerBag() {
        return ratePerBag != null ? "₹" + ratePerBag.toString() + "/bag" : "₹0/bag";
    }

    /**
     * Get status description
     */
    public String getStatusDescription() {
        return status != null ? status.getDescription() : "Unknown";
    }

    /**
     * Check if transaction is overdue
     */
    public boolean isOverdue() {
        return status == PaymentStatus.OVERDUE;
    }

    /**
     * Check if transaction is due soon
     */
    public boolean isDueSoon() {
        return daysUntilDue != null && daysUntilDue <= 3 && daysUntilDue > 0;
    }

    /**
     * Get payment completion percentage
     */
    public BigDecimal getPaymentCompletionPercentage() {
        if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0 && paidAmount != null) {
            return paidAmount
                    .divide(totalAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get formatted payment completion percentage
     */
    public String getFormattedPaymentCompletionPercentage() {
        return getPaymentCompletionPercentage().toString() + "%";
    }

    /**
     * Get transaction summary for display
     */
    public String getTransactionSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(bags).append(" bags of ").append(product);
        if (quality != null && !quality.trim().isEmpty()) {
            summary.append(" (").append(quality).append(")");
        }
        summary.append(" @ ").append(getFormattedRatePerBag());
        summary.append(" = ").append(getFormattedTotalAmount());
        return summary.toString();
    }

    /**
     * Get days since transaction
     */
    public long getDaysSinceTransaction() {
        if (date != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(date, LocalDate.now());
        }
        return 0;
    }

    /**
     * Check if transaction is recent (within last 7 days)
     */
    public boolean isRecent() {
        return getDaysSinceTransaction() <= 7;
    }

    /**
     * Check if transaction is old (more than 30 days)
     */
    public boolean isOld() {
        return getDaysSinceTransaction() > 30;
    }
}
