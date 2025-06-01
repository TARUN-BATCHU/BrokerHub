package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyerTransactionDTO {
    private OptimizedUserDTO buyer;
    private Long quantity;
    private Long productCost;
    private Long totalCost;
    private BigDecimal brokerage;
    private BigDecimal totalBrokerage;
}
