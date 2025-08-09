package com.brokerhub.brokerageapp.service;

import java.util.List;

public interface BulkBillGenerationService {
    
    void generateBulkBillsForCity(String city, Long brokerId, Long financialYearId);
    
    void generateBulkBillsForUsers(List<Long> userIds, Long brokerId, Long financialYearId);
    
    void generateBulkExcelForCity(String city, Long brokerId, Long financialYearId);
    
    void generateBulkExcelForUsers(List<Long> userIds, Long brokerId, Long financialYearId);
    
}