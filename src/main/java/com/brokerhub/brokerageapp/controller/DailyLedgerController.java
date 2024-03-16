package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.service.DailyLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/DailyLedger")
public class DailyLedgerController {

    @Autowired
    DailyLedgerService dailyLedgerService;
}
