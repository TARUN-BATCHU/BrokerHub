package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByProductName(String productName);

    List<Product> findByProductNameAndQualityAndQuantity(String productName,String productQuality, Integer quantity);

    // Optimized queries to fetch only required fields
    @Query("SELECT p.productId, p.productName FROM Product p")
    List<Object[]> findProductIdsAndNames();

    @Query("SELECT p.productName FROM Product p")
    List<String> findAllProductNames();

    @Query("SELECT DISTINCT p.productName FROM Product p")
    List<String> findDistinctProductNames();

    @Query("SELECT p.productId, p.productName, p.productBrokerage, p.quality, p.price FROM Product p")
    List<Object[]> findBasicProductInfo();

    @Query("SELECT p.productName, p.quality FROM Product p")
    List<Object[]> findProductNamesAndQualities();

    @Query("SELECT p.productId, p.productName, p.quality, p.quantity FROM Product p")
    List<Object[]> findProductNamesQualitiesAndQuantitiesWithIds();
}
