package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisplayLedgerRecordDTO {

    private String buyerName;

    private String location;

    private String productName;

    private Long quantity;

    private Long brokerage;

    private Long productCost;

    private Long total;
}
