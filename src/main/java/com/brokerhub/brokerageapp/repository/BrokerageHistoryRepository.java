package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.BrokerageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrokerageHistoryRepository extends JpaRepository<BrokerageHistory, Long> {
    
    @Query("SELECT bh FROM BrokerageHistory bh " +
           "WHERE bh.broker.brokerId = :brokerId " +
           "AND bh.financialYear.yearId = :financialYearId")
    List<BrokerageHistory> findByBrokerIdAndFinancialYear(@Param("brokerId") Long brokerId, 
                                                          @Param("financialYearId") Long financialYearId);
    
    Optional<BrokerageHistory> findByMerchantUserIdAndBrokerBrokerIdAndFinancialYearYearId(
            Long merchantId, Long brokerId, Long financialYearId);
}