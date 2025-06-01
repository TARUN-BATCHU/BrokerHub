package com.brokerhub.brokerageapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

/**
 * Transaction configuration to ensure proper transaction management
 * and prevent JDBC connection commit issues.
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        
        // Set transaction timeout (30 seconds)
        transactionManager.setDefaultTimeout(30);
        
        // Enable rollback on commit failure
        transactionManager.setRollbackOnCommitFailure(true);
        
        return transactionManager;
    }
}
