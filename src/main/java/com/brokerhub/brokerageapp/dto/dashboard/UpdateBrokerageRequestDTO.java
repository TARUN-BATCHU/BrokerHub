package com.brokerhub.brokerageapp.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBrokerageRequestDTO {
    
    @NotNull
    private Long merchantId;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal newBrokerageAmount;
    
    private String reason;
}