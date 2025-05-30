package com.brokerhub.brokerageapp.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for payment alerts and notifications.
 * Used to notify about overdue payments, payments due soon, etc.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentAlertDTO {
    
    /**
     * Alert ID
     */
    private String alertId;
    
    /**
     * Alert type: OVERDUE, DUE_SOON, LARGE_PAYMENT, CRITICAL
     */
    private String alertType;
    
    /**
     * Alert severity: LOW, MEDIUM, HIGH, CRITICAL
     */
    private String severity;
    
    /**
     * Alert title
     */
    private String title;
    
    /**
     * Alert message
     */
    private String message;
    
    /**
     * Payment type: BROKERAGE, PENDING, RECEIVABLE
     */
    private String paymentType;
    
    /**
     * Related payment ID
     */
    private Long paymentId;
    
    /**
     * Firm name involved
     */
    private String firmName;
    
    /**
     * Amount involved
     */
    private BigDecimal amount;
    
    /**
     * Due date
     */
    private LocalDate dueDate;
    
    /**
     * Days overdue (if applicable)
     */
    private Long daysOverdue;
    
    /**
     * Days until due (if applicable)
     */
    private Long daysUntilDue;
    
    /**
     * Alert creation date
     */
    private LocalDate alertDate;
    
    /**
     * Whether alert has been acknowledged
     */
    private Boolean acknowledged;
    
    /**
     * Action required
     */
    private String actionRequired;

    /**
     * Get formatted amount
     */
    public String getFormattedAmount() {
        return amount != null ? "₹" + amount.toString() : "₹0";
    }

    /**
     * Check if alert is critical
     */
    public boolean isCritical() {
        return "CRITICAL".equals(severity);
    }

    /**
     * Check if alert is high priority
     */
    public boolean isHighPriority() {
        return "HIGH".equals(severity) || "CRITICAL".equals(severity);
    }

    /**
     * Get alert priority score (1-10)
     */
    public int getPriorityScore() {
        switch (severity) {
            case "CRITICAL": return 10;
            case "HIGH": return 7;
            case "MEDIUM": return 4;
            case "LOW": return 1;
            default: return 1;
        }
    }

    /**
     * Get alert color for UI
     */
    public String getAlertColor() {
        switch (severity) {
            case "CRITICAL": return "#FF0000"; // Red
            case "HIGH": return "#FF6600";     // Orange
            case "MEDIUM": return "#FFCC00";   // Yellow
            case "LOW": return "#00CC00";      // Green
            default: return "#808080";         // Gray
        }
    }
}
