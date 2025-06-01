package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancialYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long yearId;

    /**
     * The broker who owns this financial year.
     * This enables multi-tenant isolation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    @NotNull
    private Broker broker;

    @NotNull
    private LocalDate start;

    @NotNull
    private LocalDate end;

    private String financialYearName;

    private Boolean forBills;

}
