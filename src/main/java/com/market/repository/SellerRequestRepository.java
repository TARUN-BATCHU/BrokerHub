package com.market.repository;

import com.market.entity.SellerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerRequestRepository extends JpaRepository<SellerRequest, Long> {
    List<SellerRequest> findByBrokerId(Long brokerId);
    List<SellerRequest> findBySellerId(Long sellerId);
    List<SellerRequest> findByBrokerIdAndStatus(Long brokerId, SellerRequest.RequestStatus status);
    List<SellerRequest> findBySellerIdAndStatus(Long sellerId, SellerRequest.RequestStatus status);
}