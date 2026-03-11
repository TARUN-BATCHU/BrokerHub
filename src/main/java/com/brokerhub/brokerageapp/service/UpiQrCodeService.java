package com.brokerhub.brokerageapp.service;

import java.math.BigDecimal;

public interface UpiQrCodeService {
    String buildUpiPaymentString(String payeeAddress, String payeeName, BigDecimal amount, String transactionNote);

    String generateQrCodeBase64(String content, int width, int height);
}
