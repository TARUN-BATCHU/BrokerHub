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
public class TopMerchantByBrokerageDTO {
    
    private Long merchantId;
    private String merchantName;
    private String firmName;
    private String city;
    private String userType; // MILLER or TRADER
    private BigDecimal totalBrokeragePaid;
    private Long totalQuantityTraded; // Combined bought + sold
    private Long totalQuantityBought;
    private Long totalQuantitySold;
    private BigDecimal totalAmountTraded;
    private Integer totalTransactions;
    private BigDecimal averageBrokeragePerTransaction;
    private BigDecimal averageBrokeragePerUnit;
    private String phoneNumber;
    private String email;
}
