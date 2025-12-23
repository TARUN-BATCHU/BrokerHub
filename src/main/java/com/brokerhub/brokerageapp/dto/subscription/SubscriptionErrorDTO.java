package com.brokerhub.brokerageapp.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionErrorDTO {
    private String errorCode;
    private String message;
}