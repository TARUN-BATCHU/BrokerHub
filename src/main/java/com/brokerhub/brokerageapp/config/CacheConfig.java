package com.brokerhub.brokerageapp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Redis cache configuration with 1 hour TTL for user caches
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Specific cache configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // User caches with 1 hour TTL
        cacheConfigurations.put("userNames", defaultConfig);
        cacheConfigurations.put("userNamesAndIds", defaultConfig);
        cacheConfigurations.put("userBasicInfo", defaultConfig);

        // Product caches with 1 hour TTL
        cacheConfigurations.put("productNames", defaultConfig);
        cacheConfigurations.put("productNamesAndIds", defaultConfig);
        cacheConfigurations.put("productBasicInfo", defaultConfig);
        cacheConfigurations.put("distinctProductNames", defaultConfig);
        cacheConfigurations.put("productNamesAndQualities", defaultConfig);

        // Other caches with different TTLs
        cacheConfigurations.put("financialYearAnalytics", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("dailyLedger", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("paymentDashboard", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "simple", matchIfMissing = true)
    public CacheManager fallbackCacheManager() {
        // Fallback to in-memory cache when Redis is not available
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
                "paymentTrends",
                // User and product data
                "userProfiles",
                "userNames",
                "userNamesAndIds",
                "userBasicInfo",
                "productCatalog",
                "productNames",
                "productNamesAndIds",
                "productBasicInfo",
                "distinctProductNames",
                "productNamesAndQualities",
                "addressLookup",
                // Ledger data
                "dailyLedger",
                "ledgerDetails",
                // Financial year cache
                "financialYear"
        );
    }
}
