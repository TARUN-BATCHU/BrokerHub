package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.LedgerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LedgerRecordRepository extends JpaRepository<LedgerRecord, Long> {
    
    /**
     * Find ledger records where the user is the seller (fromSeller)
     */
    @Query("SELECT lr FROM LedgerRecord lr " +
           "JOIN lr.ledgerDetails ld " +
           "WHERE ld.fromSeller.userId = :userId " +
           "AND ld.financialYearId = :financialYearId")
    List<LedgerRecord> findByFromSellerAndFinancialYear(@Param("userId") Long userId, 
                                                        @Param("financialYearId") Long financialYearId);
    
    /**
     * Find ledger records where the user is the buyer (toBuyer)
     */
    @Query("SELECT lr FROM LedgerRecord lr " +
           "WHERE lr.toBuyer.userId = :userId " +
           "AND lr.ledgerDetails.financialYearId = :financialYearId")
    List<LedgerRecord> findByToBuyerAndFinancialYear(@Param("userId") Long userId, 
                                                     @Param("financialYearId") Long financialYearId);
}
