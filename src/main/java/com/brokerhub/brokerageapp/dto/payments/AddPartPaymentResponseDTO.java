package com.brokerhub.brokerageapp.dto.payments;

import com.brokerhub.brokerageapp.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for add part payment response.
 * Returned when a partial payment is successfully added.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddPartPaymentResponseDTO {
    
    /**
     * Unique identifier for the part payment created
     */
    private String partPaymentId;
    
    /**
     * Updated pending amount after this payment
     */
    private BigDecimal updatedPendingAmount;
    
    /**
     * Updated total paid amount after this payment
     */
    private BigDecimal updatedPaidAmount;
    
    /**
     * Updated payment status after this payment
     */
    private PaymentStatus updatedStatus;
    
    /**
     * Original brokerage payment ID
     */
    private Long brokeragePaymentId;
    
    /**
     * Amount of this payment
     */
    private BigDecimal paymentAmount;
    
    /**
     * Percentage of total brokerage now paid
     */
    private BigDecimal paymentCompletionPercentage;
    
    /**
     * Whether the brokerage is now fully paid
     */
    private Boolean fullyPaid;
    
    /**
     * Remaining amount to be paid
     */
    private BigDecimal remainingAmount;
    
    /**
     * Total brokerage amount
     */
    private BigDecimal totalBrokerageAmount;

    /**
     * Calculate payment completion percentage
     */
    public void calculatePaymentCompletionPercentage() {
        if (totalBrokerageAmount != null && 
            totalBrokerageAmount.compareTo(BigDecimal.ZERO) > 0 && 
            updatedPaidAmount != null) {
            
            this.paymentCompletionPercentage = updatedPaidAmount
                    .divide(totalBrokerageAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.paymentCompletionPercentage = BigDecimal.ZERO;
        }
    }

    /**
     * Check if payment is fully completed
     */
    public void checkIfFullyPaid() {
        this.fullyPaid = updatedPendingAmount != null && 
                        updatedPendingAmount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Set remaining amount (same as updated pending amount)
     */
    public void setRemainingAmount() {
        this.remainingAmount = updatedPendingAmount;
    }

    /**
     * Get formatted payment amount
     */
    public String getFormattedPaymentAmount() {
        return paymentAmount != null ? "₹" + paymentAmount.toString() : "₹0";
    }

    /**
     * Get formatted updated pending amount
     */
    public String getFormattedUpdatedPendingAmount() {
        return updatedPendingAmount != null ? "₹" + updatedPendingAmount.toString() : "₹0";
    }

    /**
     * Get formatted updated paid amount
     */
    public String getFormattedUpdatedPaidAmount() {
        return updatedPaidAmount != null ? "₹" + updatedPaidAmount.toString() : "₹0";
    }

    /**
     * Get status description
     */
    public String getStatusDescription() {
        return updatedStatus != null ? updatedStatus.getDescription() : "Unknown";
    }

    /**
     * Get completion percentage as string
     */
    public String getCompletionPercentageString() {
        return paymentCompletionPercentage != null ? 
               paymentCompletionPercentage.toString() + "%" : "0%";
    }

    /**
     * Create a summary message for the payment
     */
    public String getPaymentSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Payment of ").append(getFormattedPaymentAmount()).append(" added successfully. ");
        summary.append("Total paid: ").append(getFormattedUpdatedPaidAmount()).append(", ");
        summary.append("Remaining: ").append(getFormattedUpdatedPendingAmount()).append(". ");
        summary.append("Status: ").append(getStatusDescription()).append(".");
        
        if (fullyPaid != null && fullyPaid) {
            summary.append(" Brokerage payment is now fully completed!");
        }
        
        return summary.toString();
    }

    /**
     * Check if this payment significantly reduces the pending amount
     */
    public boolean isSignificantPayment() {
        return paymentCompletionPercentage != null && 
               paymentCompletionPercentage.compareTo(BigDecimal.valueOf(25)) >= 0;
    }
}
