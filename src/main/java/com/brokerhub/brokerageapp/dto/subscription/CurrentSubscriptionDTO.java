package com.brokerhub.brokerageapp.dto.subscription;

import com.brokerhub.brokerageapp.enums.PlanCode;
import com.brokerhub.brokerageapp.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentSubscriptionDTO {
    private PlanCode plan;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Object> features;
}