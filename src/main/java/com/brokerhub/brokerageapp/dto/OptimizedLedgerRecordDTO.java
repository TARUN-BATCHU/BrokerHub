package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptimizedLedgerRecordDTO {
    
    private Long ledgerRecordId;
    private OptimizedUserDTO toBuyer;
    private OptimizedProductDTO product;
    private Long quantity;
    private Long brokerage;
    private Long productCost;
    private Long totalProductsCost;
    private Long totalBrokerage;
}
