package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;

public interface PdfGenerationService {
    
    byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId);
    
}