package com.brokerhub.brokerageapp.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityBrokerageAnalyticsDTO {
    
    private String city;
    private Long totalMerchants;
    private Long totalBagsSold;
    private Long totalBagsBought;
    private Long totalBags;
    private BigDecimal totalActualBrokerage;
    private BigDecimal totalBrokeragePending;
    private BigDecimal totalBrokerageReceived;
    private Long totalPayments;
    private Long totalPartialPayments;
    private Long totalSuccessPayments;
    private Long merchantsBusinessIncreased;
    private Long merchantsBusinessDecreased;
    private BigDecimal totalBrokerageChange;
}