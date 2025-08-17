package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DailyLedgerRepository extends JpaRepository<DailyLedger, Long> {

    // Multi-tenant aware queries - all include broker filtering
    DailyLedger findByBrokerBrokerIdAndDate(Long brokerId, LocalDate date);

    @Query("SELECT DISTINCT dl FROM DailyLedger dl " +
           "LEFT JOIN FETCH dl.ledgerDetails ld " +
           "LEFT JOIN FETCH ld.fromSeller fs " +
           "LEFT JOIN FETCH fs.address " +
           "WHERE dl.broker.brokerId = :brokerId AND dl.date = :date")
    Optional<DailyLedger> findByBrokerIdAndDateWithLedgerDetails(@Param("brokerId") Long brokerId, @Param("date") LocalDate date);

    @Query("SELECT DISTINCT dl FROM DailyLedger dl " +
           "LEFT JOIN FETCH dl.ledgerDetails ld " +
           "LEFT JOIN FETCH ld.fromSeller fs " +
           "LEFT JOIN FETCH fs.address " +
           "WHERE dl.broker.brokerId = :brokerId AND dl.dailyLedgerId = :id")
    Optional<DailyLedger> findByBrokerIdAndIdWithLedgerDetails(@Param("brokerId") Long brokerId, @Param("id") Long id);

    @Query("SELECT dl FROM DailyLedger dl " +
           "LEFT JOIN FETCH dl.financialYear " +
           "WHERE dl.broker.brokerId = :brokerId AND dl.date = :date")
    Optional<DailyLedger> findByBrokerIdAndDateWithFinancialYear(@Param("brokerId") Long brokerId, @Param("date") LocalDate date);

    @Query(value = "SELECT ld FROM LedgerDetails ld " +
           "LEFT JOIN FETCH ld.fromSeller fs " +
           "LEFT JOIN FETCH fs.address " +
           "WHERE ld.broker.brokerId = :brokerId AND ld.dailyLedger.date = :date",
           countQuery = "SELECT COUNT(ld) FROM LedgerDetails ld WHERE ld.broker.brokerId = :brokerId AND ld.dailyLedger.date = :date")
    Page<LedgerDetails> findLedgerDetailsByBrokerIdAndDateWithPagination(@Param("brokerId") Long brokerId, @Param("date") LocalDate date, Pageable pageable);

    List<DailyLedger> findByBrokerBrokerId(Long brokerId);

}
