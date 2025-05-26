package com.brokerhub.brokerageapp.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyAnalyticsDTO {
    
    private YearMonth month;
    private String monthName;
    private BigDecimal totalBrokerage;
    private Long totalQuantity;
    private BigDecimal totalTransactionValue;
    private Integer totalTransactions;
    
    // Product-wise breakdown
    private List<ProductAnalyticsDTO> productAnalytics;
    
    // City-wise breakdown
    private List<CityAnalyticsDTO> cityAnalytics;
    
    // Merchant type breakdown
    private List<MerchantTypeAnalyticsDTO> merchantTypeAnalytics;
}
