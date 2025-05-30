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
 * DTO for brokerage payment information.
 * Used in API responses for brokerage payment endpoints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrokeragePaymentDTO {
    
    /**
     * Unique identifier for the brokerage payment
     */
    private Long id;
    
    /**
     * Merchant ID who owes the brokerage
     */
    private String merchantId;
    
    /**
     * Firm name of the merchant
     */
    private String firmName;
    
    /**
     * Owner name of the merchant
     */
    private String ownerName;
    
    /**
     * City where the merchant is located
     */
    private String city;
    
    /**
     * Type of user: TRADER or MILLER
     */
    private String userType;
    
    /**
     * Total number of bags sold by the merchant
     */
    private Long soldBags;
    
    /**
     * Total number of bags bought by the merchant
     */
    private Long boughtBags;
    
    /**
     * Total bags (sold + bought)
     */
    private Long totalBags;
    
    /**
     * Brokerage rate per bag
     */
    private BigDecimal brokerageRate;
    
    /**
     * Gross brokerage amount before discounts
     */
    private BigDecimal grossBrokerage;
    
    /**
     * Discount amount applied
     */
    private BigDecimal discount;
    
    /**
     * TDS amount deducted
     */
    private BigDecimal tds;
    
    /**
     * Net brokerage amount after discount and TDS
     */
    private BigDecimal netBrokerage;
    
    /**
     * Amount already paid
     */
    private BigDecimal paidAmount;
    
    /**
     * Remaining amount to be paid
     */
    private BigDecimal pendingAmount;
    
    /**
     * Date of last payment
     */
    private LocalDate lastPaymentDate;
    
    /**
     * Due date for payment
     */
    private LocalDate dueDate;
    
    /**
     * Payment status
     */
    private PaymentStatus status;
    
    /**
     * List of partial payments made
     */
    private List<PartPaymentDTO> partPayments;
    
    /**
     * Additional notes
     */
    private String notes;
    
    /**
     * Phone number of the merchant
     */
    private String phoneNumber;
    
    /**
     * Email of the merchant
     */
    private String email;
    
    /**
     * GST number of the merchant
     */
    private String gstNumber;
    
    /**
     * Days until due date (calculated field)
     */
    private Long daysUntilDue;
    
    /**
     * Days overdue (calculated field)
     */
    private Long daysOverdue;
    
    /**
     * Percentage of payment completed
     */
    private BigDecimal paymentCompletionPercentage;

    /**
     * Calculate payment completion percentage
     */
    public void calculatePaymentCompletionPercentage() {
        if (netBrokerage != null && netBrokerage.compareTo(BigDecimal.ZERO) > 0 && paidAmount != null) {
            this.paymentCompletionPercentage = paidAmount
                    .divide(netBrokerage, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.paymentCompletionPercentage = BigDecimal.ZERO;
        }
    }

    /**
     * Calculate days until due date
     */
    public void calculateDaysUntilDue() {
        if (dueDate != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            this.daysUntilDue = days >= 0 ? days : 0;
        }
    }

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
     * Check if payment is critical (overdue by more than 30 days)
     */
    public boolean isCritical() {
        return daysOverdue != null && daysOverdue > 30;
    }

    /**
     * Check if payment is due soon (within 7 days)
     */
    public boolean isDueSoon() {
        return daysUntilDue != null && daysUntilDue <= 7 && daysUntilDue > 0;
    }

    /**
     * Get status description
     */
    public String getStatusDescription() {
        if (status != null) {
            return status.getDescription();
        }
        return "Unknown";
    }

    /**
     * Get formatted pending amount for display
     */
    public String getFormattedPendingAmount() {
        if (pendingAmount != null) {
            return "₹" + pendingAmount.toString();
        }
        return "₹0";
    }

    /**
     * Get formatted net brokerage for display
     */
    public String getFormattedNetBrokerage() {
        if (netBrokerage != null) {
            return "₹" + netBrokerage.toString();
        }
        return "₹0";
    }
}
