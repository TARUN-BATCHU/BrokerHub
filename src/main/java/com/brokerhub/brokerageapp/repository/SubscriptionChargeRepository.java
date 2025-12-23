package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.SubscriptionCharge;
import com.brokerhub.brokerageapp.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionChargeRepository extends JpaRepository<SubscriptionCharge, Long> {
    List<SubscriptionCharge> findBySubscription(UserSubscription subscription);
}