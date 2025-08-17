package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to manage tenant (broker) context for multi-tenant operations.
 * This service provides methods to get the current broker from the security context
 * and ensures proper tenant isolation across all operations.
 */
@Service
@Slf4j
public class TenantContextService {

    @Autowired
    private BrokerRepository brokerRepository;

    /**
     * Get the current broker from the security context.
     * This method extracts the authenticated broker's username and fetches the broker entity.
     * 
     * @return Current broker entity
     * @throws RuntimeException if no broker is authenticated or broker not found
     */
    public Broker getCurrentBroker() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in security context");
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty in security context");
            throw new RuntimeException("Invalid authentication context");
        }

        Optional<Broker> broker = brokerRepository.findByUserName(username);
        if (broker.isEmpty()) {
            log.error("Broker not found for username: {}", username);
            throw new RuntimeException("Broker not found for username: " + username);
        }

        log.debug("Current broker resolved: {} (ID: {})", broker.get().getBrokerName(), broker.get().getBrokerId());
        return broker.get();
    }

    /**
     * Get the current broker ID from the security context.
     * 
     * @return Current broker ID
     * @throws RuntimeException if no broker is authenticated or broker not found
     */
    public Long getCurrentBrokerId() {
        return getCurrentBroker().getBrokerId();
    }

    /**
     * Get the current broker's username from the security context.
     * 
     * @return Current broker's username
     * @throws RuntimeException if no broker is authenticated
     */
    public String getCurrentBrokerUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in security context");
            throw new RuntimeException("No authenticated user found");
        }

        return authentication.getName();
    }

    /**
     * Check if the given broker ID matches the current authenticated broker.
     * This is useful for authorization checks.
     * 
     * @param brokerId The broker ID to check
     * @return true if the broker ID matches the current broker, false otherwise
     */
    public boolean isCurrentBroker(Long brokerId) {
        try {
            Long currentBrokerId = getCurrentBrokerId();
            return currentBrokerId.equals(brokerId);
        } catch (Exception e) {
            log.warn("Failed to verify current broker: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate that the current broker has access to the specified broker ID.
     * Throws an exception if access is denied.
     * 
     * @param brokerId The broker ID to validate access for
     * @throws RuntimeException if access is denied
     */
    public void validateBrokerAccess(Long brokerId) {
        if (!isCurrentBroker(brokerId)) {
            Long currentBrokerId = getCurrentBrokerId();
            log.error("Access denied: Current broker {} attempted to access data for broker {}", 
                     currentBrokerId, brokerId);
            throw new RuntimeException("Access denied: You can only access your own data");
        }
    }

    /**
     * Get the current broker with error handling.
     * Returns null if no broker is found instead of throwing an exception.
     * 
     * @return Current broker entity or null if not found
     */
    public Broker getCurrentBrokerSafe() {
        try {
            return getCurrentBroker();
        } catch (Exception e) {
            log.warn("Failed to get current broker: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if there is a valid authenticated broker in the current context.
     * 
     * @return true if there is an authenticated broker, false otherwise
     */
    public boolean hasAuthenticatedBroker() {
        try {
            getCurrentBroker();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
