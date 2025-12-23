package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.SubscriptionPlan;
import com.brokerhub.brokerageapp.enums.PlanCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findByIsActiveTrue();
    Optional<SubscriptionPlan> findByPlanCode(PlanCode planCode);
}