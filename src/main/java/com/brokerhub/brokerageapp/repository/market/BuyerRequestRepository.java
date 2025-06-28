package com.brokerhub.brokerageapp.repository.market;

import com.brokerhub.brokerageapp.entity.market.BuyerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Long> {
    Optional<BuyerRequest> findByRequestId(String requestId);
}