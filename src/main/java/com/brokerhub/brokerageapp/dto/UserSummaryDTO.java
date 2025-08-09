package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private Long userId;
    private String firmName;
    private String city;
    private Long totalBagsSold;
    private Long totalBagsBought;
    private BigDecimal brokeragePerBag;
    private BigDecimal totalPayableBrokerage;
}