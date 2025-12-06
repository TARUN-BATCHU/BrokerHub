package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.CityWiseBagDistributionDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;

import java.math.BigDecimal;
import java.util.List;

public interface PdfGenerationService {
    
    byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId);
    
    byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, Long userId);
    
    byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage, Long userId);
    
    byte[] generateUserBrokerageBillPdf(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage);
    
    byte[] generatePrintOptimizedBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage, String paperSize, String orientation);
    
    byte[] generateCityWisePrintBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage, String paperSize, String orientation, List<CityWiseBagDistributionDTO> cityDistribution);
    
}