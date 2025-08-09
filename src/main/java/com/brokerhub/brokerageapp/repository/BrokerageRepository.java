package com.brokerhub.brokerageapp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.brokerhub.brokerageapp.entity.LedgerRecord;

import java.math.BigDecimal;
import java.util.List;

public interface BrokerageRepository extends JpaRepository<LedgerRecord, Long> {
    
    @Query("SELECT COALESCE(SUM(CAST(lr.totalBrokerage AS long)), 0) FROM LedgerRecord lr " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId")
    @Cacheable(value = "brokerageQuery", key = "'total_' + #brokerId + '_' + #financialYearId")
    Number getTotalBrokerageByBrokerAndFinancialYear(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId);
    
    @Query("SELECT COALESCE(SUM(CAST(lr.totalBrokerage AS long)), 0) FROM LedgerRecord lr " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "AND lr.toBuyer.userType = 'TRADER'")
    Number getTotalBrokerageFromBuyers(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId);
    
    @Query("SELECT COALESCE(SUM(CAST(lr.totalBrokerage AS long)), 0) FROM LedgerRecord lr " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "AND lr.ledgerDetails.fromSeller.userType = 'MILLER'")
    Number getTotalBrokerageFromSellers(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId);
    
    @Query("SELECT a.city, COALESCE(SUM(lr.totalBrokerage), 0) FROM LedgerRecord lr " +
           "JOIN lr.toBuyer u JOIN u.address a " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "GROUP BY a.city")
    @Cacheable(value = "brokerageQuery", key = "'citywise_' + #brokerId + '_' + #financialYearId")
    List<Object[]> getCityWiseBrokerage(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId);
    
    @Query("SELECT p.productName, COALESCE(SUM(lr.totalBrokerage), 0) FROM LedgerRecord lr " +
           "JOIN lr.product p " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "GROUP BY p.productName")
    @Cacheable(value = "brokerageQuery", key = "'productwise_' + #brokerId + '_' + #financialYearId")
    List<Object[]> getProductWiseBrokerage(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId);
    
    @Query("SELECT COALESCE(SUM(CAST(lr.totalBrokerage AS long)), 0) FROM LedgerRecord lr " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "AND lr.toBuyer.userId = :userId")
    Number getUserTotalBrokerage(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(CAST(lr.totalBrokerage AS long)), 0) FROM LedgerRecord lr " +
           "JOIN lr.toBuyer u JOIN u.address a " +
           "WHERE lr.broker.brokerId = :brokerId AND lr.ledgerDetails.financialYearId = :financialYearId " +
           "AND a.city = :city")
    Number getCityTotalBrokerage(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId, @Param("city") String city);
}
