package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "current_financial_year", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"broker_id"})
})
public class CurrentFinancialYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "broker_id", nullable = false, unique = true)
    private Long brokerId;

    @Column(name = "financial_year_id", nullable = false)
    private Long financialYearId;
}