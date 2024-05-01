package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.service.FinancialYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/BrokerHub/FinancialYear")
public class FinancialYearController {

    @Autowired
    FinancialYearService financialYearService;

    @PostMapping("/create")
    public void createFinancialYear(@RequestParam LocalDate start, LocalDate end){
        financialYearService.createFinancialYear(start,end);
    }

    @GetMapping("/getAllFinancialYearIds")
    public List<Long> getAllFinancialYearIds(){
        return financialYearService.getAllFinancialYearIds();
    }
}
