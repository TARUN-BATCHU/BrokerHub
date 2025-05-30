package com.brokerhub.brokerageapp.entity;

/**
 * Enumeration representing different payment methods available in the brokerage system.
 * Used to track how payments are made for better record keeping and reporting.
 */
public enum PaymentMethod {
    
    /**
     * Cash payment
     */
    CASH("Cash"),
    
    /**
     * Bank transfer
     */
    BANK_TRANSFER("Bank Transfer"),
    
    /**
     * Cheque payment
     */
    CHEQUE("Cheque"),
    
    /**
     * UPI payment
     */
    UPI("UPI"),
    
    /**
     * NEFT (National Electronic Funds Transfer)
     */
    NEFT("NEFT"),
    
    /**
     * RTGS (Real Time Gross Settlement)
     */
    RTGS("RTGS"),
    
    /**
     * Online payment
     */
    ONLINE("Online"),
    
    /**
     * Other payment methods
     */
    OTHER("Other");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get PaymentMethod from string value (case-insensitive)
     * @param value String value to convert
     * @return PaymentMethod enum or null if not found
     */
    public static PaymentMethod fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return PaymentMethod.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Try to match by display name
            for (PaymentMethod method : PaymentMethod.values()) {
                if (method.getDisplayName().equalsIgnoreCase(value.trim())) {
                    return method;
                }
            }
            return OTHER;
        }
    }

    /**
     * Check if the payment method is electronic
     * @return true if electronic payment method
     */
    public boolean isElectronic() {
        return this == BANK_TRANSFER || this == UPI || this == NEFT || this == RTGS || this == ONLINE;
    }

    /**
     * Check if the payment method requires bank details
     * @return true if bank details are required
     */
    public boolean requiresBankDetails() {
        return this == BANK_TRANSFER || this == NEFT || this == RTGS;
    }
}
