package com.brokerhub.brokerageapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LedgerDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerDetailsId;

    /**
     * Broker-specific transaction number that starts from 1 for each broker
     * This is what brokers use in their physical books
     */
    @Column(name = "broker_transaction_number", nullable = false)
    private Long brokerTransactionNumber;

    /**
     * The broker who owns this ledger details.
     * This enables multi-tenant isolation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    @NotNull
    private Broker broker;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User fromSeller;

    @ManyToOne
    @JsonIgnore
    private DailyLedger dailyLedger;

    @OneToMany(mappedBy = "ledgerDetails", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<LedgerRecord> records;

}
