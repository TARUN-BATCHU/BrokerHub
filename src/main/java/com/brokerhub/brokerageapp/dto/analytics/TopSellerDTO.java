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
public class TopSellerDTO {
    
    private Long sellerId;
    private String sellerName;
    private String firmName;
    private String city;
    private String userType; // MILLER or TRADER
    private Long totalQuantitySold;
    private BigDecimal totalAmountReceived;
    private BigDecimal totalBrokerageGenerated;
    private Integer totalTransactions;
    private BigDecimal averageTransactionSize;
    private String phoneNumber;
    private String email;
}
