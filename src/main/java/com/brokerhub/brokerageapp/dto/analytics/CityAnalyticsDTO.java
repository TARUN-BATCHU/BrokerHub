package com.brokerhub.brokerageapp.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityAnalyticsDTO {
    
    private String cityName;
    private Long totalQuantity;
    private BigDecimal totalBrokerage;
    private BigDecimal totalTransactionValue;
    private Integer totalTransactions;
    private Integer totalSellers;
    private Integer totalBuyers;
    
    // Product breakdown for this city
    private List<ProductAnalyticsDTO> productBreakdown;
}
