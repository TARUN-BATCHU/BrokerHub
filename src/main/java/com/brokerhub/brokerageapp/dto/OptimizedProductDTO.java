package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptimizedProductDTO {
    
    private Long productId;
    private String productName;
}
