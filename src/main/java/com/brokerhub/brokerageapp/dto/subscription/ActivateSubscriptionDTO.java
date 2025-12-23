package com.brokerhub.brokerageapp.dto.subscription;

import com.brokerhub.brokerageapp.enums.ChargeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivateSubscriptionDTO {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Plan ID is required")
    private Long planId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @Valid
    private List<ChargeDTO> charges;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChargeDTO {
        @NotNull(message = "Charge type is required")
        private ChargeType type;
        
        @NotNull(message = "Amount is required")
        private BigDecimal amount;
        
        private String description;
    }
}