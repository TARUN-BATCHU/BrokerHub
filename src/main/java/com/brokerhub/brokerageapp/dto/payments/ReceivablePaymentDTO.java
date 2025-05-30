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
 * DTO for receivable payment information.
 * Used in API responses for receivable payment endpoints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceivablePaymentDTO {
    
    /**
     * Unique identifier for the receivable payment record
     */
    private Long id;
    
    /**
     * Seller ID who is owed the payment
     */
    private String sellerId;
    
    /**
     * Seller firm name
     */
    private String sellerFirm;
    
    /**
     * Seller owner name
     */
    private String sellerOwner;
    
    /**
     * Seller city
     */
    private String sellerCity;
    
    /**
     * Seller user type (TRADER/MILLER)
     */
    private String sellerUserType;
    
    /**
     * Total receivable amount across all transactions
     */
    private BigDecimal totalReceivableAmount;
    
    /**
     * Number of transactions contributing to receivable amount
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
     * List of buyers who owe money to this seller
     */
    private List<OwedByDTO> owedBy;
    
    /**
     * Seller contact information
     */
    private String sellerPhone;
    private String sellerEmail;
    private String sellerGstNumber;
    
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
     * Number of unique buyers who owe money
     */
    private Integer uniqueBuyersCount;
    
    /**
     * Largest single debt amount
     */
    private BigDecimal largestSingleDebt;

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
        if (totalReceivableAmount != null && totalReceivableAmount.compareTo(BigDecimal.valueOf(100000)) > 0) {
            if ("LOW".equals(priorityLevel)) {
                this.priorityLevel = "MEDIUM";
            } else if ("MEDIUM".equals(priorityLevel)) {
                this.priorityLevel = "HIGH";
            }
        }
    }

    /**
     * Calculate unique buyers count
     */
    public void calculateUniqueBuyersCount() {
        this.uniqueBuyersCount = owedBy != null ? owedBy.size() : 0;
    }

    /**
     * Calculate largest single debt
     */
    public void calculateLargestSingleDebt() {
        if (owedBy != null && !owedBy.isEmpty()) {
            this.largestSingleDebt = owedBy.stream()
                    .map(OwedByDTO::getTotalOwed)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        } else {
            this.largestSingleDebt = BigDecimal.ZERO;
        }
    }

    /**
     * Get formatted total receivable amount
     */
    public String getFormattedTotalReceivableAmount() {
        return totalReceivableAmount != null ? "₹" + totalReceivableAmount.toString() : "₹0";
    }

    /**
     * Get formatted largest single debt
     */
    public String getFormattedLargestSingleDebt() {
        return largestSingleDebt != null ? "₹" + largestSingleDebt.toString() : "₹0";
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
     * Get average receivable amount per buyer
     */
    public BigDecimal getAverageReceivablePerBuyer() {
        if (totalReceivableAmount != null && uniqueBuyersCount != null && uniqueBuyersCount > 0) {
            return totalReceivableAmount.divide(BigDecimal.valueOf(uniqueBuyersCount), 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get formatted average receivable per buyer
     */
    public String getFormattedAverageReceivablePerBuyer() {
        return "₹" + getAverageReceivablePerBuyer().toString();
    }

    /**
     * Get average transaction amount
     */
    public BigDecimal getAverageTransactionAmount() {
        if (totalReceivableAmount != null && transactionCount != null && transactionCount > 0) {
            return totalReceivableAmount.divide(BigDecimal.valueOf(transactionCount), 2, java.math.RoundingMode.HALF_UP);
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
     * Check if seller has multiple buyers owing money
     */
    public boolean hasMultipleBuyers() {
        return uniqueBuyersCount != null && uniqueBuyersCount > 1;
    }

    /**
     * Get summary description for display
     */
    public String getSummaryDescription() {
        StringBuilder summary = new StringBuilder();
        summary.append(sellerFirm).append(" is owed ").append(getFormattedTotalReceivableAmount());
        summary.append(" by ").append(uniqueBuyersCount).append(" buyer");
        if (uniqueBuyersCount != null && uniqueBuyersCount > 1) {
            summary.append("s");
        }
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
