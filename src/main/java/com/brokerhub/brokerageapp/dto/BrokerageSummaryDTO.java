package com.brokerhub.brokerageapp.dto;

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
public class BrokerageSummaryDTO {
    
    private BigDecimal totalBrokerageEarned;
    private BigDecimal totalBrokerageFromSellers;
    private BigDecimal totalBrokerageFromBuyers;
    private List<CityBrokerageDTO> cityWiseBrokerage;
    private List<ProductBrokerageDTO> productWiseBrokerage;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CityBrokerageDTO {
        private String city;
        private BigDecimal totalBrokerage;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductBrokerageDTO {
        private String productName;
        private BigDecimal totalBrokerage;
    }
}