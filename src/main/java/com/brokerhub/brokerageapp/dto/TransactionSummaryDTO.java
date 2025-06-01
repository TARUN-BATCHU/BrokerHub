package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionSummaryDTO {
    private Long totalBagsSoldInTransaction;
    private BigDecimal totalBrokerageInTransaction;
    private Long totalReceivableAmountInTransaction;
    private BigDecimal averageBrokeragePerBag;
    private Integer numberOfProducts;
    private Integer numberOfBuyers;
}
