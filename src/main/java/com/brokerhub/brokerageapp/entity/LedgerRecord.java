package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
public class LedgerRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerRecordId;

    @ManyToOne
    private LedgerDetails ledgerDetails;

    @ManyToOne
    @NotNull
    private User toBuyer;

    @ManyToOne
    private Product product;

    @PositiveOrZero
    private Long quantity;

    @PositiveOrZero
    @NotNull
    private Long brokerage;

    @PositiveOrZero
    private Long productCost;

    @PositiveOrZero
    private Long totalProductsCost;

    @PositiveOrZero
    @NotNull
    private Long totalBrokerage;

    public Long getLedgerRecordId() {
        return ledgerRecordId;
    }

    public void setLedgerRecordId(Long ledgerRecordId) {
        this.ledgerRecordId = ledgerRecordId;
    }

    public LedgerDetails getLedgerDetails() {
        return ledgerDetails;
    }

    public void setLedgerDetails(LedgerDetails ledgerDetails) {
        this.ledgerDetails = ledgerDetails;
    }

    public User getToBuyer() {
        return toBuyer;
    }

    public void setToBuyer(User toBuyer) {
        this.toBuyer = toBuyer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(Long brokerage) {
        this.brokerage = brokerage;
    }

    public Long getProductCost() {
        return productCost;
    }

    public void setProductCost(Long productCost) {
        this.productCost = productCost;
    }

    public Long getTotalProductsCost() {
        return totalProductsCost;
    }

    public void setTotalProductsCost(Long totalProductsCost) {
        this.totalProductsCost = totalProductsCost;
    }

    public Long getTotalBrokerage() {
        return totalBrokerage;
    }

    public void setTotalBrokerage(Long totalBrokerage) {
        this.totalBrokerage = totalBrokerage;
    }
}
