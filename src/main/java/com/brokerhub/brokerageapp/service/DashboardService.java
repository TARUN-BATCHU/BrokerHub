package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.analytics.FinancialYearAnalyticsDTO;
import com.brokerhub.brokerageapp.dto.analytics.TopPerformersDTO;
import com.brokerhub.brokerageapp.dto.analytics.TopBuyerDTO;
import com.brokerhub.brokerageapp.dto.analytics.TopSellerDTO;
import com.brokerhub.brokerageapp.dto.analytics.TopMerchantByBrokerageDTO;

import java.util.List;

public interface DashboardService {

    /**
     * Get comprehensive analytics for a financial year
     * @param brokerId The broker ID requesting the analytics
     * @param financialYearId The financial year ID
     * @return Complete analytics data with month-wise breakdown
     */
    FinancialYearAnalyticsDTO getFinancialYearAnalytics(Long brokerId, Long financialYearId);

    /**
     * Get top performers for a financial year
     * @param brokerId The broker ID requesting the analytics
     * @param financialYearId The financial year ID
     * @return Top 5 buyers, sellers, and merchants by brokerage
     */
    TopPerformersDTO getTopPerformers(Long brokerId, Long financialYearId);

    /**
     * Get top 5 buyers by quantity for a financial year
     * @param brokerId The broker ID requesting the analytics
     * @param financialYearId The financial year ID
     * @return List of top 5 buyers by quantity
     */
    List<TopBuyerDTO> getTop5BuyersByQuantity(Long brokerId, Long financialYearId);

    /**
     * Get top 5 sellers by quantity for a financial year
     * @param brokerId The broker ID requesting the analytics
     * @param financialYearId The financial year ID
     * @return List of top 5 sellers by quantity
     */
    List<TopSellerDTO> getTop5SellersByQuantity(Long brokerId, Long financialYearId);

    /**
     * Get top 5 merchants by brokerage amount for a financial year
     * @param brokerId The broker ID requesting the analytics
     * @param financialYearId The financial year ID
     * @return List of top 5 merchants by brokerage amount
     */
    List<TopMerchantByBrokerageDTO> getTop5MerchantsByBrokerage(Long brokerId, Long financialYearId);

    /**
     * Refresh analytics cache for a specific financial year
     * @param financialYearId The financial year ID to refresh
     */
    void refreshAnalyticsCache(Long financialYearId);

    /**
     * Refresh all analytics cache
     */
    void refreshAllAnalyticsCache();
}
