package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class DailyLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyLedgerId;

    private Date date;

    @ManyToOne
    private FinancialYear financialYear;

    @OneToMany(mappedBy = "dailyLedger")
    private List<LedgerDetails> ledgerDetails;

    public Long getDailyLedgerId() {
        return dailyLedgerId;
    }

    public void setDailyLedgerId(Long dailyLedgerId) {
        this.dailyLedgerId = dailyLedgerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FinancialYear getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(FinancialYear financialYear) {
        this.financialYear = financialYear;
    }

    public List<LedgerDetails> getLedgerDetails() {
        return ledgerDetails;
    }

    public void setLedgerDetails(List<LedgerDetails> ledgerDetails) {
        this.ledgerDetails = ledgerDetails;
    }
}
