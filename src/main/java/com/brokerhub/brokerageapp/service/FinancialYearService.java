package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface FinancialYearService {
   public ResponseEntity<String> createFinancialYear(LocalDate start, LocalDate end);

   public List<Long> getAllFinancialYearIds();
}
