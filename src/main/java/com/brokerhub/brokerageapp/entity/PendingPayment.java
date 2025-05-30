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
 * Entity representing pending payments between merchants.
 * This tracks amounts that buyers owe to sellers for transactions.
 */
@Entity
@Table(name = "pending_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The buyer who owes the payment
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull
    private User buyer;

    /**
     * The broker facilitating this transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    @NotNull
    private Broker broker;

    /**
     * Total pending amount across all transactions for this buyer
     */
    @PositiveOrZero
    private BigDecimal totalPendingAmount;

    /**
     * Number of transactions contributing to this pending amount
     */
    @PositiveOrZero
    private Integer transactionCount;

    /**
     * Date of the oldest transaction contributing to this pending amount
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
     * Financial year for which this pending payment is tracked
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_year_id")
    private FinancialYear financialYear;

    /**
     * List of individual transactions contributing to this pending payment
     */
    @OneToMany(mappedBy = "pendingPayment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentTransaction> transactions;

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
        if (totalPendingAmount == null || totalPendingAmount.compareTo(BigDecimal.ZERO) == 0) {
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
     * Calculate total pending amount from transactions
     */
    public void calculateTotalFromTransactions() {
        if (transactions != null && !transactions.isEmpty()) {
            this.totalPendingAmount = transactions.stream()
                    .map(PaymentTransaction::getPendingAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.transactionCount = transactions.size();
            this.oldestTransactionDate = transactions.stream()
                    .map(PaymentTransaction::getTransactionDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);
        }
    }

    /**
     * Get buyer firm name for display
     */
    public String getBuyerFirmName() {
        return buyer != null ? buyer.getFirmName() : "";
    }

    /**
     * Get buyer owner name for display
     */
    public String getBuyerOwnerName() {
        return buyer != null ? buyer.getOwnerName() : "";
    }

    /**
     * Get buyer city for display
     */
    public String getBuyerCity() {
        return buyer != null && buyer.getAddress() != null ? buyer.getAddress().getCity() : "";
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
}
