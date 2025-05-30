package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing individual payment transactions between merchants.
 * This tracks specific transactions that contribute to pending/receivable payments.
 */
@Entity
@Table(name = "payment_transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique transaction identifier
     */
    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    /**
     * Date of the transaction
     */
    @NotNull
    private LocalDate transactionDate;

    /**
     * The seller in this transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    private User seller;

    /**
     * The buyer in this transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull
    private User buyer;

    /**
     * Product involved in the transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;

    /**
     * Quality of the product
     */
    private String quality;

    /**
     * Number of bags in the transaction
     */
    @PositiveOrZero
    private Long bags;

    /**
     * Rate per bag
     */
    @PositiveOrZero
    private BigDecimal ratePerBag;

    /**
     * Total amount for the transaction
     */
    @PositiveOrZero
    private BigDecimal totalAmount;

    /**
     * Amount already paid
     */
    @PositiveOrZero
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * Pending amount (totalAmount - paidAmount)
     */
    @PositiveOrZero
    private BigDecimal pendingAmount;

    /**
     * Due date for payment
     */
    private LocalDate dueDate;

    /**
     * Payment status
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Reference to pending payment (for buyer's perspective)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pending_payment_id")
    private PendingPayment pendingPayment;

    /**
     * Reference to receivable transaction (for seller's perspective)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivable_transaction_id")
    private ReceivableTransaction receivableTransaction;

    /**
     * Financial year for this transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_year_id")
    private FinancialYear financialYear;

    /**
     * Date when this record was created
     */
    @Column(name = "created_date")
    @Builder.Default
    private LocalDate createdDate = LocalDate.now();

    /**
     * Date when this record was last updated
     */
    @Column(name = "updated_date")
    @Builder.Default
    private LocalDate updatedDate = LocalDate.now();

    /**
     * Additional notes
     */
    @Column(length = 500)
    private String notes;

    /**
     * Pre-persist method
     */
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDate.now();
        updatedDate = LocalDate.now();
        if (transactionId == null || transactionId.trim().isEmpty()) {
            generateTransactionId();
        }
        calculatePendingAmount();
        updateStatus();
    }

    /**
     * Pre-update method
     */
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDate.now();
        calculatePendingAmount();
        updateStatus();
    }

    /**
     * Generate unique transaction ID
     */
    private void generateTransactionId() {
        String dateStr = transactionDate != null ? 
            transactionDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) :
            LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.transactionId = "PT" + dateStr + (id != null ? String.format("%06d", id) : "000000");
    }

    /**
     * Calculate pending amount
     */
    public void calculatePendingAmount() {
        if (totalAmount != null && paidAmount != null) {
            this.pendingAmount = totalAmount.subtract(paidAmount);
            if (this.pendingAmount.compareTo(BigDecimal.ZERO) < 0) {
                this.pendingAmount = BigDecimal.ZERO;
            }
        }
    }

    /**
     * Update payment status
     */
    public void updateStatus() {
        if (pendingAmount == null) {
            calculatePendingAmount();
        }
        
        if (pendingAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = PaymentStatus.PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
                this.status = PaymentStatus.OVERDUE;
            } else {
                this.status = PaymentStatus.PARTIAL_PAID;
            }
        } else {
            if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
                this.status = PaymentStatus.OVERDUE;
            } else {
                this.status = PaymentStatus.PENDING;
            }
        }
    }

    /**
     * Get seller firm name
     */
    public String getSellerFirmName() {
        return seller != null ? seller.getFirmName() : "";
    }

    /**
     * Get seller owner name
     */
    public String getSellerOwnerName() {
        return seller != null ? seller.getOwnerName() : "";
    }

    /**
     * Get buyer firm name
     */
    public String getBuyerFirmName() {
        return buyer != null ? buyer.getFirmName() : "";
    }

    /**
     * Get buyer owner name
     */
    public String getBuyerOwnerName() {
        return buyer != null ? buyer.getOwnerName() : "";
    }

    /**
     * Get product name
     */
    public String getProductName() {
        return product != null ? product.getProductName() : "";
    }

    /**
     * Check if transaction is overdue
     */
    public boolean isOverdue() {
        return status == PaymentStatus.OVERDUE;
    }

    /**
     * Make a payment towards this transaction
     */
    public void makePayment(BigDecimal paymentAmount) {
        if (paymentAmount != null && paymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (this.paidAmount == null) {
                this.paidAmount = BigDecimal.ZERO;
            }
            this.paidAmount = this.paidAmount.add(paymentAmount);
            calculatePendingAmount();
            updateStatus();
        }
    }
}
