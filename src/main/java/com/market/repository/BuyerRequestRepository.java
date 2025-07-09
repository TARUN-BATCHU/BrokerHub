package com.market.repository;

import com.market.entity.BuyerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Long> {
    List<BuyerRequest> findByProductId(Long productId);
    List<BuyerRequest> findByBuyerId(Long buyerId);
    List<BuyerRequest> findByProductIdAndStatus(Long productId, BuyerRequest.RequestStatus status);
    List<BuyerRequest> findByBuyerIdAndStatus(Long buyerId, BuyerRequest.RequestStatus status);
}