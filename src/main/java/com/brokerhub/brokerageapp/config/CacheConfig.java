package com.brokerhub.brokerageapp.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        // Using simple in-memory cache manager for now
        // This will work without Redis and can be easily switched to Redis later
        return new ConcurrentMapCacheManager(
                "financialYearAnalytics",
                "topPerformers",
                "topBuyers",
                "topSellers",
                "topMerchants",
                // Payment system caches
                "firmNames",
                "brokeragePayments",
                "pendingPayments",
                "receivablePayments",
                "paymentDashboard",
                "paymentSummary",
                "paymentAlerts",
                "paymentTrends"
        );
    }
}
