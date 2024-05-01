package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.FinancialYear;

import java.time.LocalDate;
import java.util.List;

public interface FinancialYearService {
   public void createFinancialYear(LocalDate start, LocalDate end);

   public List<Long> getAllFinancialYearIds();
}
