package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerageSummaryDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;

import java.math.BigDecimal;
import java.util.List;

public interface BrokerageService {
    
    BigDecimal getTotalBrokerageInFinancialYear(Long brokerId, Long financialYearId);
    
    BrokerageSummaryDTO getBrokerageSummaryInFinancialYear(Long brokerId, Long financialYearId);
    
    BigDecimal getUserTotalBrokerageInFinancialYear(Long userId, Long brokerId, Long financialYearId);
    
    BigDecimal getCityTotalBrokerageInFinancialYear(String city, Long brokerId, Long financialYearId);
    
    UserBrokerageDetailDTO getUserBrokerageDetailInFinancialYear(Long userId, Long brokerId, Long financialYearId);
    
    byte[] generateUserBrokerageBill(Long userId, Long brokerId, Long financialYearId);
    
    byte[] generateUserBrokerageBill(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generateUserBrokerageBillPdf(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generatePrintOptimizedBill(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage, String paperSize, String orientation);
    
    byte[] generateUserBrokerageExcel(Long userId, Long brokerId, Long financialYearId);
    
    byte[] generateUserBrokerageExcel(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generateBrokerageSummaryExcel(Long brokerId, Long financialYearId);
    
    byte[] generateCityBrokerageExcel(String city, Long brokerId, Long financialYearId);
    

    
    byte[] generateBulkBillsHtml(List<Long> userIds, Long brokerId, Long financialYearId);
    
    byte[] generateBulkBillsExcel(List<Long> userIds, Long brokerId, Long financialYearId);
    
    byte[] generateCityWisePrintBill(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage, String paperSize, String orientation);
    
    String generateExcelFilename(Long userId, Long financialYearId);
}