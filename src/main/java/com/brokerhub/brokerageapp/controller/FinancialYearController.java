package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.security.JwtUtil;
import com.brokerhub.brokerageapp.service.FinancialYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/BrokerHub/FinancialYear")
public class FinancialYearController {

    @Autowired
    FinancialYearService financialYearService;
    
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<String> createFinancialYear(@RequestBody FinancialYear financialYear, HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long brokerId = jwtUtil.getBrokerIdFromToken(token);
            return financialYearService.createFinancialYear(financialYear, brokerId);
        }
        return ResponseEntity.badRequest().body("Invalid or missing authorization token");
    }

    @GetMapping("/getAllFinancialYearIds")
    public List<Long> getAllFinancialYearIds(){
        return financialYearService.getAllFinancialYearIds();
    }

    @GetMapping("/getAllFinancialYears")
    public List<FinancialYear> getFinancialYearById(){
        return financialYearService.getAllFinancialYears();
    }
}
