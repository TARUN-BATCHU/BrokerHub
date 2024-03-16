package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.service.LedgerRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/LedgerRecord")
public class LedgerRecordController {

    @Autowired
    LedgerRecordService ledgerRecordService;
}
