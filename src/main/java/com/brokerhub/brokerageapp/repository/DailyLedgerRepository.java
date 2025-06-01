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
import java.util.Optional;

public interface DailyLedgerRepository extends JpaRepository<DailyLedger, Long> {

    public DailyLedger findByDate(LocalDate date);

    @Query("SELECT DISTINCT dl FROM DailyLedger dl " +
           "LEFT JOIN FETCH dl.ledgerDetails ld " +
           "LEFT JOIN FETCH ld.fromSeller fs " +
           "LEFT JOIN FETCH fs.address " +
           "WHERE dl.date = :date")
    public Optional<DailyLedger> findByDateWithLedgerDetails(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT dl FROM DailyLedger dl " +
           "LEFT JOIN FETCH dl.ledgerDetails ld " +
           "LEFT JOIN FETCH ld.fromSeller fs " +
           "LEFT JOIN FETCH fs.address " +
           "WHERE dl.dailyLedgerId = :id")
    public Optional<DailyLedger> findByIdWithLedgerDetails(@Param("id") Long id);

    @Query("SELECT dl FROM DailyLedger dl " +
           "LEFT JOIN FETCH dl.financialYear " +
           "WHERE dl.date = :date")
    public Optional<DailyLedger> findByDateWithFinancialYear(@Param("date") LocalDate date);

    @Query(value = "SELECT ld FROM LedgerDetails ld " +
           "LEFT JOIN FETCH ld.fromSeller fs " +
           "LEFT JOIN FETCH fs.address " +
           "WHERE ld.dailyLedger.date = :date",
           countQuery = "SELECT COUNT(ld) FROM LedgerDetails ld WHERE ld.dailyLedger.date = :date")
    public Page<LedgerDetails> findLedgerDetailsByDateWithPagination(@Param("date") LocalDate date, Pageable pageable);
}
