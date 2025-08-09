package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyLedgerId;

    /**
     * The broker who owns this daily ledger.
     * This enables multi-tenant isolation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    @NotNull
    private Broker broker;

    @NotNull
    private LocalDate date;

    @ManyToOne
    private FinancialYear financialYear;

    @OneToMany(mappedBy = "dailyLedger", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<LedgerDetails> ledgerDetails;

}
