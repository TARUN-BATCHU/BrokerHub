package com.brokerhub.brokerageapp.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAnalyticsDTO {
    
    private Long productId;
    private String productName;
    private Long totalQuantity;
    private BigDecimal totalBrokerage;
    private BigDecimal totalTransactionValue;
    private Integer totalTransactions;
    private BigDecimal averagePrice;
    private BigDecimal averageBrokeragePerUnit;
}
