package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.ProductBasicInfoDTO;
import com.brokerhub.brokerageapp.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductCacheService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TenantContextService tenantContextService;

    private static final String PRODUCT_NAMES_CACHE = "productNames";
    private static final String PRODUCT_NAMES_IDS_CACHE = "productNamesAndIds";
    private static final String PRODUCT_BASIC_INFO_CACHE = "productBasicInfo";
    private static final String DISTINCT_PRODUCT_NAMES_CACHE = "distinctProductNames";
    private static final String PRODUCT_NAMES_QUALITIES_CACHE = "productNamesAndQualities";
    private static final String PRODUCT_NAMES_QUALITIES_QUANTITIES_IDS_CACHE = "productNamesQualitiesQuantitiesIds";

    /**
     * Get all product names with Redis caching (1 hour TTL) - Multi-tenant aware
     */
    @Cacheable(value = PRODUCT_NAMES_CACHE, key = "#root.target.getCurrentBrokerId()")
    public List<String> getAllProductNames() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Fetching product names from database for broker {} - cache miss", currentBrokerId);
        return productRepository.findAllProductNamesByBrokerId(currentBrokerId);
    }

    /**
     * Get distinct product names with Redis caching (1 hour TTL) - Multi-tenant aware
     */
    @Cacheable(value = DISTINCT_PRODUCT_NAMES_CACHE, key = "#root.target.getCurrentBrokerId()")
    public List<String> getDistinctProductNames() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Fetching distinct product names from database for broker {} - cache miss", currentBrokerId);
        return productRepository.findDistinctProductNamesByBrokerId(currentBrokerId);
    }

    /**
     * Get product names and IDs as HashMap with Redis caching (1 hour TTL) - Multi-tenant aware
     */
    @Cacheable(value = PRODUCT_NAMES_IDS_CACHE, key = "#root.target.getCurrentBrokerId()")
    public List<HashMap<String, Long>> getProductNamesAndIds() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Fetching product names and IDs from database for broker {} - cache miss", currentBrokerId);
        List<Object[]> results = productRepository.findProductIdsAndNamesByBrokerId(currentBrokerId);

        return results.stream()
                .map(row -> {
                    HashMap<String, Long> productInfo = new HashMap<>();
                    productInfo.put((String) row[1], (Long) row[0]); // productName -> productId
                    return productInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Helper method for cache key generation
     */
    public Long getCurrentBrokerId() {
        return tenantContextService.getCurrentBrokerId();
    }

    /**
     * Get basic product information with Redis caching (1 hour TTL) - Multi-tenant aware
     */
    @Cacheable(value = PRODUCT_BASIC_INFO_CACHE, key = "#root.target.getCurrentBrokerId()")
    public List<ProductBasicInfoDTO> getAllBasicProductInfo() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Fetching basic product info from database for broker {} - cache miss", currentBrokerId);
        List<Object[]> results = productRepository.findBasicProductInfoByBrokerId(currentBrokerId);

        return results.stream()
                .map(ProductBasicInfoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get product names and qualities with Redis caching (1 hour TTL) - Multi-tenant aware
     */
    @Cacheable(value = PRODUCT_NAMES_QUALITIES_CACHE, key = "#root.target.getCurrentBrokerId()")
    public List<ProductBasicInfoDTO> getProductNamesAndQualities() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Fetching product names and qualities from database for broker {} - cache miss", currentBrokerId);
        List<Object[]> results = productRepository.findProductNamesAndQualitiesByBrokerId(currentBrokerId);

        return results.stream()
                .map(row -> new ProductBasicInfoDTO((String) row[0], (String) row[1]))
                .collect(Collectors.toList());
    }

    /**
     * Get product names, qualities, quantities with IDs as HashMap with Redis caching (1 hour TTL) - Multi-tenant aware
     */
    @Cacheable(value = PRODUCT_NAMES_QUALITIES_QUANTITIES_IDS_CACHE, key = "#root.target.getCurrentBrokerId()")
    public List<HashMap<String, Long>> getProductNamesAndQualitiesAndQuantitiesWithIds() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Fetching product names, qualities, quantities with IDs from database for broker {} - cache miss", currentBrokerId);
        List<Object[]> results = productRepository.findProductNamesQualitiesAndQuantitiesWithIdsByBrokerId(currentBrokerId);

        return results.stream()
                .map(row -> {
                    HashMap<String, Long> productInfo = new HashMap<>();
                    Long productId = (Long) row[0];
                    String productName = (String) row[1];
                    String quality = (String) row[2];
                    Integer quantity = (Integer) row[3];

                    // Format: "productName - quality - quantity kgs : productId"
                    String formattedKey = String.format("%s - %s - %d kgs",
                        productName != null ? productName : "Unknown",
                        quality != null ? quality : "Unknown",
                        quantity != null ? quantity : 0);

                    productInfo.put(formattedKey, productId);
                    return productInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Clear all product-related caches when product data is modified
     */
    @CacheEvict(value = {PRODUCT_NAMES_CACHE, PRODUCT_NAMES_IDS_CACHE, PRODUCT_BASIC_INFO_CACHE,
                        DISTINCT_PRODUCT_NAMES_CACHE, PRODUCT_NAMES_QUALITIES_CACHE,
                        PRODUCT_NAMES_QUALITIES_QUANTITIES_IDS_CACHE}, allEntries = true)
    public void clearProductCaches() {
        log.info("Clearing all product caches due to data modification");
    }

    /**
     * Clear specific cache
     */
    @CacheEvict(value = PRODUCT_NAMES_CACHE, allEntries = true)
    public void clearProductNamesCache() {
        log.info("Clearing product names cache");
    }

    /**
     * Clear product names and IDs cache
     */
    @CacheEvict(value = PRODUCT_NAMES_IDS_CACHE, allEntries = true)
    public void clearProductNamesAndIdsCache() {
        log.info("Clearing product names and IDs cache");
    }

    /**
     * Clear basic product info cache
     */
    @CacheEvict(value = PRODUCT_BASIC_INFO_CACHE, allEntries = true)
    public void clearBasicProductInfoCache() {
        log.info("Clearing basic product info cache");
    }

    /**
     * Clear distinct product names cache
     */
    @CacheEvict(value = DISTINCT_PRODUCT_NAMES_CACHE, allEntries = true)
    public void clearDistinctProductNamesCache() {
        log.info("Clearing distinct product names cache");
    }

    /**
     * Clear product names and qualities cache
     */
    @CacheEvict(value = PRODUCT_NAMES_QUALITIES_CACHE, allEntries = true)
    public void clearProductNamesAndQualitiesCache() {
        log.info("Clearing product names and qualities cache");
    }

    /**
     * Clear product names, qualities, quantities with IDs cache
     */
    @CacheEvict(value = PRODUCT_NAMES_QUALITIES_QUANTITIES_IDS_CACHE, allEntries = true)
    public void clearProductNamesQualitiesQuantitiesIdsCache() {
        log.info("Clearing product names, qualities, quantities with IDs cache");
    }
}
