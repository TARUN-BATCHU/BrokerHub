package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.service.LedgerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/LedgerDetails")
public class LedgerDetailsController {

    @Autowired
    LedgerDetailsService ledgerDetailsService;
}
