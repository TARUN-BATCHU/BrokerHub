package com.market.repository;

import com.market.entity.MarketProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MarketProductRepository extends JpaRepository<MarketProduct, Long> {
    List<MarketProduct> findByBrokerId(Long brokerId);
    List<MarketProduct> findByAvailableUntilGreaterThanEqual(LocalDateTime date);
    List<MarketProduct> findByBrokerIdAndAvailableUntilGreaterThanEqual(Long brokerId, LocalDateTime date);
}