package com.brokerhub.brokerageapp.repository.market;

import com.brokerhub.brokerageapp.entity.market.MarketProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketProductRepository extends JpaRepository<MarketProduct, Long> {
    Optional<MarketProduct> findByProductId(String productId);
}