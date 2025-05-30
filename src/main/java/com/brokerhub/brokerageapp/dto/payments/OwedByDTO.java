package com.brokerhub.brokerageapp.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing a buyer who owes money to a seller.
 * Used within ReceivablePaymentDTO to show breakdown by buyer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwedByDTO {
    
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
     * Buyer user type
     */
    private String buyerUserType;
    
    /**
     * Total amount owed by this buyer
     */
    private BigDecimal totalOwed;
    
    /**
     * Number of transactions from this buyer
     */
    private Integer transactionCount;
    
    /**
     * Date of oldest transaction from this buyer
     */
    private LocalDate oldestTransactionDate;
    
    /**
     * Date of most recent transaction from this buyer
     */
    private LocalDate mostRecentTransactionDate;
    
    /**
     * List of individual transactions from this buyer
     */
    private List<PaymentTransactionDTO> transactions;
    
    /**
     * Buyer contact information
     */
    private String buyerPhone;
    private String buyerEmail;
    private String buyerGstNumber;
    
    /**
     * Whether this buyer has any overdue transactions
     */
    private Boolean hasOverdueTransactions;
    
    /**
     * Days since oldest transaction
     */
    private Long daysSinceOldestTransaction;
    
    /**
     * Average transaction amount for this buyer
     */
    private BigDecimal averageTransactionAmount;

    /**
     * Calculate days since oldest transaction
     */
    public void calculateDaysSinceOldestTransaction() {
        if (oldestTransactionDate != null) {
            this.daysSinceOldestTransaction = java.time.temporal.ChronoUnit.DAYS
                    .between(oldestTransactionDate, LocalDate.now());
        } else {
            this.daysSinceOldestTransaction = 0L;
        }
    }

    /**
     * Calculate average transaction amount
     */
    public void calculateAverageTransactionAmount() {
        if (totalOwed != null && transactionCount != null && transactionCount > 0) {
            this.averageTransactionAmount = totalOwed.divide(
                    BigDecimal.valueOf(transactionCount), 
                    2, 
                    java.math.RoundingMode.HALF_UP
            );
        } else {
            this.averageTransactionAmount = BigDecimal.ZERO;
        }
    }

    /**
     * Check for overdue transactions
     */
    public void checkForOverdueTransactions() {
        if (transactions != null && !transactions.isEmpty()) {
            this.hasOverdueTransactions = transactions.stream()
                    .anyMatch(PaymentTransactionDTO::isOverdue);
        } else {
            this.hasOverdueTransactions = false;
        }
    }

    /**
     * Get formatted total owed
     */
    public String getFormattedTotalOwed() {
        return totalOwed != null ? "₹" + totalOwed.toString() : "₹0";
    }

    /**
     * Get formatted average transaction amount
     */
    public String getFormattedAverageTransactionAmount() {
        return averageTransactionAmount != null ? "₹" + averageTransactionAmount.toString() : "₹0";
    }

    /**
     * Get buyer display name (firm name or owner name)
     */
    public String getBuyerDisplayName() {
        if (buyerFirm != null && !buyerFirm.trim().isEmpty()) {
            return buyerFirm;
        }
        return buyerOwner != null ? buyerOwner : "Unknown Buyer";
    }

    /**
     * Check if buyer is a high-value customer
     */
    public boolean isHighValueCustomer() {
        return totalOwed != null && totalOwed.compareTo(BigDecimal.valueOf(50000)) > 0;
    }

    /**
     * Check if buyer has frequent transactions
     */
    public boolean hasFrequentTransactions() {
        return transactionCount != null && transactionCount >= 5;
    }

    /**
     * Get transaction frequency description
     */
    public String getTransactionFrequency() {
        if (transactionCount == null || transactionCount == 0) {
            return "No transactions";
        } else if (transactionCount == 1) {
            return "Single transaction";
        } else if (transactionCount <= 3) {
            return "Few transactions";
        } else if (transactionCount <= 10) {
            return "Regular transactions";
        } else {
            return "Frequent transactions";
        }
    }

    /**
     * Get risk level based on amount and overdue status
     */
    public String getRiskLevel() {
        if (hasOverdueTransactions != null && hasOverdueTransactions) {
            if (totalOwed != null && totalOwed.compareTo(BigDecimal.valueOf(100000)) > 0) {
                return "HIGH";
            } else if (totalOwed != null && totalOwed.compareTo(BigDecimal.valueOf(25000)) > 0) {
                return "MEDIUM";
            } else {
                return "LOW";
            }
        }
        
        if (totalOwed != null && totalOwed.compareTo(BigDecimal.valueOf(200000)) > 0) {
            return "MEDIUM";
        }
        
        return "LOW";
    }

    /**
     * Get summary description for this buyer
     */
    public String getSummaryDescription() {
        StringBuilder summary = new StringBuilder();
        summary.append(getBuyerDisplayName());
        summary.append(" owes ").append(getFormattedTotalOwed());
        summary.append(" across ").append(transactionCount).append(" transaction");
        if (transactionCount != null && transactionCount > 1) {
            summary.append("s");
        }
        
        if (hasOverdueTransactions != null && hasOverdueTransactions) {
            summary.append(" (has overdue payments)");
        }
        
        return summary.toString();
    }

    /**
     * Get the largest single transaction amount
     */
    public BigDecimal getLargestTransactionAmount() {
        if (transactions != null && !transactions.isEmpty()) {
            return transactions.stream()
                    .map(PaymentTransactionDTO::getTotalAmount)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get formatted largest transaction amount
     */
    public String getFormattedLargestTransactionAmount() {
        return "₹" + getLargestTransactionAmount().toString();
    }
}
