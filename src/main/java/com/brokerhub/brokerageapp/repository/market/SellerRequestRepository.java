package com.brokerhub.brokerageapp.repository.market;

import com.brokerhub.brokerageapp.entity.market.SellerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRequestRepository extends JpaRepository<SellerRequest, Long> {
    Optional<SellerRequest> findByRequestId(String requestId);
}