package com.brokerhub.brokerageapp.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for payment data validation report.
 * Provides information about data integrity issues and inconsistencies.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentValidationReportDTO {
    
    /**
     * Validation report ID
     */
    private String reportId;
    
    /**
     * Validation timestamp
     */
    private LocalDateTime validationTimestamp;
    
    /**
     * Overall validation status
     */
    private String overallStatus; // PASSED, FAILED, WARNING
    
    /**
     * Total number of records validated
     */
    private Long totalRecordsValidated;
    
    /**
     * Number of records with issues
     */
    private Long recordsWithIssues;
    
    /**
     * List of validation issues found
     */
    private List<ValidationIssueDTO> validationIssues;
    
    /**
     * Summary of validation results
     */
    private ValidationSummaryDTO validationSummary;

    /**
     * DTO for individual validation issues
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ValidationIssueDTO {
        private String issueId;
        private String issueType; // DATA_MISMATCH, MISSING_DATA, CALCULATION_ERROR, etc.
        private String severity; // LOW, MEDIUM, HIGH, CRITICAL
        private String description;
        private String tableName;
        private Long recordId;
        private String fieldName;
        private String expectedValue;
        private String actualValue;
        private String recommendedAction;
    }

    /**
     * DTO for validation summary
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ValidationSummaryDTO {
        private Long criticalIssues;
        private Long highIssues;
        private Long mediumIssues;
        private Long lowIssues;
        private Double dataIntegrityScore; // 0-100
        private String overallHealthStatus;
        private List<String> recommendations;
    }

    /**
     * Check if validation passed
     */
    public boolean isValidationPassed() {
        return "PASSED".equals(overallStatus);
    }

    /**
     * Check if there are critical issues
     */
    public boolean hasCriticalIssues() {
        return validationSummary != null && 
               validationSummary.getCriticalIssues() != null && 
               validationSummary.getCriticalIssues() > 0;
    }

    /**
     * Get data integrity percentage
     */
    public Double getDataIntegrityPercentage() {
        if (totalRecordsValidated != null && totalRecordsValidated > 0 && recordsWithIssues != null) {
            return ((double) (totalRecordsValidated - recordsWithIssues) / totalRecordsValidated) * 100;
        }
        return 100.0;
    }
}
