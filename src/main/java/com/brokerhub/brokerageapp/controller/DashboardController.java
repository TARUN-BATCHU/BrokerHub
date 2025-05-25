package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.analytics.*;
import com.brokerhub.brokerageapp.service.DashboardService;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/BrokerHub/Dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get comprehensive analytics for a financial year
     * This single API provides all the analytics data including:
     * - Month-wise breakdown
     * - Product-wise analytics per month and overall
     * - City-wise analytics per month and overall with product breakdown
     * - Merchant type analytics per month and overall
     * - Overall totals for the financial year
     */
    @GetMapping("/{brokerId}/getFinancialYearAnalytics/{financialYearId}")
    public ResponseEntity<FinancialYearAnalyticsDTO> getFinancialYearAnalytics(
            @PathVariable Long brokerId,
            @PathVariable Long financialYearId) {

        try {
            log.info("Fetching analytics for broker: {} and financial year: {}", brokerId, financialYearId);

            FinancialYearAnalyticsDTO analytics = dashboardService.getFinancialYearAnalytics(brokerId, financialYearId);

            if (analytics == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(analytics);

        } catch (Exception e) {
            log.error("Error fetching analytics for broker: {} and financial year: {}", brokerId, financialYearId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Refresh analytics cache for a specific financial year
     */
    @PostMapping("/refreshCache/{financialYearId}")
    public ResponseEntity<String> refreshAnalyticsCache(@PathVariable Long financialYearId) {
        try {
            log.info("Refreshing cache for financial year: {}", financialYearId);
            dashboardService.refreshAnalyticsCache(financialYearId);
            return ResponseEntity.ok("Analytics cache refreshed successfully for financial year: " + financialYearId);
        } catch (Exception e) {
            log.error("Error refreshing cache for financial year: {}", financialYearId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error refreshing cache: " + e.getMessage());
        }
    }

    /**
     * Refresh all analytics cache
     */
    @PostMapping("/refreshAllCache")
    public ResponseEntity<String> refreshAllAnalyticsCache() {
        try {
            log.info("Refreshing all analytics cache");
            dashboardService.refreshAllAnalyticsCache();
            return ResponseEntity.ok("All analytics cache refreshed successfully");
        } catch (Exception e) {
            log.error("Error refreshing all analytics cache", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error refreshing cache: " + e.getMessage());
        }
    }

    /**
     * Get all top performers (buyers, sellers, merchants) for a financial year
     */
    @GetMapping("/{brokerId}/getTopPerformers/{financialYearId}")
    public ResponseEntity<TopPerformersDTO> getTopPerformers(
            @PathVariable Long brokerId,
            @PathVariable Long financialYearId) {

        try {
            log.info("Fetching top performers for broker: {} and financial year: {}", brokerId, financialYearId);

            TopPerformersDTO topPerformers = dashboardService.getTopPerformers(brokerId, financialYearId);

            if (topPerformers == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(topPerformers);

        } catch (Exception e) {
            log.error("Error fetching top performers for broker: {} and financial year: {}", brokerId, financialYearId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top 5 buyers by quantity for a financial year
     */
    @GetMapping("/{brokerId}/getTop5BuyersByQuantity/{financialYearId}")
    public ResponseEntity<List<TopBuyerDTO>> getTop5BuyersByQuantity(
            @PathVariable Long brokerId,
            @PathVariable Long financialYearId) {

        try {
            log.info("Fetching top 5 buyers by quantity for broker: {} and financial year: {}", brokerId, financialYearId);

            List<TopBuyerDTO> topBuyers = dashboardService.getTop5BuyersByQuantity(brokerId, financialYearId);

            return ResponseEntity.ok(topBuyers);

        } catch (Exception e) {
            log.error("Error fetching top buyers for broker: {} and financial year: {}", brokerId, financialYearId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top 5 sellers by quantity for a financial year
     */
    @GetMapping("/{brokerId}/getTop5SellersByQuantity/{financialYearId}")
    public ResponseEntity<List<TopSellerDTO>> getTop5SellersByQuantity(
            @PathVariable Long brokerId,
            @PathVariable Long financialYearId) {

        try {
            log.info("Fetching top 5 sellers by quantity for broker: {} and financial year: {}", brokerId, financialYearId);

            List<TopSellerDTO> topSellers = dashboardService.getTop5SellersByQuantity(brokerId, financialYearId);

            return ResponseEntity.ok(topSellers);

        } catch (Exception e) {
            log.error("Error fetching top sellers for broker: {} and financial year: {}", brokerId, financialYearId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top 5 merchants by brokerage amount for a financial year
     */
    @GetMapping("/{brokerId}/getTop5MerchantsByBrokerage/{financialYearId}")
    public ResponseEntity<List<TopMerchantByBrokerageDTO>> getTop5MerchantsByBrokerage(
            @PathVariable Long brokerId,
            @PathVariable Long financialYearId) {

        try {
            log.info("Fetching top 5 merchants by brokerage for broker: {} and financial year: {}", brokerId, financialYearId);

            List<TopMerchantByBrokerageDTO> topMerchants = dashboardService.getTop5MerchantsByBrokerage(brokerId, financialYearId);

            return ResponseEntity.ok(topMerchants);

        } catch (Exception e) {
            log.error("Error fetching top merchants for broker: {} and financial year: {}", brokerId, financialYearId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
