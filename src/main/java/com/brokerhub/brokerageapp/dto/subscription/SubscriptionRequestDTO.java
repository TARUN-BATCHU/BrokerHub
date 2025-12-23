package com.brokerhub.brokerageapp.dto.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequestDTO {
    @NotNull(message = "Plan ID is required")
    private Long planId;
}