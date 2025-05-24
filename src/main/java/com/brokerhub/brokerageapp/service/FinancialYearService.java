package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface FinancialYearService {
   public ResponseEntity<String> createFinancialYear(FinancialYear financialYear);

   public List<Long> getAllFinancialYearIds();

   public List<FinancialYear> getAllFinancialYears();
}
