package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/BrokerHub/Test")
public class TestController {

    @Autowired
    private FinancialYearRepository financialYearRepository;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Dashboard service is running!");
    }

    @GetMapping("/financialYears")
    public ResponseEntity<List<FinancialYear>> getFinancialYears() {
        List<FinancialYear> financialYears = financialYearRepository.findAll();
        return ResponseEntity.ok(financialYears);
    }

    @GetMapping("/testBasicQuery/{financialYearId}")
    public ResponseEntity<String> testBasicQuery(@PathVariable Long financialYearId) {
        try {
            // Test a simple query without JSON operations
            String result = "Financial Year ID: " + financialYearId + " - Basic query test successful";
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
