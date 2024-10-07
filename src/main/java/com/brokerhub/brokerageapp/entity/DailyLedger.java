package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotNull
    private LocalDate date;

    @ManyToOne
    private FinancialYear financialYear;

    @OneToMany(mappedBy = "dailyLedger")
    private List<LedgerDetails> ledgerDetails;

}
