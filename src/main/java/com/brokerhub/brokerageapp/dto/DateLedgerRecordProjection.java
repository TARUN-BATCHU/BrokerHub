package com.brokerhub.brokerageapp.dto;

public interface DateLedgerRecordProjection {
    Long getSellerId();
    Long getLedgerDetailsId();
    Long getBuyerId();
    Long getProductId();
    Long getQuantity();
    Long getBrokerage();
    Long getProductCost();
}