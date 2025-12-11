package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "brokerage_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrokerageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private User merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    private Broker broker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_year_id", nullable = false)
    private FinancialYear financialYear;

    private Long soldBags;
    private Long boughtBags;
    private Long totalBags;
    private BigDecimal totalBrokerage;
    private BigDecimal paidBrokerage;

    @PrePersist
    @PreUpdate
    protected void calculateTotalBags() {
        if (soldBags != null && boughtBags != null) {
            this.totalBags = soldBags + boughtBags;
        }
    }
}