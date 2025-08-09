package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerageSummaryDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;

import java.util.List;

public interface ExcelGenerationService {
    
    byte[] generateUserBrokerageExcel(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId);
    
    byte[] generateBrokerageSummaryExcel(BrokerageSummaryDTO summary, Broker broker, Long financialYearId);
    
    byte[] generateCityBrokerageExcel(String city, List<UserBrokerageDetailDTO> cityUsers, Broker broker, Long financialYearId);
    
}