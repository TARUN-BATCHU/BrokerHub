package com.brokerhub.brokerageapp.dto.payments;

import com.brokerhub.brokerageapp.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for partial payment information.
 * Used in API requests and responses for part payment operations.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartPaymentDTO {
    
    /**
     * Unique identifier for the part payment
     */
    private String id;
    
    /**
     * Payment amount
     */
    private BigDecimal amount;
    
    /**
     * Date when payment was made
     */
    private LocalDate date;
    
    /**
     * Payment method used
     */
    private PaymentMethod method;
    
    /**
     * Additional notes about the payment
     */
    private String notes;
    
    /**
     * Transaction reference (cheque number, UPI ref, etc.)
     */
    private String transactionReference;
    
    /**
     * Bank details if applicable
     */
    private String bankDetails;
    
    /**
     * Whether payment is verified
     */
    private Boolean verified;
    
    /**
     * Date when payment was verified
     */
    private LocalDate verifiedDate;
    
    /**
     * User who verified the payment
     */
    private String verifiedBy;
    
    /**
     * User who recorded the payment
     */
    private String recordedBy;

    /**
     * Get formatted amount for display
     */
    public String getFormattedAmount() {
        if (amount != null) {
            return "₹" + amount.toString();
        }
        return "₹0";
    }

    /**
     * Get payment method display name
     */
    public String getMethodDisplayName() {
        if (method != null) {
            return method.getDisplayName();
        }
        return "Unknown";
    }

    /**
     * Check if payment is recent (within last 7 days)
     */
    public boolean isRecent() {
        return date != null && date.isAfter(LocalDate.now().minusDays(7));
    }

    /**
     * Get payment description for display
     */
    public String getPaymentDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(getFormattedAmount());
        desc.append(" via ").append(getMethodDisplayName());
        if (date != null) {
            desc.append(" on ").append(date);
        }
        return desc.toString();
    }

    /**
     * Check if payment requires verification
     */
    public boolean requiresVerification() {
        // Large amounts or certain payment methods might require verification
        return amount != null && amount.compareTo(BigDecimal.valueOf(10000)) > 0;
    }

    /**
     * Get verification status text
     */
    public String getVerificationStatus() {
        if (verified == null || !verified) {
            return "Pending Verification";
        }
        return "Verified";
    }
}
