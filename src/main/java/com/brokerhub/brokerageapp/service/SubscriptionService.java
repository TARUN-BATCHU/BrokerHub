package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.subscription.*;
import com.brokerhub.brokerageapp.entity.User;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionPlanDTO> getAllActivePlans();
    CurrentSubscriptionDTO getCurrentSubscription(User user);
    void requestSubscription(User user, SubscriptionRequestDTO request);
    void activateSubscription(ActivateSubscriptionDTO request);
    void expireSubscription(Long userId);
    boolean hasActiveSubscription(User user);
}