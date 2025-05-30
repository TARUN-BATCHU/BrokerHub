package com.brokerhub.brokerageapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for monitoring application performance and providing optimization insights.
 * Tracks database performance, cache hit rates, and system metrics.
 */
@Service
@Slf4j
public class PerformanceMonitoringService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CacheManager cacheManager;

    /**
     * Monitor database performance metrics
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void monitorDatabasePerformance() {
        try {
            Map<String, Object> metrics = collectDatabaseMetrics();
            logPerformanceMetrics("Database", metrics);

            // Check for slow queries
            checkSlowQueries();

            // Monitor connection pool
            monitorConnectionPool();

        } catch (Exception e) {
            log.error("Error monitoring database performance", e);
        }
    }

    /**
     * Monitor cache performance and hit rates
     */
    @Scheduled(fixedRate = 180000) // Every 3 minutes
    public void monitorCachePerformance() {
        try {
            Map<String, Object> cacheMetrics = collectCacheMetrics();
            logPerformanceMetrics("Cache", cacheMetrics);

            // Check cache hit rates
            checkCacheHitRates(cacheMetrics);

        } catch (Exception e) {
            log.error("Error monitoring cache performance", e);
        }
    }

    /**
     * Monitor system resource usage
     */
    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void monitorSystemResources() {
        try {
            Map<String, Object> systemMetrics = collectSystemMetrics();
            logPerformanceMetrics("System", systemMetrics);

            // Check memory usage
            checkMemoryUsage(systemMetrics);

            // Check CPU usage
            checkCpuUsage(systemMetrics);

        } catch (Exception e) {
            log.error("Error monitoring system resources", e);
        }
    }

    /**
     * Collect database performance metrics
     */
    private Map<String, Object> collectDatabaseMetrics() throws SQLException {
        Map<String, Object> metrics = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // Check database connections
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SHOW STATUS LIKE 'Threads_connected'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    metrics.put("active_connections", rs.getInt("Value"));
                }
            }
            
            // Check slow queries
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SHOW STATUS LIKE 'Slow_queries'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    metrics.put("slow_queries", rs.getInt("Value"));
                }
            }
            
            // Check query cache hit rate
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SHOW STATUS LIKE 'Qcache_hits'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    metrics.put("query_cache_hits", rs.getInt("Value"));
                }
            }
            
            // Check table sizes
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT table_name, ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'size_mb' " +
                    "FROM information_schema.tables WHERE table_schema = 'brokerHub' " +
                    "ORDER BY (data_length + index_length) DESC LIMIT 10")) {
                ResultSet rs = stmt.executeQuery();
                Map<String, Double> tableSizes = new HashMap<>();
                while (rs.next()) {
                    tableSizes.put(rs.getString("table_name"), rs.getDouble("size_mb"));
                }
                metrics.put("largest_tables", tableSizes);
            }
        }
        
        return metrics;
    }

    /**
     * Collect cache performance metrics
     */
    private Map<String, Object> collectCacheMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Get cache names and basic stats
        cacheManager.getCacheNames().forEach(cacheName -> {
            try {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    // Basic cache information
                    metrics.put(cacheName + "_exists", true);
                    
                    // Note: Detailed cache statistics would depend on the cache implementation
                    // Redis cache would provide different metrics than in-memory cache
                }
            } catch (Exception e) {
                log.warn("Error collecting metrics for cache: {}", cacheName, e);
            }
        });
        
        return metrics;
    }

    /**
     * Collect system resource metrics
     */
    private Map<String, Object> collectSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        
        // Memory metrics
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        metrics.put("total_memory_mb", totalMemory / (1024 * 1024));
        metrics.put("used_memory_mb", usedMemory / (1024 * 1024));
        metrics.put("free_memory_mb", freeMemory / (1024 * 1024));
        metrics.put("max_memory_mb", maxMemory / (1024 * 1024));
        metrics.put("memory_usage_percent", (double) usedMemory / maxMemory * 100);
        
        // CPU metrics
        metrics.put("available_processors", runtime.availableProcessors());
        
        // JVM metrics
        metrics.put("active_threads", Thread.activeCount());
        
        return metrics;
    }

    /**
     * Check for slow queries and log warnings
     */
    private void checkSlowQueries() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SHOW STATUS LIKE 'Slow_queries'")) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int slowQueries = rs.getInt("Value");
                if (slowQueries > 0) {
                    log.warn("Detected {} slow queries. Consider query optimization.", slowQueries);
                }
            }
        } catch (SQLException e) {
            log.error("Error checking slow queries", e);
        }
    }

    /**
     * Monitor database connection pool
     */
    private void monitorConnectionPool() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SHOW STATUS LIKE 'Threads_connected'")) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int activeConnections = rs.getInt("Value");
                if (activeConnections > 15) { // Threshold based on our pool size of 20
                    log.warn("High number of active database connections: {}. Consider connection optimization.", activeConnections);
                }
            }
        } catch (SQLException e) {
            log.error("Error monitoring connection pool", e);
        }
    }

    /**
     * Check cache hit rates and log recommendations
     */
    private void checkCacheHitRates(Map<String, Object> cacheMetrics) {
        // Implementation would depend on cache provider
        // For Redis, we could check hit/miss ratios
        // For now, just log that cache monitoring is active
        log.debug("Cache monitoring active for {} caches", cacheMetrics.size());
    }

    /**
     * Check memory usage and log warnings
     */
    private void checkMemoryUsage(Map<String, Object> systemMetrics) {
        Double memoryUsagePercent = (Double) systemMetrics.get("memory_usage_percent");
        if (memoryUsagePercent != null && memoryUsagePercent > 80.0) {
            log.warn("High memory usage detected: {:.2f}%. Consider memory optimization.", memoryUsagePercent);
        }
    }

    /**
     * Check CPU usage patterns
     */
    private void checkCpuUsage(Map<String, Object> systemMetrics) {
        Integer availableProcessors = (Integer) systemMetrics.get("available_processors");
        Integer activeThreads = (Integer) systemMetrics.get("active_threads");
        
        if (availableProcessors != null && activeThreads != null) {
            double threadToProcessorRatio = (double) activeThreads / availableProcessors;
            if (threadToProcessorRatio > 10.0) {
                log.warn("High thread to processor ratio: {:.2f}. Consider thread pool optimization.", threadToProcessorRatio);
            }
        }
    }

    /**
     * Log performance metrics in a structured format
     */
    private void logPerformanceMetrics(String category, Map<String, Object> metrics) {
        log.info("=== {} Performance Metrics ===", category);
        metrics.forEach((key, value) -> {
            if (value instanceof Map) {
                log.info("{}: {}", key, value);
            } else {
                log.info("{}: {}", key, value);
            }
        });
        log.info("=== End {} Metrics ===", category);
    }

    /**
     * Async methods for manual performance monitoring
     */
    @Async("analyticsTaskExecutor")
    public CompletableFuture<Void> monitorDatabasePerformanceAsync() {
        return CompletableFuture.runAsync(() -> monitorDatabasePerformance());
    }

    @Async("cacheTaskExecutor")
    public CompletableFuture<Void> monitorCachePerformanceAsync() {
        return CompletableFuture.runAsync(() -> monitorCachePerformance());
    }

    @Async("analyticsTaskExecutor")
    public CompletableFuture<Void> monitorSystemResourcesAsync() {
        return CompletableFuture.runAsync(() -> monitorSystemResources());
    }

    /**
     * Generate performance report
     */
    @Async("analyticsTaskExecutor")
    public CompletableFuture<Map<String, Object>> generatePerformanceReport() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> report = new HashMap<>();
            
            try {
                report.put("database_metrics", collectDatabaseMetrics());
                report.put("cache_metrics", collectCacheMetrics());
                report.put("system_metrics", collectSystemMetrics());
                report.put("timestamp", System.currentTimeMillis());
                
                log.info("Performance report generated successfully");
            } catch (Exception e) {
                log.error("Error generating performance report", e);
                report.put("error", e.getMessage());
            }
            
            return report;
        });
    }
}
