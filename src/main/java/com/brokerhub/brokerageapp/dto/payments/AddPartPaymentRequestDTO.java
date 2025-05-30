package com.brokerhub.brokerageapp.dto.payments;

import com.brokerhub.brokerageapp.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for adding part payment requests.
 * Used when merchants make partial payments towards their brokerage.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddPartPaymentRequestDTO {
    
    /**
     * Payment amount (required)
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    /**
     * Payment method (required)
     */
    @NotNull(message = "Payment method is required")
    private PaymentMethod method;
    
    /**
     * Additional notes about the payment (optional)
     */
    private String notes;
    
    /**
     * Payment date (required)
     */
    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;
    
    /**
     * Transaction reference number (optional)
     * For cheque: cheque number
     * For UPI: transaction ID
     * For bank transfer: reference number
     */
    private String transactionReference;
    
    /**
     * Bank details if payment is through bank (optional)
     */
    private String bankDetails;
    
    /**
     * User who is recording this payment
     */
    private String recordedBy;

    /**
     * Validate the request data
     */
    public boolean isValid() {
        return amount != null && 
               amount.compareTo(BigDecimal.ZERO) > 0 && 
               method != null && 
               paymentDate != null;
    }

    /**
     * Get validation error message
     */
    public String getValidationError() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Amount must be positive";
        }
        if (method == null) {
            return "Payment method is required";
        }
        if (paymentDate == null) {
            return "Payment date is required";
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            return "Payment date cannot be in the future";
        }
        return null;
    }

    /**
     * Check if payment date is valid (not in future)
     */
    public boolean isPaymentDateValid() {
        return paymentDate != null && !paymentDate.isAfter(LocalDate.now());
    }

    /**
     * Check if transaction reference is required for this payment method
     */
    public boolean isTransactionReferenceRequired() {
        return method != null && (method == PaymentMethod.CHEQUE || 
                                 method == PaymentMethod.UPI || 
                                 method == PaymentMethod.BANK_TRANSFER ||
                                 method == PaymentMethod.NEFT ||
                                 method == PaymentMethod.RTGS);
    }

    /**
     * Check if bank details are required for this payment method
     */
    public boolean areBankDetailsRequired() {
        return method != null && method.requiresBankDetails();
    }

    /**
     * Get formatted amount for logging
     */
    public String getFormattedAmount() {
        return amount != null ? "₹" + amount.toString() : "₹0";
    }

    /**
     * Get payment method display name
     */
    public String getMethodDisplayName() {
        return method != null ? method.getDisplayName() : "Unknown";
    }

    /**
     * Create a summary string for logging
     */
    public String getSummary() {
        return String.format("Payment: %s via %s on %s", 
                           getFormattedAmount(), 
                           getMethodDisplayName(), 
                           paymentDate);
    }
}
