package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import com.brokerhub.brokerageapp.service.DailyLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/BrokerHub/DailyLedger")
public class DailyLedgerController {

    @Autowired
    DailyLedgerService dailyLedgerService;

    @PostMapping("/create")
    public Long createDailyLedger(@RequestParam Long financialYearId){
        return dailyLedgerService.createDailyLedger(financialYearId);
    }

    @GetMapping("/getDailyLedger")
    public DailyLedger getDailyLedger(@RequestParam LocalDate date){
        return dailyLedgerService.getDailyLedger(date);
    }
}
