package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.OptimizedDailyLedgerDTO;
import com.brokerhub.brokerageapp.entity.DailyLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.time.LocalDate;

public interface DailyLedgerService {
    public Long createDailyLedger(Long financialYearId,LocalDate date);

    public Long getDailyLedgerId(LocalDate date);

    public DailyLedger getDailyLedger(LocalDate date);

    public DailyLedger getDailyLedgerByFinancialYear(LocalDate date, Long financialYearId);

    public DailyLedger getDailyLedgerWithPagination(LocalDate date, Pageable pageable);

    public OptimizedDailyLedgerDTO getDailyLedgerOptimizedWithPagination(LocalDate date, Pageable pageable);

    DailyLedger getDailyLedgerOnDate(LocalDate date) throws FileNotFoundException;

    OptimizedDailyLedgerDTO getOptimizedDailyLedger(LocalDate date);
}
