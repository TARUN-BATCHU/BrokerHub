package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Multi-tenant aware queries - all include broker filtering
    List<Product> findByBrokerBrokerIdAndProductName(Long brokerId, String productName);
    
    List<Product> findByBrokerBrokerIdAndProductNameIgnoreCase(Long brokerId, String productName);

    List<Product> findByBrokerBrokerIdAndProductNameAndQualityAndQuantity(Long brokerId, String productName, String productQuality, Integer quantity);

    List<Product> findByBrokerBrokerId(Long brokerId);

    // Optimized queries with broker filtering
    @Query("SELECT p.productId, p.productName FROM Product p WHERE p.broker.brokerId = :brokerId")
    List<Object[]> findProductIdsAndNamesByBrokerId(@Param("brokerId") Long brokerId);

    @Query("SELECT p.productName FROM Product p WHERE p.broker.brokerId = :brokerId")
    List<String> findAllProductNamesByBrokerId(@Param("brokerId") Long brokerId);

    @Query("SELECT DISTINCT p.productName FROM Product p WHERE p.broker.brokerId = :brokerId")
    List<String> findDistinctProductNamesByBrokerId(@Param("brokerId") Long brokerId);

    @Query("SELECT p.productId, p.productName, p.productBrokerage, p.quality, p.price FROM Product p WHERE p.broker.brokerId = :brokerId")
    List<Object[]> findBasicProductInfoByBrokerId(@Param("brokerId") Long brokerId);

    @Query("SELECT p.productName, p.quality FROM Product p WHERE p.broker.brokerId = :brokerId")
    List<Object[]> findProductNamesAndQualitiesByBrokerId(@Param("brokerId") Long brokerId);

    @Query("SELECT p.productId, p.productName, p.quality, p.quantity FROM Product p WHERE p.broker.brokerId = :brokerId")
    List<Object[]> findProductNamesQualitiesAndQuantitiesWithIdsByBrokerId(@Param("brokerId") Long brokerId);

}
