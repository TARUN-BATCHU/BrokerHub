package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.SubscriptionHistory;
import com.brokerhub.brokerageapp.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {
    List<SubscriptionHistory> findBySubscriptionOrderByChangedAtDesc(UserSubscription subscription);
}