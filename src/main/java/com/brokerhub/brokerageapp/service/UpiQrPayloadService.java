package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.FinancialYear;

import java.math.BigDecimal;
import java.util.Optional;

public interface UpiQrPayloadService {
    String getDynamicQRCodeBase64(BigDecimal amount, Optional<FinancialYear> financialYear);

    String buildUpiTransactionNote(Optional<FinancialYear> financialYear);

    String formatFinancialYearShort(FinancialYear financialYear);

    int getQrDisplaySize();

    String getUpiPayeeAddress();
}
