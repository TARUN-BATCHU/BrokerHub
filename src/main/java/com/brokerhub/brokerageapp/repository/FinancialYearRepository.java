package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FinancialYearRepository extends JpaRepository<FinancialYear, Long> {

    public List<FinancialYear> findByStartAndEnd(LocalDate start, LocalDate end);

    @Query("SELECT fy FROM FinancialYear fy WHERE :start <= fy.end AND :end >= fy.start")
    List<FinancialYear> findOverlappingYears(LocalDate start, LocalDate end);

    List<FinancialYear> findByBrokerBrokerId(Long brokerId);
    
    @Query("SELECT fy FROM FinancialYear fy " +
           "WHERE fy.broker.brokerId = :brokerId " +
           "AND fy.end < CURRENT_DATE " +
           "ORDER BY fy.end DESC")
    java.util.Optional<FinancialYear> findPreviousFinancialYear(@org.springframework.data.repository.query.Param("brokerId") Long brokerId);
}
