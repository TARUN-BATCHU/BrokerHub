package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class UpiQrPayloadServiceImpl implements UpiQrPayloadService {

    @Autowired
    private UpiQrCodeService upiQrCodeService;

    @Value("${app.qr.upi.payee-address:}")
    private String upiPayeeAddress;

    @Value("${app.qr.upi.payee-name:}")
    private String upiPayeeName;

    @Value("${app.qr.upi.note-template:}")
    private String upiNoteTemplate;

    @Value("${app.qr.code.size:200}")
    private int upiQrCodeSize;

    @Override
    public String getDynamicQRCodeBase64(BigDecimal amount, Optional<FinancialYear> financialYear) {
        if (upiPayeeAddress == null || upiPayeeAddress.isBlank()) {
            return null;
        }

        String transactionNote = buildUpiTransactionNote(financialYear);
        String upiString = upiQrCodeService.buildUpiPaymentString(
                upiPayeeAddress,
                upiPayeeName,
                amount,
                transactionNote
        );

        return upiQrCodeService.generateQrCodeBase64(upiString, getQrDisplaySize(), getQrDisplaySize());
    }

    @Override
    public String buildUpiTransactionNote(Optional<FinancialYear> financialYear) {
        if (upiNoteTemplate == null || upiNoteTemplate.isBlank()) {
            return null;
        }

        if (financialYear.isPresent() && upiNoteTemplate.contains("{FY}")) {
            return upiNoteTemplate.replace("{FY}", formatFinancialYearShort(financialYear.get()));
        }

        return upiNoteTemplate;
    }

    @Override
    public String formatFinancialYearShort(FinancialYear financialYear) {
        int startYear = financialYear.getStart().getYear() % 100;
        int endYear = financialYear.getEnd().getYear() % 100;
        return String.format("%02d-%02d", startYear, endYear);
    }

    @Override
    public int getQrDisplaySize() {
        return upiQrCodeSize > 0 ? upiQrCodeSize : 200;
    }

    @Override
    public String getUpiPayeeAddress() {
        return upiPayeeAddress;
    }
}
