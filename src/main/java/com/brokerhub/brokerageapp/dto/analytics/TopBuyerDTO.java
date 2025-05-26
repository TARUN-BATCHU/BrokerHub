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
public class TopBuyerDTO {
    
    private Long buyerId;
    private String buyerName;
    private String firmName;
    private String city;
    private String userType; // MILLER or TRADER
    private Long totalQuantityBought;
    private BigDecimal totalAmountSpent;
    private BigDecimal totalBrokeragePaid;
    private Integer totalTransactions;
    private BigDecimal averageTransactionSize;
    private String phoneNumber;
    private String email;
}
