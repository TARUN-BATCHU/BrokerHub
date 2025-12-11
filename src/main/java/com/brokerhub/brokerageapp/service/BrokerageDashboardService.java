package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.dashboard.*;

import java.util.List;

public interface BrokerageDashboardService {
    
    BrokerageDashboardDTO getBrokerageDashboard(Long brokerId);
    
    List<MerchantBrokerageDTO> getMerchantsBrokerage(Long brokerId);
    
    void updatePaymentStatus(Long brokerId, UpdatePaymentStatusRequestDTO request);
    
    void updateBrokerageAmount(Long brokerId, UpdateBrokerageRequestDTO request);
    
    List<MerchantBrokerageDTO.PaymentHistoryDTO> getPaymentHistory(Long brokerId, Long merchantId);
    
    void calculateBrokerageForBroker(Long brokerId);
    
    CityBrokerageAnalyticsDTO getCityBrokerageAnalytics(Long brokerId, String city);
}