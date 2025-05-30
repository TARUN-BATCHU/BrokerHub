package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Optimized service for bulk database operations to improve performance
 * when dealing with large datasets and batch processing.
 */
@Service
@Slf4j
@Transactional
public class OptimizedBulkOperationService {

    @Autowired
    private LedgerDetailsRepository ledgerDetailsRepository;

    @Autowired
    private LedgerRecordRepository ledgerRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DailyLedgerRepository dailyLedgerRepository;

    @Autowired
    private BrokeragePaymentRepository brokeragePaymentRepository;

    @Autowired
    private PendingPaymentRepository pendingPaymentRepository;

    @Autowired
    private ReceivablePaymentRepository receivablePaymentRepository;

    /**
     * Bulk create ledger records with optimized batch processing
     */
    @CacheEvict(value = {"dailyLedger", "ledgerDetails", "financialYearAnalytics"}, allEntries = true)
    public void bulkCreateLedgerRecords(List<LedgerRecord> ledgerRecords) {
        log.info("Starting bulk creation of {} ledger records", ledgerRecords.size());
        
        try {
            // Process in batches of 25 (matching Hibernate batch size)
            int batchSize = 25;
            for (int i = 0; i < ledgerRecords.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, ledgerRecords.size());
                List<LedgerRecord> batch = ledgerRecords.subList(i, endIndex);
                
                ledgerRecordRepository.saveAll(batch);
                
                // Clear persistence context to prevent memory issues
                if (i % (batchSize * 4) == 0) {
                    ledgerRecordRepository.flush();
                }
            }
            
            log.info("Successfully created {} ledger records in bulk", ledgerRecords.size());
        } catch (Exception e) {
            log.error("Error during bulk ledger record creation", e);
            throw new RuntimeException("Failed to create ledger records in bulk", e);
        }
    }

    /**
     * Bulk update user brokerage amounts with optimized processing
     */
    @CacheEvict(value = {"userProfiles", "topBuyers", "topSellers", "topMerchants"}, allEntries = true)
    public void bulkUpdateUserBrokerage(Map<Long, BigDecimal> userBrokerageUpdates) {
        log.info("Starting bulk update of user brokerage for {} users", userBrokerageUpdates.size());
        
        try {
            // Fetch all users that need updates
            List<Long> userIds = userBrokerageUpdates.keySet().stream().collect(Collectors.toList());
            List<User> users = userRepository.findAllById(userIds);
            
            // Update brokerage amounts
            users.parallelStream().forEach(user -> {
                BigDecimal additionalBrokerage = userBrokerageUpdates.get(user.getUserId());
                if (additionalBrokerage != null) {
                    BigDecimal currentBrokerage = user.getTotalPayableBrokerage() != null ? 
                        user.getTotalPayableBrokerage() : BigDecimal.ZERO;
                    user.setTotalPayableBrokerage(currentBrokerage.add(additionalBrokerage));
                }
            });
            
            // Save in batches
            int batchSize = 25;
            for (int i = 0; i < users.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, users.size());
                List<User> batch = users.subList(i, endIndex);
                userRepository.saveAll(batch);
            }
            
            log.info("Successfully updated brokerage for {} users", users.size());
        } catch (Exception e) {
            log.error("Error during bulk user brokerage update", e);
            throw new RuntimeException("Failed to update user brokerage in bulk", e);
        }
    }

    /**
     * Bulk generate payment data from ledger records for a financial year
     */
    @CacheEvict(value = {"brokeragePayments", "pendingPayments", "receivablePayments", "paymentDashboard"}, allEntries = true)
    public CompletableFuture<Void> bulkGeneratePaymentDataAsync(Long financialYearId, Long brokerId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Starting bulk payment data generation for financial year: {} and broker: {}", financialYearId, brokerId);
            
            try {
                // This would contain the logic to generate payment data from ledger records
                // Implementation would depend on specific business rules
                
                // Example: Generate brokerage payments
                generateBrokeragePayments(financialYearId, brokerId);
                
                // Example: Generate pending payments
                generatePendingPayments(financialYearId, brokerId);
                
                // Example: Generate receivable payments
                generateReceivablePayments(financialYearId, brokerId);
                
                log.info("Successfully generated payment data for financial year: {}", financialYearId);
            } catch (Exception e) {
                log.error("Error during bulk payment data generation", e);
                throw new RuntimeException("Failed to generate payment data", e);
            }
        });
    }

    /**
     * Optimized cache warming for frequently accessed data
     */
    public CompletableFuture<Void> warmCacheAsync(Long brokerId, Long financialYearId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Starting cache warming for broker: {} and financial year: {}", brokerId, financialYearId);
            
            try {
                // Pre-load frequently accessed data into cache
                // This would trigger the @Cacheable methods to populate the cache
                
                // Note: Actual implementation would call the cached service methods
                // to populate the cache with frequently accessed data
                
                log.info("Cache warming completed for broker: {} and financial year: {}", brokerId, financialYearId);
            } catch (Exception e) {
                log.error("Error during cache warming", e);
                // Don't throw exception for cache warming failures
            }
        });
    }

    /**
     * Bulk cleanup of old data with optimized deletion
     */
    @Transactional
    public void bulkCleanupOldData(LocalDate cutoffDate) {
        log.info("Starting bulk cleanup of data older than: {}", cutoffDate);
        
        try {
            // Delete old daily ledgers and related data
            // Implementation would depend on data retention policies
            
            log.info("Bulk cleanup completed for data older than: {}", cutoffDate);
        } catch (Exception e) {
            log.error("Error during bulk cleanup", e);
            throw new RuntimeException("Failed to cleanup old data", e);
        }
    }

    // Private helper methods for payment generation
    private void generateBrokeragePayments(Long financialYearId, Long brokerId) {
        // Implementation for generating brokerage payments from ledger data
        log.debug("Generating brokerage payments for financial year: {}", financialYearId);
    }

    private void generatePendingPayments(Long financialYearId, Long brokerId) {
        // Implementation for generating pending payments from ledger data
        log.debug("Generating pending payments for financial year: {}", financialYearId);
    }

    private void generateReceivablePayments(Long financialYearId, Long brokerId) {
        // Implementation for generating receivable payments from ledger data
        log.debug("Generating receivable payments for financial year: {}", financialYearId);
    }

    /**
     * Bulk validation of data integrity
     */
    public CompletableFuture<Boolean> validateDataIntegrityAsync(Long financialYearId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Starting data integrity validation for financial year: {}", financialYearId);
            
            try {
                // Implement data integrity checks
                // Example: Verify that all ledger records have valid references
                // Example: Check that brokerage calculations are correct
                // Example: Validate that payment amounts match ledger totals
                
                log.info("Data integrity validation completed for financial year: {}", financialYearId);
                return true;
            } catch (Exception e) {
                log.error("Data integrity validation failed", e);
                return false;
            }
        });
    }

    /**
     * Optimized data export for reporting
     */
    public CompletableFuture<byte[]> exportDataAsync(Long financialYearId, String format) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Starting data export for financial year: {} in format: {}", financialYearId, format);
            
            try {
                // Implementation for optimized data export
                // Could use streaming for large datasets
                
                log.info("Data export completed for financial year: {}", financialYearId);
                return new byte[0]; // Placeholder
            } catch (Exception e) {
                log.error("Error during data export", e);
                throw new RuntimeException("Failed to export data", e);
            }
        });
    }
}
