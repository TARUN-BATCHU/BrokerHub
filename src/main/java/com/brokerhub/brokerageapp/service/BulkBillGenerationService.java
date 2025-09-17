package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import java.util.List;

public interface BulkBillGenerationService {
    
    byte[] generateBulkBillsHtmlSync(List<Long> userIds, Broker broker, Long financialYearId);
    
    byte[] generateBulkBillsExcelSync(List<Long> userIds, Broker broker, Long financialYearId);
    
}