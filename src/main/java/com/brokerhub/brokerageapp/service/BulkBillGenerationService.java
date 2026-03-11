package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import java.math.BigDecimal;
import java.util.List;

public interface BulkBillGenerationService {
    
    byte[] generateBulkBillsHtmlSync(List<Long> userIds, Broker broker, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generateBulkBillsExcelSync(List<Long> userIds, Broker broker, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generateBulkPrintBillsSync(List<Long> userIds, Broker broker, Long financialYearId, BigDecimal customBrokerage);
    
}