package com.brokerhub.brokerageapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.brokerhub.brokerageapp.entity.LedgerRecord;

import java.util.List;

public interface UserBrokerageRepository extends JpaRepository<LedgerRecord, Long> {
    
    @Query("SELECT COALESCE(SUM(lr.quantity), 0) FROM LedgerRecord lr " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "AND lr.toBuyer.userId = :userId")
    Long getUserTotalBagsBought(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(lr.quantity), 0) FROM LedgerRecord lr " +
           "JOIN lr.ledgerDetails ld " +
           "WHERE lr.broker.brokerId = :brokerId AND ld.financialYearId = :financialYearId " +
           "AND ld.fromSeller.userId = :userId")
    Long getUserTotalBagsSold(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT p.productName, COALESCE(SUM(lr.quantity), 0) FROM LedgerRecord lr " +
           "JOIN lr.product p " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "AND lr.toBuyer.userId = :userId " +
           "GROUP BY p.productName")
    List<Object[]> getUserProductsBought(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT p.productName, COALESCE(SUM(lr.quantity), 0) FROM LedgerRecord lr " +
           "JOIN lr.product p JOIN lr.ledgerDetails ld " +
           "WHERE lr.broker.brokerId = :brokerId AND ld.financialYearId = :financialYearId " +
           "AND ld.fromSeller.userId = :userId " +
           "GROUP BY p.productName")
    List<Object[]> getUserProductsSold(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT a.city, COALESCE(SUM(lr.quantity), 0) FROM LedgerRecord lr " +
           "JOIN lr.ledgerDetails ld JOIN ld.fromSeller fs JOIN fs.address a " +
           "WHERE lr.broker.brokerId = :brokerId AND ld.financialYearId = :financialYearId " +
           "AND lr.toBuyer.userId = :userId " +
           "GROUP BY a.city")
    List<Object[]> getUserCitiesBoughtFrom(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT a.city, COALESCE(SUM(lr.quantity), 0) FROM LedgerRecord lr " +
           "JOIN lr.toBuyer tb JOIN tb.address a JOIN lr.ledgerDetails ld " +
           "WHERE lr.broker.brokerId = :brokerId AND ld.financialYearId = :financialYearId " +
           "AND ld.fromSeller.userId = :userId " +
           "GROUP BY a.city")
    List<Object[]> getUserCitiesSoldTo(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(lr.totalProductsCost), 0) FROM LedgerRecord lr " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "AND lr.toBuyer.userId = :userId")
    Long getUserTotalAmountPaid(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(lr.totalProductsCost), 0) FROM LedgerRecord lr " +
           "JOIN lr.ledgerDetails ld " +
           "WHERE lr.broker.brokerId = :brokerId AND ld.financialYearId = :financialYearId " +
           "AND ld.fromSeller.userId = :userId")
    Long getUserTotalAmountEarned(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT ld.brokerTransactionNumber, dl.date, " +
           "CASE WHEN lr.toBuyer.userId = :userId THEN fs.firmName ELSE tb.firmName END, " +
           "CASE WHEN lr.toBuyer.userId = :userId THEN sellerAddr.city ELSE buyerAddr.city END, " +
           "p.productName, lr.productCost, lr.quantity, lr.totalBrokerage, " +
           "CASE WHEN lr.toBuyer.userId = :userId THEN 'BOUGHT' ELSE 'SOLD' END " +
           "FROM LedgerRecord lr " +
           "JOIN lr.ledgerDetails ld " +
           "JOIN ld.dailyLedger dl " +
           "JOIN ld.fromSeller fs " +
           "JOIN fs.address sellerAddr " +
           "JOIN lr.toBuyer tb " +
           "JOIN tb.address buyerAddr " +
           "JOIN lr.product p " +
           "WHERE lr.broker.brokerId = :brokerId AND ld.financialYearId = :financialYearId " +
           "AND (lr.toBuyer.userId = :userId OR ld.fromSeller.userId = :userId) " +
           "ORDER BY ld.brokerTransactionNumber")
    List<Object[]> getUserTransactionDetails(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT " +
           "CASE WHEN lr.toBuyer.userId = :userId THEN sellerAddr.city ELSE buyerAddr.city END as cityName, " +
           "COALESCE(SUM(lr.quantity), 0) as totalBags " +
           "FROM LedgerRecord lr " +
           "JOIN lr.ledgerDetails ld " +
           "JOIN ld.fromSeller fs " +
           "JOIN fs.address sellerAddr " +
           "JOIN lr.toBuyer tb " +
           "JOIN tb.address buyerAddr " +
           "WHERE lr.broker.brokerId = :brokerId AND ld.financialYearId = :financialYearId " +
           "AND (lr.toBuyer.userId = :userId OR ld.fromSeller.userId = :userId) " +
           "GROUP BY CASE WHEN lr.toBuyer.userId = :userId THEN sellerAddr.city ELSE buyerAddr.city END " +
           "ORDER BY cityName")
    List<Object[]> getUserCityWiseBagDistribution(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
}