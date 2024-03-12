package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class LedgerDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerDetailsId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User fromSeller;

    @ManyToOne
    private DailyLedger dailyLedger;

    @OneToMany(mappedBy = "ledgerDetails")
    private List<LedgerRecord> records;

    public Long getLedgerDetailsId() {
        return ledgerDetailsId;
    }

    public void setLedgerDetailsId(Long ledgerDetailsId) {
        this.ledgerDetailsId = ledgerDetailsId;
    }

    public User getFromSeller() {
        return fromSeller;
    }

    public void setFromSeller(User fromSeller) {
        this.fromSeller = fromSeller;
    }

    public DailyLedger getDailyLedger() {
        return dailyLedger;
    }

    public void setDailyLedger(DailyLedger dailyLedger) {
        this.dailyLedger = dailyLedger;
    }

    public List<LedgerRecord> getRecords() {
        return records;
    }

    public void setRecords(List<LedgerRecord> records) {
        this.records = records;
    }
}
