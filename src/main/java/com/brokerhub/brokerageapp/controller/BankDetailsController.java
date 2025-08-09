package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.service.BrokerBankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/BankDetails")
public class BankDetailsController {

    @Autowired
    BrokerBankDetailsService brokerBankDetailsService;
}
