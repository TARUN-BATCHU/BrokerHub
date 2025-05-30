package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptimizedLedgerDetailsDTO {
    
    private Long ledgerDetailsId;
    private OptimizedUserDTO fromSeller;
    private List<OptimizedLedgerRecordDTO> records;
}
