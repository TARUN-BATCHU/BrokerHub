package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.entity.UserSubscription;
import com.brokerhub.brokerageapp.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    
    @Query("SELECT us FROM UserSubscription us WHERE us.user = :user ORDER BY us.createdAt DESC LIMIT 1")
    Optional<UserSubscription> findLatestByUser(@Param("user") User user);
    
    Optional<UserSubscription> findByUserAndStatus(User user, SubscriptionStatus status);
    
    boolean existsByUserAndStatus(User user, SubscriptionStatus status);
}