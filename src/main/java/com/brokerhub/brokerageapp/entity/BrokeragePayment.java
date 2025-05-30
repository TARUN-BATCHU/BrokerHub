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
 * Entity representing brokerage payments that merchants need to pay to the broker.
 * This tracks the brokerage amount owed by merchants based on their trading activities.
 */
@Entity
@Table(name = "brokerage_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrokeragePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The merchant who owes the brokerage payment
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    @NotNull
    private User merchant;

    /**
     * The broker to whom the payment is owed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    @NotNull
    private Broker broker;

    /**
     * Total number of bags sold by the merchant
     */
    @PositiveOrZero
    private Long soldBags;

    /**
     * Total number of bags bought by the merchant
     */
    @PositiveOrZero
    private Long boughtBags;

    /**
     * Total bags (sold + bought) for brokerage calculation
     */
    @PositiveOrZero
    private Long totalBags;

    /**
     * Brokerage rate per bag (in rupees)
     */
    @PositiveOrZero
    private BigDecimal brokerageRate;

    /**
     * Gross brokerage amount before discounts and TDS
     * Formula: totalBags * brokerageRate
     */
    @PositiveOrZero
    private BigDecimal grossBrokerage;

    /**
     * Discount amount (typically 10% of gross brokerage)
     */
    @PositiveOrZero
    private BigDecimal discount;

    /**
     * TDS amount (typically 5% of gross brokerage)
     */
    @PositiveOrZero
    private BigDecimal tds;

    /**
     * Net brokerage amount after discount and TDS
     * Formula: grossBrokerage - discount - tds
     */
    @PositiveOrZero
    private BigDecimal netBrokerage;

    /**
     * Total amount paid so far
     */
    @PositiveOrZero
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * Remaining amount to be paid
     * Formula: netBrokerage - paidAmount
     */
    @PositiveOrZero
    private BigDecimal pendingAmount;

    /**
     * Date of last payment received
     */
    private LocalDate lastPaymentDate;

    /**
     * Due date for the payment
     */
    private LocalDate dueDate;

    /**
     * Payment status: PENDING, PARTIAL_PAID, PAID, OVERDUE
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Financial year for which this brokerage is calculated
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_year_id")
    private FinancialYear financialYear;

    /**
     * List of partial payments made towards this brokerage
     */
    @OneToMany(mappedBy = "brokeragePayment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartPayment> partPayments;

    /**
     * Date when this brokerage record was created
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
     * Additional notes or comments
     */
    @Column(length = 500)
    private String notes;

    /**
     * Pre-persist method to set creation date
     */
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDate.now();
        updatedDate = LocalDate.now();
        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }
        calculatePendingAmount();
        updateStatus();
    }

    /**
     * Pre-update method to set update date
     */
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDate.now();
        calculatePendingAmount();
        updateStatus();
    }

    /**
     * Calculate pending amount based on net brokerage and paid amount
     */
    public void calculatePendingAmount() {
        if (netBrokerage != null && paidAmount != null) {
            this.pendingAmount = netBrokerage.subtract(paidAmount);
            if (this.pendingAmount.compareTo(BigDecimal.ZERO) < 0) {
                this.pendingAmount = BigDecimal.ZERO;
            }
        }
    }

    /**
     * Update payment status based on pending amount and due date
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
     * Add a partial payment to this brokerage payment
     */
    public void addPartPayment(PartPayment partPayment) {
        if (this.partPayments != null) {
            this.partPayments.add(partPayment);
        }
        if (this.paidAmount == null) {
            this.paidAmount = BigDecimal.ZERO;
        }
        this.paidAmount = this.paidAmount.add(partPayment.getAmount());
        this.lastPaymentDate = partPayment.getPaymentDate();
        calculatePendingAmount();
        updateStatus();
    }
}
