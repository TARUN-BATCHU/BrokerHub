package com.brokerhub.brokerageapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.brokerhub.brokerageapp.repository")
@EnableTransactionManagement
public class JpaConfig {
    // Spring Boot will auto-configure EntityManagerFactory, DataSource, and TransactionManager
    // This configuration class ensures proper JPA setup
}
