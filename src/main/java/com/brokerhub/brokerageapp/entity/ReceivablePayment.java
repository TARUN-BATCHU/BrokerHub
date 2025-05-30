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
import java.util.List;

/**
 * Entity representing receivable payments for merchants.
 * This tracks amounts that sellers are owed by buyers for transactions.
 */
@Entity
@Table(name = "receivable_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceivablePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The seller who is owed the payment
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    private User seller;

    /**
     * The broker facilitating this transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    @NotNull
    private Broker broker;

    /**
     * Total receivable amount across all transactions for this seller
     */
    @PositiveOrZero
    private BigDecimal totalReceivableAmount;

    /**
     * Number of transactions contributing to this receivable amount
     */
    @PositiveOrZero
    private Integer transactionCount;

    /**
     * Date of the oldest transaction contributing to this receivable amount
     */
    private LocalDate oldestTransactionDate;

    /**
     * Due date for the payment
     */
    private LocalDate dueDate;

    /**
     * Payment status
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Financial year for which this receivable payment is tracked
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_year_id")
    private FinancialYear financialYear;

    /**
     * List of buyers who owe money to this seller
     */
    @OneToMany(mappedBy = "receivablePayment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReceivableTransaction> owedBy;

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
        updateStatus();
    }

    /**
     * Pre-update method
     */
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDate.now();
        updateStatus();
    }

    /**
     * Update payment status based on due date and amount
     */
    public void updateStatus() {
        if (totalReceivableAmount == null || totalReceivableAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = PaymentStatus.PAID;
        } else if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
            this.status = PaymentStatus.OVERDUE;
        } else if (dueDate != null && LocalDate.now().plusDays(7).isAfter(dueDate)) {
            this.status = PaymentStatus.DUE_SOON;
        } else {
            this.status = PaymentStatus.PENDING;
        }
    }

    /**
     * Calculate total receivable amount from transactions
     */
    public void calculateTotalFromTransactions() {
        if (owedBy != null && !owedBy.isEmpty()) {
            this.totalReceivableAmount = owedBy.stream()
                    .flatMap(rt -> rt.getTransactions().stream())
                    .map(PaymentTransaction::getPendingAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            this.transactionCount = owedBy.stream()
                    .mapToInt(rt -> rt.getTransactions().size())
                    .sum();
            
            this.oldestTransactionDate = owedBy.stream()
                    .flatMap(rt -> rt.getTransactions().stream())
                    .map(PaymentTransaction::getTransactionDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);
        }
    }

    /**
     * Get seller firm name for display
     */
    public String getSellerFirmName() {
        return seller != null ? seller.getFirmName() : "";
    }

    /**
     * Get seller owner name for display
     */
    public String getSellerOwnerName() {
        return seller != null ? seller.getOwnerName() : "";
    }

    /**
     * Get seller city for display
     */
    public String getSellerCity() {
        return seller != null && seller.getAddress() != null ? seller.getAddress().getCity() : "";
    }

    /**
     * Check if payment is overdue
     */
    public boolean isOverdue() {
        return status == PaymentStatus.OVERDUE;
    }

    /**
     * Check if payment is due soon
     */
    public boolean isDueSoon() {
        return status == PaymentStatus.DUE_SOON;
    }

    /**
     * Get days until due date
     */
    public long getDaysUntilDue() {
        if (dueDate == null) {
            return Long.MAX_VALUE;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * Get days overdue
     */
    public long getDaysOverdue() {
        if (dueDate == null || !isOverdue()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Get total number of unique buyers who owe money
     */
    public int getUniqueBuyersCount() {
        return owedBy != null ? owedBy.size() : 0;
    }

    /**
     * Get the largest single amount owed by any buyer
     */
    public BigDecimal getLargestSingleDebt() {
        if (owedBy == null || owedBy.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return owedBy.stream()
                .map(ReceivableTransaction::getTotalOwed)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
