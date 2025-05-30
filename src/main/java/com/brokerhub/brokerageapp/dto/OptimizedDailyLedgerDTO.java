package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptimizedDailyLedgerDTO {
    
    private Long dailyLedgerId;
    private LocalDate date;
    private Long financialYearId;
    private List<OptimizedLedgerDetailsDTO> ledgerDetails;
}
