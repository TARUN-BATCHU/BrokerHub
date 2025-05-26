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
public class MerchantTypeAnalyticsDTO {
    
    private String merchantType; // MILLER or TRADER
    private Long totalQuantitySold;
    private Long totalQuantityBought;
    private BigDecimal totalBrokeragePaid;
    private BigDecimal totalTransactionValue;
    private Integer totalTransactions;
    private Integer totalMerchants;
}
