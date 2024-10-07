package com.brokerhub.brokerageapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User fromSeller;

    @ManyToOne
    @JsonIgnore
    private DailyLedger dailyLedger;

    @OneToMany(mappedBy = "ledgerDetails")
    private List<LedgerRecord> records;

}
