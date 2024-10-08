package com.brokerhub.brokerageapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LedgerRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerRecordId;

    @ManyToOne
    @JsonIgnore
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

}
