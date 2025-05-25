package com.brokerhub.brokerageapp.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancialYearAnalyticsDTO {
    
    private Long financialYearId;
    private String financialYearName;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Overall totals
    private BigDecimal totalBrokerage;
    private Long totalQuantity;
    private BigDecimal totalTransactionValue;
    private Integer totalTransactions;
    
    // Month-wise breakdown
    private List<MonthlyAnalyticsDTO> monthlyAnalytics;
    
    // Overall product totals
    private List<ProductAnalyticsDTO> overallProductTotals;
    
    // Overall city totals
    private List<CityAnalyticsDTO> overallCityTotals;
    
    // Overall merchant type totals
    private List<MerchantTypeAnalyticsDTO> overallMerchantTypeTotals;
}
