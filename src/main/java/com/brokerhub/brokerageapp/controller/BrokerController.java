package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.service.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/Broker")
public class BrokerController {

    @Autowired
    BrokerService brokerService;
}
