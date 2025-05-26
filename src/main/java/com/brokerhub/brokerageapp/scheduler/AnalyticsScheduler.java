package com.brokerhub.brokerageapp.scheduler;

import com.brokerhub.brokerageapp.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnalyticsScheduler {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Refresh analytics cache daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshAnalyticsCache() {
        log.info("Starting scheduled analytics cache refresh");
        try {
            dashboardService.refreshAllAnalyticsCache();
            log.info("Analytics cache refresh completed successfully");
        } catch (Exception e) {
            log.error("Error during analytics cache refresh", e);
        }
    }
}
