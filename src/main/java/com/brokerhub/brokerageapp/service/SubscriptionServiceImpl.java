package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.subscription.*;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.enums.SubscriptionStatus;
import com.brokerhub.brokerageapp.exception.InvalidOperationException;
import com.brokerhub.brokerageapp.exception.ResourceNotFoundException;
import com.brokerhub.brokerageapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository historyRepository;
    private final SubscriptionChargeRepository chargeRepository;
    private final UserRepository userRepository;

    @Override
    public List<SubscriptionPlanDTO> getAllActivePlans() {
        return planRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CurrentSubscriptionDTO getCurrentSubscription(User user) {
        UserSubscription subscription = subscriptionRepository.findLatestByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        // Check if subscription is expired
        if (subscription.getEndDate().isBefore(LocalDate.now()) && 
            subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
            throw new ResourceNotFoundException("No active subscription found");
        }

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new ResourceNotFoundException("No active subscription found");
        }

        return CurrentSubscriptionDTO.builder()
                .plan(subscription.getPlan().getPlanCode())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .features(subscription.getPlan().getFeaturesJson())
                .build();
    }

    @Override
    @Transactional
    public void requestSubscription(User user, SubscriptionRequestDTO request) {
        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));

        if (!plan.getIsActive()) {
            throw new InvalidOperationException("Selected plan is not active");
        }

        log.info("Subscription request submitted for user: {} with plan: {}", user.getUserId(), plan.getPlanCode());
    }

    @Override
    @Transactional
    public void activateSubscription(ActivateSubscriptionDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));

        // Check if user already has active subscription
        if (subscriptionRepository.existsByUserAndStatus(user, SubscriptionStatus.ACTIVE)) {
            throw new InvalidOperationException("User already has an active subscription");
        }

        // Create new subscription
        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        subscription = subscriptionRepository.save(subscription);

        // Create charges
        if (request.getCharges() != null) {
            for (ActivateSubscriptionDTO.ChargeDTO chargeDTO : request.getCharges()) {
                SubscriptionCharge charge = SubscriptionCharge.builder()
                        .subscription(subscription)
                        .chargeType(chargeDTO.getType())
                        .amount(chargeDTO.getAmount())
                        .description(chargeDTO.getDescription())
                        .build();
                chargeRepository.save(charge);
            }
        }

        // Create history record
        SubscriptionHistory history = SubscriptionHistory.builder()
                .subscription(subscription)
                .newPlan(plan)
                .changedBy("ADMIN")
                .reason("Subscription activated")
                .build();
        historyRepository.save(history);

        log.info("Subscription activated for user: {} with plan: {}", user.getUserId(), plan.getPlanCode());
    }

    @Override
    @Transactional
    public void expireSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserSubscription subscription = subscriptionRepository.findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscriptionRepository.save(subscription);

        // Create history record
        SubscriptionHistory history = SubscriptionHistory.builder()
                .subscription(subscription)
                .oldPlan(subscription.getPlan())
                .newPlan(subscription.getPlan())
                .changedBy("ADMIN")
                .reason("Subscription expired by admin")
                .build();
        historyRepository.save(history);

        log.info("Subscription expired for user: {}", userId);
    }

    @Override
    public boolean hasActiveSubscription(User user) {
        UserSubscription subscription = subscriptionRepository.findLatestByUser(user).orElse(null);
        
        if (subscription == null) {
            return false;
        }

        // Auto-expire if end date passed
        if (subscription.getEndDate().isBefore(LocalDate.now()) && 
            subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
            return false;
        }

        return subscription.getStatus() == SubscriptionStatus.ACTIVE;
    }

    private SubscriptionPlanDTO mapToDTO(SubscriptionPlan plan) {
        return SubscriptionPlanDTO.builder()
                .id(plan.getId())
                .planCode(plan.getPlanCode())
                .planName(plan.getPlanName())
                .price(plan.getBasePrice())
                .billingCycle(plan.getBillingCycle())
                .features(plan.getFeaturesJson())
                .build();
    }
}