package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.dto.DateLedgerRecordDTO;
import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import com.brokerhub.brokerageapp.entity.LedgerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LedgerDetailsRepository extends JpaRepository<LedgerDetails, Long> {

    // Multi-tenant aware queries - all include broker filtering
    @Query(value = "SELECT ld.user_id, ld.ledger_details_id, lr.to_buyer_user_id, lr.product_product_id, lr.quantity, lr.brokerage, lr.product_cost " +
            "FROM Ledger_record lr " +
            "JOIN Ledger_details ld " +
            "ON lr.ledger_details_ledger_details_Id = ld.ledger_details_id " +
            "JOIN Daily_ledger dl " +
            "ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id " +
            "WHERE dl.date = :date AND ld.broker_id = :brokerId", nativeQuery = true)
    List<DateLedgerRecordDTO> findLedgersOnDateByBrokerId(@Param("brokerId") Long brokerId, @Param("date") LocalDate date);

    @Query("SELECT DISTINCT ld FROM LedgerDetails ld " +
           "LEFT JOIN FETCH ld.records lr " +
           "LEFT JOIN FETCH lr.toBuyer tb " +
           "LEFT JOIN FETCH tb.address " +
           "LEFT JOIN FETCH lr.product " +
           "LEFT JOIN FETCH ld.fromSeller fs " +
           "LEFT JOIN FETCH fs.address " +
           "LEFT JOIN FETCH ld.dailyLedger dl " +
           "LEFT JOIN FETCH dl.financialYear " +
           "WHERE ld.broker.brokerId = :brokerId AND ld.ledgerDetailsId = :id")
    Optional<LedgerDetails> findByBrokerIdAndIdWithAllRelations(@Param("brokerId") Long brokerId, @Param("id") Long id);

    @Query("SELECT DISTINCT ld FROM LedgerDetails ld " +
           "LEFT JOIN FETCH ld.records lr " +
           "LEFT JOIN FETCH lr.toBuyer tb " +
           "LEFT JOIN FETCH lr.product " +
           "LEFT JOIN FETCH ld.fromSeller fs " +
           "LEFT JOIN FETCH ld.dailyLedger dl " +
           "WHERE ld.broker.brokerId = :brokerId")
    List<LedgerDetails> findAllWithRecordsByBrokerId(@Param("brokerId") Long brokerId);

    List<LedgerDetails> findByBrokerBrokerId(Long brokerId);

    List<LedgerDetails> findByBrokerBrokerIdAndFromSellerUserId(Long brokerId, Long sellerId);


}
