package com.brokerhub.brokerageapp.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
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
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24)) // 24 hours default TTL
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Custom cache configurations with different TTLs
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Analytics caches - longer TTL (24 hours)
        cacheConfigurations.put("financialYearAnalytics", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("topPerformers", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("topBuyers", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("topSellers", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("topMerchants", defaultConfig.entryTtl(Duration.ofHours(24)));

        // Payment system caches - medium TTL (6 hours)
        cacheConfigurations.put("firmNames", defaultConfig.entryTtl(Duration.ofHours(6)));
        cacheConfigurations.put("brokeragePayments", defaultConfig.entryTtl(Duration.ofHours(6)));
        cacheConfigurations.put("pendingPayments", defaultConfig.entryTtl(Duration.ofHours(6)));
        cacheConfigurations.put("receivablePayments", defaultConfig.entryTtl(Duration.ofHours(6)));
        cacheConfigurations.put("paymentDashboard", defaultConfig.entryTtl(Duration.ofHours(6)));

        // Frequently changing data - shorter TTL (1 hour)
        cacheConfigurations.put("paymentSummary", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("paymentAlerts", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("paymentTrends", defaultConfig.entryTtl(Duration.ofHours(2)));

        // User and product data - medium TTL (12 hours)
        cacheConfigurations.put("userProfiles", defaultConfig.entryTtl(Duration.ofHours(12)));
        cacheConfigurations.put("productCatalog", defaultConfig.entryTtl(Duration.ofHours(12)));
        cacheConfigurations.put("addressLookup", defaultConfig.entryTtl(Duration.ofHours(12)));

        // Ledger data - shorter TTL (2 hours) as it changes frequently
        cacheConfigurations.put("dailyLedger", defaultConfig.entryTtl(Duration.ofHours(2)));
        cacheConfigurations.put("ledgerDetails", defaultConfig.entryTtl(Duration.ofHours(2)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
