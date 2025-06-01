package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserBasicInfoDTO;
import com.brokerhub.brokerageapp.repository.UserRepository;
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
public class UserCacheService {

    @Autowired
    private UserRepository userRepository;

    private static final String USER_NAMES_CACHE = "userNames";
    private static final String USER_NAMES_IDS_CACHE = "userNamesAndIds";
    private static final String USER_BASIC_INFO_CACHE = "userBasicInfo";

    /**
     * Get all user firm names with Redis caching (1 hour TTL)
     */
    @Cacheable(value = USER_NAMES_CACHE, key = "'all'")
    public List<String> getAllUserNames() {
        log.info("Fetching user names from database - cache miss");
        return userRepository.findAllFirmNames();
    }

    /**
     * Get user names and IDs as HashMap with Redis caching (1 hour TTL)
     */
    @Cacheable(value = USER_NAMES_IDS_CACHE, key = "'all'")
    public List<HashMap<String, Long>> getUserNamesAndIds() {
        log.info("Fetching user names and IDs from database - cache miss");
        List<Object[]> results = userRepository.findUserIdsAndFirmNames();
        
        return results.stream()
                .map(row -> {
                    HashMap<String, Long> userInfo = new HashMap<>();
                    userInfo.put((String) row[1], (Long) row[0]); // firmName -> userId
                    return userInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get basic user information with Redis caching (1 hour TTL)
     */
    @Cacheable(value = USER_BASIC_INFO_CACHE, key = "'all'")
    public List<UserBasicInfoDTO> getAllBasicUserInfo() {
        log.info("Fetching basic user info from database - cache miss");
        List<Object[]> results = userRepository.findBasicUserInfo();
        
        return results.stream()
                .map(UserBasicInfoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Clear all user-related caches when user data is modified
     */
    @CacheEvict(value = {USER_NAMES_CACHE, USER_NAMES_IDS_CACHE, USER_BASIC_INFO_CACHE}, allEntries = true)
    public void clearUserCaches() {
        log.info("Clearing all user caches due to data modification");
    }

    /**
     * Clear specific cache
     */
    @CacheEvict(value = USER_NAMES_CACHE, allEntries = true)
    public void clearUserNamesCache() {
        log.info("Clearing user names cache");
    }

    /**
     * Clear user names and IDs cache
     */
    @CacheEvict(value = USER_NAMES_IDS_CACHE, allEntries = true)
    public void clearUserNamesAndIdsCache() {
        log.info("Clearing user names and IDs cache");
    }

    /**
     * Clear basic user info cache
     */
    @CacheEvict(value = USER_BASIC_INFO_CACHE, allEntries = true)
    public void clearBasicUserInfoCache() {
        log.info("Clearing basic user info cache");
    }
}
