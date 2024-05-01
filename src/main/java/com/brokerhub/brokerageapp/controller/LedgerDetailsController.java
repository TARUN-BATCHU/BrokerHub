package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.service.LedgerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/LedgerDetails")
public class LedgerDetailsController {

    @Autowired
    LedgerDetailsService ledgerDetailsService;

    @PostMapping("/createLedgerDetails")
    public ResponseEntity<String> createLedgerDetails(@RequestBody LedgerDetailsDTO ledgerDetailsDTO){
        return ledgerDetailsService.createLedgerDetails(ledgerDetailsDTO);

    }


}
