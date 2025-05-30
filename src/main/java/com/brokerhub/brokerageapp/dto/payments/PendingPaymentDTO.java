package com.brokerhub.brokerageapp.dto.payments;

import com.brokerhub.brokerageapp.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for pending payment information.
 * Used in API responses for pending payment endpoints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingPaymentDTO {
    
    /**
     * Unique identifier for the pending payment record
     */
    private Long id;
    
    /**
     * Buyer ID who owes the payment
     */
    private String buyerId;
    
    /**
     * Buyer firm name
     */
    private String buyerFirm;
    
    /**
     * Buyer owner name
     */
    private String buyerOwner;
    
    /**
     * Buyer city
     */
    private String buyerCity;
    
    /**
     * Buyer user type (TRADER/MILLER)
     */
    private String buyerUserType;
    
    /**
     * Total pending amount across all transactions
     */
    private BigDecimal totalPendingAmount;
    
    /**
     * Number of transactions contributing to pending amount
     */
    private Integer transactionCount;
    
    /**
     * Date of oldest transaction
     */
    private LocalDate oldestTransactionDate;
    
    /**
     * Due date for payment
     */
    private LocalDate dueDate;
    
    /**
     * Payment status
     */
    private PaymentStatus status;
    
    /**
     * List of individual transactions
     */
    private List<PaymentTransactionDTO> transactions;
    
    /**
     * Buyer contact information
     */
    private String buyerPhone;
    private String buyerEmail;
    private String buyerGstNumber;
    
    /**
     * Days overdue (calculated field)
     */
    private Long daysOverdue;
    
    /**
     * Days until due (calculated field)
     */
    private Long daysUntilDue;
    
    /**
     * Priority level based on amount and overdue days
     */
    private String priorityLevel;

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
     * Calculate priority level based on amount and overdue status
     */
    public void calculatePriorityLevel() {
        if (daysOverdue != null && daysOverdue > 30) {
            this.priorityLevel = "CRITICAL";
        } else if (daysOverdue != null && daysOverdue > 7) {
            this.priorityLevel = "HIGH";
        } else if (daysUntilDue != null && daysUntilDue <= 3) {
            this.priorityLevel = "MEDIUM";
        } else {
            this.priorityLevel = "LOW";
        }
        
        // Upgrade priority for large amounts
        if (totalPendingAmount != null && totalPendingAmount.compareTo(BigDecimal.valueOf(100000)) > 0) {
            if ("LOW".equals(priorityLevel)) {
                this.priorityLevel = "MEDIUM";
            } else if ("MEDIUM".equals(priorityLevel)) {
                this.priorityLevel = "HIGH";
            }
        }
    }

    /**
     * Get formatted total pending amount
     */
    public String getFormattedTotalPendingAmount() {
        return totalPendingAmount != null ? "₹" + totalPendingAmount.toString() : "₹0";
    }

    /**
     * Get status description
     */
    public String getStatusDescription() {
        return status != null ? status.getDescription() : "Unknown";
    }

    /**
     * Check if payment is critical
     */
    public boolean isCritical() {
        return "CRITICAL".equals(priorityLevel);
    }

    /**
     * Check if payment is high priority
     */
    public boolean isHighPriority() {
        return "HIGH".equals(priorityLevel) || "CRITICAL".equals(priorityLevel);
    }

    /**
     * Get average transaction amount
     */
    public BigDecimal getAverageTransactionAmount() {
        if (totalPendingAmount != null && transactionCount != null && transactionCount > 0) {
            return totalPendingAmount.divide(BigDecimal.valueOf(transactionCount), 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get formatted average transaction amount
     */
    public String getFormattedAverageTransactionAmount() {
        return "₹" + getAverageTransactionAmount().toString();
    }

    /**
     * Get days since oldest transaction
     */
    public long getDaysSinceOldestTransaction() {
        if (oldestTransactionDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(oldestTransactionDate, LocalDate.now());
        }
        return 0;
    }

    /**
     * Check if buyer has multiple pending transactions
     */
    public boolean hasMultipleTransactions() {
        return transactionCount != null && transactionCount > 1;
    }

    /**
     * Get summary description for display
     */
    public String getSummaryDescription() {
        StringBuilder summary = new StringBuilder();
        summary.append(buyerFirm).append(" owes ").append(getFormattedTotalPendingAmount());
        summary.append(" across ").append(transactionCount).append(" transaction");
        if (transactionCount != null && transactionCount > 1) {
            summary.append("s");
        }
        if (daysOverdue != null && daysOverdue > 0) {
            summary.append(", overdue by ").append(daysOverdue).append(" days");
        }
        return summary.toString();
    }
}
