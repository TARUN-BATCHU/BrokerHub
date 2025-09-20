package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface FinancialYearService {
   public ResponseEntity<String> createFinancialYear(FinancialYear financialYear, Long brokerId);

   public List<Long> getAllFinancialYearIds();

   public List<FinancialYear> getAllFinancialYears();

   public Optional<FinancialYear> getFinancialYear(Long financialYearId);
}
