package com.brokerhub.brokerageapp.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerageDashboardDTO {
    
    private BrokerageSummary summary;
    private List<MerchantBrokerageDTO> merchants;
    private List<BrokerageChartData> chartData;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrokerageSummary {
        private BigDecimal totalBrokerageReceivable;
        private BigDecimal totalBrokerageReceived;
        private BigDecimal totalBrokeragePending;
        private Long totalMerchants;
        private Long paidMerchants;
        private Long pendingMerchants;
        private Long partialPaidMerchants;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrokerageChartData {
        private String label;
        private BigDecimal value;
        private String color;
    }
}