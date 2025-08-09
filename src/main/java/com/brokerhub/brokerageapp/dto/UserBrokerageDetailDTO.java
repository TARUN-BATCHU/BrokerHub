package com.brokerhub.brokerageapp.dto;

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
public class UserBrokerageDetailDTO {
    
    private UserBasicInfo userBasicInfo;
    private BrokerageSummary brokerageSummary;
    private List<TransactionDetail> transactionDetails;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserBasicInfo {
        private String firmName;
        private String ownerName;
        private String city;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BrokerageSummary {
        private Long totalBagsSold;
        private Long totalBagsBought;
        private List<ProductSummary> productsBought;
        private List<ProductSummary> productsSold;
        private List<CitySummary> citiesSoldTo;
        private List<CitySummary> citiesBoughtFrom;
        private BigDecimal totalBrokeragePayable;
        private Long totalAmountEarned;
        private Long totalAmountPaid;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductSummary {
        private String productName;
        private Long totalBags;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CitySummary {
        private String city;
        private Long totalBags;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TransactionDetail {
        private Long transactionNumber;
        private LocalDate transactionDate;
        private String counterPartyFirmName;
        private String productName;
        private Long productCost;
        private Long quantity;
        private BigDecimal brokerage;
    }
}