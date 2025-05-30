package com.brokerhub.brokerageapp.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for payment trends and analytics.
 * Provides insights into payment patterns over time.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTrendsDTO {
    
    /**
     * Analysis period start date
     */
    private LocalDate periodStart;
    
    /**
     * Analysis period end date
     */
    private LocalDate periodEnd;
    
    /**
     * Number of days analyzed
     */
    private Integer daysAnalyzed;
    
    /**
     * Daily payment trends
     */
    private List<DailyPaymentTrendDTO> dailyTrends;
    
    /**
     * Weekly payment trends
     */
    private List<WeeklyPaymentTrendDTO> weeklyTrends;
    
    /**
     * Overall trend summary
     */
    private PaymentTrendSummaryDTO trendSummary;

    /**
     * DTO for daily payment trends
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DailyPaymentTrendDTO {
        private LocalDate date;
        private BigDecimal totalPaymentsReceived;
        private Integer paymentsCount;
        private BigDecimal averagePaymentAmount;
        private BigDecimal newPendingAmount;
        private Integer newPendingCount;
    }

    /**
     * DTO for weekly payment trends
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WeeklyPaymentTrendDTO {
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private BigDecimal totalPaymentsReceived;
        private Integer paymentsCount;
        private BigDecimal averagePaymentAmount;
        private BigDecimal weekOverWeekChange;
        private String trend; // INCREASING, DECREASING, STABLE
    }

    /**
     * DTO for overall trend summary
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentTrendSummaryDTO {
        private BigDecimal totalPaymentsReceived;
        private Integer totalPaymentsCount;
        private BigDecimal averageDailyPayments;
        private BigDecimal peakDayAmount;
        private LocalDate peakDay;
        private String overallTrend; // IMPROVING, DECLINING, STABLE
        private BigDecimal trendPercentage;
        private String insights;
    }
}
