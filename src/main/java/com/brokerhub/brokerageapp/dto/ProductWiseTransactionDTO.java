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
public class ProductWiseTransactionDTO {
    private OptimizedProductDTO product;
    private Long totalQuantityForProduct;
    private BigDecimal totalBrokerageForProduct;
    private Long totalValueForProduct;
    private BigDecimal averagePricePerBag;
    private BigDecimal brokeragePerBag;
    private List<BuyerTransactionDTO> buyers;
}
