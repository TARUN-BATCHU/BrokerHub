package com.brokerhub.brokerageapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrokerageCacheService {
    
    @Autowired
    private TenantContextService tenantContextService;
    
    @CacheEvict(value = {"totalBrokerage", "brokerageSummary", "userBrokerage", "cityBrokerage", "userBrokerageDetail", "brokerageQuery"}, 
                allEntries = true)
    public void evictBrokerageCache(Long financialYearId) {
        Long brokerId = tenantContextService.getCurrentBrokerId();
        log.info("Evicted all brokerage cache for broker {} and financial year {}", brokerId, financialYearId);
    }
    
    @CacheEvict(value = {"userBrokerage", "userBrokerageDetail"}, 
                allEntries = true)
    public void evictUserBrokerageCache(Long userId) {
        Long brokerId = tenantContextService.getCurrentBrokerId();
        log.info("Evicted user brokerage cache for broker {} and user {}", brokerId, userId);
    }
    
    @CacheEvict(value = "cityBrokerage", 
                allEntries = true)
    public void evictCityBrokerageCache(String city) {
        Long brokerId = tenantContextService.getCurrentBrokerId();
        log.info("Evicted city brokerage cache for broker {} and city {}", brokerId, city);
    }
    
    @CacheEvict(value = {"totalBrokerage", "brokerageSummary", "userBrokerage", "cityBrokerage", "userBrokerageDetail", "brokerageQuery"}, 
                allEntries = true)
    public void evictAllBrokerageCache() {
        Long brokerId = tenantContextService.getCurrentBrokerId();
        log.info("Evicted all brokerage cache for broker {}", brokerId);
    }
}