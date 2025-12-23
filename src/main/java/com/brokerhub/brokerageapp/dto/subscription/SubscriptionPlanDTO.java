package com.brokerhub.brokerageapp.dto.subscription;

import com.brokerhub.brokerageapp.enums.BillingCycle;
import com.brokerhub.brokerageapp.enums.PlanCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlanDTO {
    private Long id;
    private PlanCode planCode;
    private String planName;
    private BigDecimal price;
    private BillingCycle billingCycle;
    private Map<String, Object> features;
}