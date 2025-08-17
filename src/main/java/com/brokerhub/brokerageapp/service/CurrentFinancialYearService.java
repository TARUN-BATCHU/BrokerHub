package com.brokerhub.brokerageapp.service;

import org.springframework.http.ResponseEntity;

public interface CurrentFinancialYearService {
    
    ResponseEntity<String> setCurrentFinancialYear(Long brokerId, Long financialYearId);
    
    Long getCurrentFinancialYearId(Long brokerId);
}