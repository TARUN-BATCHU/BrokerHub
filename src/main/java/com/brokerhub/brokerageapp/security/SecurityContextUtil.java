package com.brokerhub.brokerageapp.security;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityContextUtil {

    private final BrokerRepository brokerRepository;
    private final UserRepository userRepository;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && !authentication.getName().equals("anonymousUser");
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("No authenticated user found");
        }
        
        Broker broker = brokerRepository.findByUserName(username)
            .orElseThrow(() -> new RuntimeException("Broker not found"));
        
        // For subscription purposes, we'll use the first user of this broker
        // In a real scenario, you might want to handle this differently
        return userRepository.findByBroker(broker).stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No users found for broker"));
    }
}