package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;

@Entity
public class LedgerRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerRecordId;

    @ManyToOne
    private LedgerDetails ledgerDetails;

    @ManyToOne
    private User toBuyer;

    @ManyToOne
    private Product product;

    private int quantity;

    private int brokerage;

    private int productCost;

    private int totalProductsCost;

    private int totalBrokerage;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(int brokerage) {
        this.brokerage = brokerage;
    }

    public int getProductCost() {
        return productCost;
    }

    public void setProductCost(int productCost) {
        this.productCost = productCost;
    }

    public int getTotalProductsCost() {
        return totalProductsCost;
    }

    public void setTotalProductsCost(int totalProductsCost) {
        this.totalProductsCost = totalProductsCost;
    }

    public int getTotalBrokerage() {
        return totalBrokerage;
    }

    public void setTotalBrokerage(int totalBrokerage) {
        this.totalBrokerage = totalBrokerage;
    }
}
