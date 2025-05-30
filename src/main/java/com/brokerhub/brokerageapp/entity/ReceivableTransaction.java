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
 * Entity representing receivable transactions grouped by buyer.
 * This groups all transactions where a specific buyer owes money to a seller.
 */
@Entity
@Table(name = "receivable_transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceivableTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The buyer who owes money
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull
    private User buyer;

    /**
     * Total amount owed by this buyer to the seller
     */
    @PositiveOrZero
    private BigDecimal totalOwed;

    /**
     * Reference to the receivable payment this belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivable_payment_id", nullable = false)
    @NotNull
    private ReceivablePayment receivablePayment;

    /**
     * List of individual transactions from this buyer
     */
    @OneToMany(mappedBy = "receivableTransaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
     * Pre-persist method
     */
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDate.now();
        updatedDate = LocalDate.now();
        calculateTotalOwed();
    }

    /**
     * Pre-update method
     */
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDate.now();
        calculateTotalOwed();
    }

    /**
     * Calculate total owed from transactions
     */
    public void calculateTotalOwed() {
        if (transactions != null && !transactions.isEmpty()) {
            this.totalOwed = transactions.stream()
                    .map(PaymentTransaction::getPendingAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalOwed = BigDecimal.ZERO;
        }
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
     * Get number of transactions
     */
    public int getTransactionCount() {
        return transactions != null ? transactions.size() : 0;
    }

    /**
     * Get oldest transaction date
     */
    public LocalDate getOldestTransactionDate() {
        if (transactions == null || transactions.isEmpty()) {
            return null;
        }
        return transactions.stream()
                .map(PaymentTransaction::getTransactionDate)
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    /**
     * Get most recent transaction date
     */
    public LocalDate getMostRecentTransactionDate() {
        if (transactions == null || transactions.isEmpty()) {
            return null;
        }
        return transactions.stream()
                .map(PaymentTransaction::getTransactionDate)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    /**
     * Check if any transaction is overdue
     */
    public boolean hasOverdueTransactions() {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }
        return transactions.stream()
                .anyMatch(PaymentTransaction::isOverdue);
    }

    /**
     * Get total amount across all transactions (including paid)
     */
    public BigDecimal getTotalTransactionAmount() {
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return transactions.stream()
                .map(PaymentTransaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total paid amount across all transactions
     */
    public BigDecimal getTotalPaidAmount() {
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return transactions.stream()
                .map(PaymentTransaction::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Add a transaction to this receivable transaction
     */
    public void addTransaction(PaymentTransaction transaction) {
        if (transactions != null) {
            transactions.add(transaction);
            transaction.setReceivableTransaction(this);
            calculateTotalOwed();
        }
    }

    /**
     * Remove a transaction from this receivable transaction
     */
    public void removeTransaction(PaymentTransaction transaction) {
        if (transactions != null) {
            transactions.remove(transaction);
            transaction.setReceivableTransaction(null);
            calculateTotalOwed();
        }
    }
}
