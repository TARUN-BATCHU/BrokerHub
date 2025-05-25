package com.brokerhub.brokerageapp.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopPerformersDTO {
    
    private Long financialYearId;
    private String financialYearName;
    
    // Top 5 buyers by quantity
    private List<TopBuyerDTO> topBuyersByQuantity;
    
    // Top 5 sellers by quantity
    private List<TopSellerDTO> topSellersByQuantity;
    
    // Top 5 merchants by brokerage amount
    private List<TopMerchantByBrokerageDTO> topMerchantsByBrokerage;
}
