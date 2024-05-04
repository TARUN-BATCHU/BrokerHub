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
}
