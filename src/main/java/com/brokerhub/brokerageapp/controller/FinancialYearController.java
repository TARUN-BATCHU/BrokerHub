package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.service.FinancialYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/BrokerHub/FinancialYear")
public class FinancialYearController {

    @Autowired
    FinancialYearService financialYearService;

    @PostMapping("/create")
    public ResponseEntity<String> createFinancialYear(@RequestParam LocalDate start, @RequestParam LocalDate end){
        return financialYearService.createFinancialYear(start,end);
        //TODO
        //if null then for bills logic
    }

    @GetMapping("/getAllFinancialYearIds")
    public List<Long> getAllFinancialYearIds(){
        return financialYearService.getAllFinancialYearIds();
    }
}
