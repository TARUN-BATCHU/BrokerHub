package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing partial payments made towards brokerage payments.
 * This tracks individual payment transactions made by merchants to reduce their brokerage debt.
 */
@Entity
@Table(name = "part_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier for the part payment (for reference)
     */
    @Column(name = "payment_reference", unique = true)
    private String paymentReference;

    /**
     * The brokerage payment this part payment is applied to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brokerage_payment_id", nullable = false)
    @NotNull
    private BrokeragePayment brokeragePayment;

    /**
     * Amount of this partial payment
     */
    @Positive
    @NotNull
    private BigDecimal amount;

    /**
     * Date when the payment was made
     */
    @NotNull
    private LocalDate paymentDate;

    /**
     * Method used for this payment
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod method;

    /**
     * Additional notes or comments about this payment
     */
    @Column(length = 500)
    private String notes;

    /**
     * Reference number for the payment (cheque number, transaction ID, etc.)
     */
    @Column(name = "transaction_reference")
    private String transactionReference;

    /**
     * Bank details if payment was made through bank
     */
    @Column(name = "bank_details")
    private String bankDetails;

    /**
     * Date and time when this record was created
     */
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Date and time when this record was last updated
     */
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * User who recorded this payment (for audit trail)
     */
    @Column(name = "recorded_by")
    private String recordedBy;

    /**
     * Whether this payment has been verified
     */
    @Builder.Default
    private Boolean verified = false;

    /**
     * Date when the payment was verified
     */
    @Column(name = "verified_date")
    private LocalDate verifiedDate;

    /**
     * User who verified this payment
     */
    @Column(name = "verified_by")
    private String verifiedBy;

    /**
     * Pre-persist method to set creation timestamp and generate reference
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (paymentReference == null || paymentReference.trim().isEmpty()) {
            generatePaymentReference();
        }
    }

    /**
     * Pre-update method to set update timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Generate a unique payment reference
     */
    private void generatePaymentReference() {
        // Generate reference in format: PP{YYYYMMDD}{HHMMSS}{ID}
        String dateTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.paymentReference = "PP" + dateTime + (id != null ? id : "");
    }

    /**
     * Mark this payment as verified
     */
    public void markAsVerified(String verifiedBy) {
        this.verified = true;
        this.verifiedDate = LocalDate.now();
        this.verifiedBy = verifiedBy;
    }

    /**
     * Get formatted payment reference for display
     */
    public String getFormattedReference() {
        if (paymentReference != null) {
            return paymentReference;
        }
        return "PP" + (id != null ? String.format("%06d", id) : "000000");
    }

    /**
     * Check if this payment is recent (within last 30 days)
     */
    public boolean isRecent() {
        return paymentDate != null && paymentDate.isAfter(LocalDate.now().minusDays(30));
    }

    /**
     * Get payment description for display
     */
    public String getPaymentDescription() {
        StringBuilder description = new StringBuilder();
        description.append("₹").append(amount);
        description.append(" via ").append(method.getDisplayName());
        if (paymentDate != null) {
            description.append(" on ").append(paymentDate);
        }
        return description.toString();
    }
}
