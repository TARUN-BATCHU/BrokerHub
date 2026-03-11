package com.brokerhub.brokerageapp.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class UpiQrCodeServiceImpl implements UpiQrCodeService {

    @Override
    public String buildUpiPaymentString(String payeeAddress, String payeeName, BigDecimal amount, String transactionNote) {
        if (payeeAddress == null || payeeAddress.trim().isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder("upi://pay?pa=").append(payeeAddress.trim());

        if (payeeName != null && !payeeName.trim().isEmpty()) {
            builder.append("&pn=").append(encodeValue(payeeName.trim()));
        }

        if (amount != null) {
            builder.append("&am=").append(formatAmount(amount));
        }

        if (transactionNote != null && !transactionNote.trim().isEmpty()) {
            builder.append("&tn=").append(encodeValue(transactionNote.trim()));
        }

        return builder.toString();
    }

    public String generateQrCodeBase64(String content, int width, int height) {
        if (content == null || content.isBlank()) {
            return null;
        }

        int safeWidth = width > 0 ? width : 200;
        int safeHeight = height > 0 ? height : 200;

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, safeWidth, safeHeight);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Error generating QR code", e);
            return null;
        }
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String formatAmount(BigDecimal amount) {
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return scaled.toPlainString();
    }
}
