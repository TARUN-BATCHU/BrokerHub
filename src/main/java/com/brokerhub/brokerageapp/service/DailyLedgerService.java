package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.time.LocalDate;

public interface DailyLedgerService {
    public Long createDailyLedger(Long financialYearId,LocalDate date);

    public Long getDailyLedgerId(LocalDate date);

    public DailyLedger getDailyLedger(LocalDate date);

    DailyLedger getDailyLedgerOnDate(LocalDate date) throws FileNotFoundException;
}
