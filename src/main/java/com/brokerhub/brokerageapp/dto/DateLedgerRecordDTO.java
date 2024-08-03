package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateLedgerRecordDTO {


    private Long sellerId;
    private Long ledgerDetailsId;
    private Long buyerId;
    private Long productId;
    private Long quantity;
    private Long brokerage;
    private Long productCost;


}
