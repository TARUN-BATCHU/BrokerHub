package com.brokerhub.brokerageapp.entity;

/**
 * Enumeration representing different payment statuses in the brokerage system.
 * Used across various payment entities to track the current state of payments.
 */
public enum PaymentStatus {
    
    /**
     * Payment has not been made yet
     */
    PENDING("Payment not yet made"),
    
    /**
     * Partial payment has been received, but full amount is still pending
     */
    PARTIAL_PAID("Partial payment received"),
    
    /**
     * Full payment has been completed
     */
    PAID("Fully paid"),
    
    /**
     * Payment is overdue (past the due date)
     */
    OVERDUE("Payment overdue"),
    
    /**
     * Payment is due soon (within warning period)
     */
    DUE_SOON("Payment due soon");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get status based on payment conditions
     * @param paidAmount Amount already paid
     * @param totalAmount Total amount due
     * @param dueDate Due date for payment
     * @return Appropriate PaymentStatus
     */
    public static PaymentStatus determineStatus(java.math.BigDecimal paidAmount, 
                                              java.math.BigDecimal totalAmount, 
                                              java.time.LocalDate dueDate) {
        if (paidAmount == null) paidAmount = java.math.BigDecimal.ZERO;
        if (totalAmount == null) totalAmount = java.math.BigDecimal.ZERO;
        
        // Check if fully paid
        if (paidAmount.compareTo(totalAmount) >= 0) {
            return PAID;
        }
        
        // Check if overdue
        if (dueDate != null && java.time.LocalDate.now().isAfter(dueDate)) {
            return OVERDUE;
        }
        
        // Check if due soon (within 7 days)
        if (dueDate != null && java.time.LocalDate.now().plusDays(7).isAfter(dueDate)) {
            if (paidAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                return PARTIAL_PAID;
            } else {
                return DUE_SOON;
            }
        }
        
        // Check if partial payment made
        if (paidAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            return PARTIAL_PAID;
        }
        
        return PENDING;
    }
}
