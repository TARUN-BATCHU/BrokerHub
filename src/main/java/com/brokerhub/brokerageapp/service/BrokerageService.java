package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerageSummaryDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    

    
    byte[] generateBulkBillsHtml(List<Long> userIds, Long brokerId, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generateBulkBillsExcel(List<Long> userIds, Long brokerId, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generateBulkPrintBills(List<Long> userIds, Long brokerId, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generateCityWisePrintBill(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage, String paperSize, String orientation);
    
    String generateExcelFilename(Long userId, Long financialYearId);

    // Date-range bill methods
    UserBrokerageDetailDTO getUserBrokerageDetailForDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    byte[] generateUserBrokerageBillForDateRange(Long userId, LocalDate startDate, LocalDate endDate, BigDecimal customBrokerage);

    byte[] generateUserBrokerageBillPdfForDateRange(Long userId, LocalDate startDate, LocalDate endDate, BigDecimal customBrokerage);

    byte[] generatePrintOptimizedBillForDateRange(Long userId, LocalDate startDate, LocalDate endDate, BigDecimal customBrokerage, String paperSize, String orientation);

    byte[] generateUserBrokerageExcelForDateRange(Long userId, LocalDate startDate, LocalDate endDate, BigDecimal customBrokerage);

    byte[] generateCityWisePrintBillForDateRange(Long userId, LocalDate startDate, LocalDate endDate, BigDecimal customBrokerage, String paperSize, String orientation);

    byte[] generateBulkBillsHtmlForDateRange(List<Long> userIds, LocalDate startDate, LocalDate endDate, BigDecimal customBrokerage);

    byte[] generateBulkBillsExcelForDateRange(List<Long> userIds, LocalDate startDate, LocalDate endDate, BigDecimal customBrokerage);

    byte[] generateBulkPrintBillsForDateRange(List<Long> userIds, LocalDate startDate, LocalDate endDate, BigDecimal customBrokerage);
}