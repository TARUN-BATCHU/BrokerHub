package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.CityWiseBagDistributionDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PdfGenerationServiceImpl implements PdfGenerationService {

    @Autowired
    FinancialYearService financialYearService;
    
    private String getQRCodeBase64() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/static/images/paytm-qr.png");
            if (inputStream == null) return null;
            
            byte[] imageBytes = inputStream.readAllBytes();
            inputStream.close();
            
            return java.util.Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            log.error("Error loading QR code image", e);
            return null;
        }
    }
    
    @Override
    public byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId) {
        return generateUserBrokerageBill(userDetail, broker, financialYearId, null);
    }
    
    @Override
    public byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage) {
        try {
            return generateSimpleBill(userDetail, broker, financialYearId, customBrokerage);
        } catch (Exception e) {
            log.error("Error generating PDF bill", e);
            throw new RuntimeException("Failed to generate PDF bill", e);
        }
    }
    
    private byte[] generateSimpleBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage) throws IOException {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html><html><head>")
            .append("<meta charset='UTF-8'>")
            .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
            .append("<title>Brokerage Bill</title>")
            .append("<style>")
            .append(getProfessionalCSS())
            .append("</style></head><body>");
        
        // Header Section
        html.append("<div class='bill-container'>")
            .append("<div class='header-section'>")
            .append("<div class='company-info'>")
            .append("<div class='company-logo'>🏢</div>")
            .append("<h1 class='company-name'>").append(broker.getBrokerageFirmName()).append("</h1>")
            .append("<p class='company-tagline'>Professional Brokerage Services</p>")
            .append("</div>")
            .append("<div class='bill-info'>")
            .append("<h2 class='bill-title'>BROKERAGE STATEMENT</h2>")
            .append("<div class='bill-meta'>")
            .append("<div class='meta-item'><span class='meta-label'>📅 Financial Year:</span> <span class='meta-value'>FY ").append(financialYearId).append("</span></div>")
            .append("<div class='meta-item'><span class='meta-label'>📄 Generated:</span> <span class='meta-value'>").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("</span></div>")
            .append("<div class='meta-item'><span class='meta-label'>🆔 Bill ID:</span> <span class='meta-value'>BH-").append(System.currentTimeMillis() % 100000).append("</span></div>")
            .append("</div></div></div>");
        
        // Client Information
        html.append("<div class='client-section'>")
            .append("<div class='section-header'><h3>📋 Bill To</h3></div>")
            .append("<div class='client-info'>")
            .append("<div class='client-detail'><span class='label'>🏪 Firm Name:</span> <span class='value'>").append(userDetail.getUserBasicInfo().getFirmName()).append("</span></div>")
            .append("<div class='client-detail'><span class='label'>👤 Owner Name:</span> <span class='value'>").append(userDetail.getUserBasicInfo().getOwnerName()).append("</span></div>")
            .append("<div class='client-detail'><span class='label'>📍 City:</span> <span class='value'>").append(userDetail.getUserBasicInfo().getCity() != null ? userDetail.getUserBasicInfo().getCity() : "N/A").append("</span></div>")
            .append("</div></div>");
        
        // Visual Summary Cards
        html.append("<div class='summary-cards'>")
            .append("<div class='card card-sold'><div class='card-icon'>📦</div><div class='card-content'><div class='card-value'>").append(userDetail.getBrokerageSummary().getTotalBagsSold()).append("</div><div class='card-label'>Bags Sold</div></div></div>")
            .append("<div class='card card-bought'><div class='card-icon'>🛒</div><div class='card-content'><div class='card-value'>").append(userDetail.getBrokerageSummary().getTotalBagsBought()).append("</div><div class='card-label'>Bags Bought</div></div></div>")
            .append("<div class='card card-earned'><div class='card-icon'>💰</div><div class='card-content'><div class='card-value'>₹").append(formatCurrency(convertToBigDecimal(userDetail.getBrokerageSummary().getTotalAmountEarned()))).append("</div><div class='card-label'>Amount Earned</div></div></div>")
            .append("<div class='card card-paid'><div class='card-icon'>💳</div><div class='card-content'><div class='card-value'>₹").append(formatCurrency(convertToBigDecimal(userDetail.getBrokerageSummary().getTotalAmountPaid()))).append("</div><div class='card-label'>Amount Paid</div></div></div>")
            .append("</div>");
        
        // Total Brokerage Highlight
        BigDecimal totalBrokerage = calculateTotalBrokerage(userDetail, customBrokerage);
        html.append("<div class='total-section'>")
            .append("<div class='total-card'>")
            .append("<div class='total-icon'>🎯</div>")
            .append("<div class='total-content'>")
            .append("<div class='total-label'>Total Brokerage Payable</div>")
            .append("<div class='total-amount'>₹").append(formatCurrency(totalBrokerage)).append("</div>")
            .append(customBrokerage != null ? "<div class='custom-rate'>@ ₹" + customBrokerage + " per bag</div>" : "")
            .append("</div></div></div>");
        
        // Transaction Details Table
        html.append("<div class='transactions-section'>")
            .append("<div class='section-header'><h3>📊 Transaction Details</h3></div>")
            .append("<div class='table-container'>")
            .append("<table class='transactions-table'>")
            .append("<thead><tr>")
            .append("<th>🔢 Transaction #</th>")
            .append("<th>📅 Date</th>")
            .append("<th>🤝 Counter Party</th>")
            .append("<th>🌾 Product</th>")
            .append("<th>📦 Quantity</th>")
            .append("<th>💵 Rate</th>")
            .append("<th>💰 Brokerage</th>")
            .append("</tr></thead><tbody>");
        
        for (UserBrokerageDetailDTO.TransactionDetail transaction : userDetail.getTransactionDetails()) {
            BigDecimal transactionBrokerage = customBrokerage != null ? 
                customBrokerage.multiply(BigDecimal.valueOf(transaction.getQuantity())) : 
                transaction.getBrokerage();
            
            html.append("<tr>")
                .append("<td class='transaction-id'>").append(transaction.getTransactionNumber()).append("</td>")
                .append("<td>").append(transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("</td>")
                .append("<td class='party-name'>").append(transaction.getCounterPartyFirmName()).append("</td>")
                .append("<td class='product-name'>").append(transaction.getProductName()).append("</td>")
                .append("<td class='quantity'>").append(transaction.getQuantity()).append(" bags</td>")
                .append("<td class='rate'>₹").append(formatCurrency(convertToBigDecimal(transaction.getProductCost()))).append("</td>")
                .append("<td class='brokerage'>₹").append(formatCurrency(transactionBrokerage)).append("</td>")
                .append("</tr>");
        }
        
        html.append("</tbody></table></div></div>");
        
        // Payment Information
        if (broker.getBankDetails() != null) {
            html.append("<div class='payment-section'>")
                .append("<div class='section-header'><h3>🏦 Payment Information</h3></div>")
                .append("<div class='payment-info'>")
                .append("<div class='payment-detail'><span class='label'>🏛️ Bank Name:</span> <span class='value'>").append(broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A").append("</span></div>")
                .append("<div class='payment-detail'><span class='label'>🔢 Account Number:</span> <span class='value account-number'>").append(broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A").append("</span></div>")
                .append("<div class='payment-detail'><span class='label'>🏷️ IFSC Code:</span> <span class='value ifsc-code'>").append(broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A").append("</span></div>")
                .append("</div></div>");
        }
        
        // Footer
        html.append("<div class='footer-section'>")
            .append("<div class='footer-content'>")
            .append("<p class='footer-text'>📞 For any queries, please contact us at your earliest convenience.</p>")
            .append("<p class='footer-text'>✅ This is a computer-generated document and does not require a signature.</p>")
            .append("<div class='footer-branding'>Powered by BrokerHub 🚀</div>")
            .append("</div></div>")
            .append("</div></body></html>");
        
        return html.toString().getBytes();
    }
    
    private String getProfessionalCSS() {
        return "* { margin: 0; padding: 0; box-sizing: border-box; }" +
               "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: white; min-height: 100vh; padding: 20px; }" +
               ".bill-container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 15px; box-shadow: 0 20px 40px rgba(0,0,0,0.1); overflow: hidden; }" +
               ".header-section { background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%); color: white; padding: 30px; display: flex; justify-content: space-between; align-items: center; }" +
               ".company-info { display: flex; align-items: center; gap: 15px; }" +
               ".company-logo { font-size: 48px; }" +
               ".company-name { font-size: 28px; font-weight: 700; margin-bottom: 5px; }" +
               ".company-tagline { font-size: 14px; opacity: 0.8; }" +
               ".bill-info { text-align: right; }" +
               ".bill-title { font-size: 24px; font-weight: 600; margin-bottom: 15px; color: #ecf0f1; }" +
               ".bill-meta { display: flex; flex-direction: column; gap: 8px; }" +
               ".meta-item { display: flex; align-items: center; gap: 10px; }" +
               ".meta-label { font-size: 12px; opacity: 0.8; }" +
               ".meta-value { font-weight: 600; background: rgba(255,255,255,0.1); padding: 4px 8px; border-radius: 4px; }" +
               ".client-section { padding: 25px 30px; border-bottom: 2px solid #ecf0f1; }" +
               ".section-header h3 { color: #2c3e50; font-size: 18px; margin-bottom: 15px; display: flex; align-items: center; gap: 8px; }" +
               ".client-info { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; }" +
               ".client-detail { display: flex; align-items: center; gap: 10px; padding: 10px; background: #f8f9fa; border-radius: 8px; }" +
               ".label { font-weight: 600; color: #495057; min-width: 120px; }" +
               ".value { color: #2c3e50; font-weight: 500; }" +
               ".summary-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; padding: 25px 30px; background: #f8f9fa; }" +
               ".card { background: white; border-radius: 12px; padding: 20px; display: flex; align-items: center; gap: 15px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); transition: transform 0.2s; }" +
               ".card:hover { transform: translateY(-2px); }" +
               ".card-icon { font-size: 32px; }" +
               ".card-content { flex: 1; }" +
               ".card-value { font-size: 24px; font-weight: 700; margin-bottom: 4px; }" +
               ".card-label { font-size: 12px; color: #6c757d; text-transform: uppercase; letter-spacing: 0.5px; }" +
               ".card-sold { border-left: 4px solid #28a745; } .card-sold .card-value { color: #28a745; }" +
               ".card-bought { border-left: 4px solid #007bff; } .card-bought .card-value { color: #007bff; }" +
               ".card-earned { border-left: 4px solid #ffc107; } .card-earned .card-value { color: #e67e22; }" +
               ".card-paid { border-left: 4px solid #dc3545; } .card-paid .card-value { color: #dc3545; }" +
               ".total-section { padding: 25px 30px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }" +
               ".total-card { background: white; border-radius: 15px; padding: 25px; display: flex; align-items: center; gap: 20px; box-shadow: 0 8px 16px rgba(0,0,0,0.1); }" +
               ".total-icon { font-size: 48px; }" +
               ".total-content { flex: 1; text-align: center; }" +
               ".total-label { font-size: 16px; color: #6c757d; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 1px; }" +
               ".total-amount { font-size: 36px; font-weight: 800; color: #2c3e50; margin-bottom: 5px; }" +
               ".custom-rate { font-size: 14px; color: #e67e22; font-weight: 600; }" +
               ".transactions-section { padding: 25px 30px; }" +
               ".table-container { overflow-x: auto; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }" +
               ".transactions-table { width: 100%; border-collapse: collapse; background: white; }" +
               ".transactions-table thead { background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%); color: white; }" +
               ".transactions-table th { padding: 15px 12px; text-align: left; font-weight: 600; font-size: 14px; }" +
               ".transactions-table td { padding: 12px; border-bottom: 1px solid #dee2e6; }" +
               ".transactions-table tbody tr:hover { background: #f8f9fa; }" +
               ".transaction-id { font-weight: 600; color: #495057; }" +
               ".party-name { color: #2c3e50; font-weight: 500; }" +
               ".product-name { color: #28a745; font-weight: 500; }" +
               ".quantity { text-align: center; font-weight: 600; }" +
               ".rate, .brokerage { text-align: right; font-weight: 600; color: #e67e22; }" +
               ".payment-section { padding: 25px 30px; background: #f8f9fa; }" +
               ".payment-info { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; }" +
               ".payment-detail { display: flex; align-items: center; gap: 10px; padding: 15px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }" +
               ".account-number, .ifsc-code { font-family: 'Courier New', monospace; background: #e9ecef; padding: 4px 8px; border-radius: 4px; }" +
               ".footer-section { background: #2c3e50; color: white; padding: 20px 30px; text-align: center; }" +
               ".footer-text { margin-bottom: 8px; font-size: 14px; opacity: 0.9; }" +
               ".footer-branding { margin-top: 15px; font-weight: 600; color: #3498db; }" +
               "@media (max-width: 768px) { .header-section { flex-direction: column; gap: 20px; text-align: center; } .bill-info { text-align: center; } .summary-cards { grid-template-columns: 1fr; } .client-info, .payment-info { grid-template-columns: 1fr; } }";
    }
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }
    
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Long) return BigDecimal.valueOf((Long) value);
        if (value instanceof Integer) return BigDecimal.valueOf((Integer) value);
        if (value instanceof Double) return BigDecimal.valueOf((Double) value);
        return BigDecimal.ZERO;
    }
    
    private BigDecimal calculateTotalBrokerage(UserBrokerageDetailDTO userDetail, BigDecimal customBrokerage) {
        if (customBrokerage == null) {
            return userDetail.getBrokerageSummary().getTotalBrokeragePayable();
        }
        
        return userDetail.getTransactionDetails().stream()
            .map(transaction -> customBrokerage.multiply(BigDecimal.valueOf(transaction.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public byte[] generateUserBrokerageBillPdf(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage) {
        return generateUserBrokerageBill(userDetail, broker, financialYearId, customBrokerage);
    }
    
    @Override
    public byte[] generatePrintOptimizedBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage, String paperSize, String orientation) {
        try {
            return generatePrintBill(userDetail, broker, financialYearId, customBrokerage, paperSize, orientation);
        } catch (Exception e) {
            log.error("Error generating print bill", e);
            throw new RuntimeException("Failed to generate print bill", e);
        }
    }

    private byte[] generatePrintBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId,
                                     BigDecimal customBrokerage, String paperSize, String orientation) throws IOException {
        StringBuilder html = new StringBuilder();
        BigDecimal totalBrokerage = BigDecimal.valueOf(0);
        html.append("<!DOCTYPE html><html><head>")
                .append("<meta charset='UTF-8'>")
                .append("<title>Print Bill</title>")
                .append("<style>")
                .append(getPrintCSS(paperSize, orientation))
                .append("</style>")
                .append("<script>")
                .append("function printBill() { window.print(); }")
                .append("</script>")
                .append("</head><body>");

        // Print Button
        html.append("<div class='no-print'>")
                .append("<button onclick='printBill()' class='print-btn'>🖨️ Print Bill</button>")
                .append("</div>");

        Optional<FinancialYear> financialYear = financialYearService.getFinancialYear(financialYearId);
        StringBuilder duration = new StringBuilder();
        if(null!=financialYear){
            LocalDate start = financialYear.get().getStart();
            LocalDate end = financialYear.get().getEnd();
            duration.append(start.getMonth().toString()).append(" ").append(start.getYear()).append(" TO ").append(end.getMonth().toString()).append(" ").append(end.getYear());
        }
        // === HEADER ===
        html.append("<div class='broker-firm-name'>")
                .append("<h1>").append(broker.getBrokerageFirmName()).append("</h1>")
                .append("</div>");
        html.append("<div class='broker-info'>")
                .append("<table>")
                .append("<tr class='broker-row'><td><strong>Broker:</strong></td><td><strong>")
                .append(broker.getBrokerName() != null ? broker.getBrokerName() : "N/A")
                .append("</strong></td></tr>")
                .append("<td><strong>FY:</strong></td><td>").append(duration).append("</td></tr>")
                .append("<tr><td><strong>Date:</strong></td><td>").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("</td>")
                .append("<td><strong>Phone:</strong></td><td>").append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A").append("</td></tr>");


        html.append("</table></div>");

        // === MERCHANT INFO (Compact & Highlighted) ===
        html.append("<div class='client-info'>")
                .append("<h3>")
                .append("Merchant Name: <strong>").append(userDetail.getUserBasicInfo().getFirmName()).append("</strong>")
                .append(" &nbsp; | &nbsp; City: <strong>")
                .append(userDetail.getUserBasicInfo().getCity() != null ? userDetail.getUserBasicInfo().getCity() : "N/A").append("</strong>")
                .append("</h3>")
                .append("</div>");

        // === TRANSACTIONS TABLE ===
        html.append("<h4 style='margin: 6px 0 4px 0; font-size: 12px;'>Transaction Details</h4>")
                .append("<table class='transactions-table'>")
                .append("<thead><tr><th>S.No</th><th>DATE</th><th>MERCHANT FIRM NAME</th><th>PRODUCT</th><th>Qty</th><th>RATE</th><th>BROKERAGE</th></tr></thead><tbody>");

        int counter = 1;
        for (UserBrokerageDetailDTO.TransactionDetail transaction : userDetail.getTransactionDetails()) {
            BigDecimal transactionBrokerage = customBrokerage != null ?
                    customBrokerage.multiply(BigDecimal.valueOf(transaction.getQuantity())) :
                    transaction.getBrokerage();
            if(customBrokerage==null){
                totalBrokerage = totalBrokerage.add(transaction.getBrokerage());
            }

            html.append("<tr>")
                    .append("<td>").append(counter++).append("</td>") // Auto sequence number
                    .append("<td>").append(transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yy"))).append("</td>")
                    .append("<td>").append(transaction.getCounterPartyFirmName()).append("</td>")
                    .append("<td>").append(transaction.getProductName()).append("</td>")
                    .append("<td>").append(transaction.getQuantity()).append("</td>")
                    .append("<td>₹").append(formatCurrency(convertToBigDecimal(transaction.getProductCost()))).append("</td>")
                    .append("<td>₹").append(formatCurrency(transactionBrokerage)).append("</td>")
                    .append("</tr>");
        }
        html.append("</tbody></table>");

        // === SUMMARY (Compact & Highlighted) ===
        long totalBagsSold = userDetail.getBrokerageSummary().getTotalBagsSold();
        long totalBagsBought = userDetail.getBrokerageSummary().getTotalBagsBought();
        long totalBags = totalBagsSold + totalBagsBought;

        BigDecimal brokeragePerBag = customBrokerage != null ? customBrokerage : null;
        BigDecimal totalPayableBrokerage;

        if (brokeragePerBag != null) {
            totalPayableBrokerage = brokeragePerBag.multiply(BigDecimal.valueOf(totalBags));
        } else {
            totalPayableBrokerage = totalBrokerage;
        }

        html.append("<table class='summary-table'>")
                .append("<thead><tr>")
                .append("<th>Bags Sold</th>")
                .append("<th>Bags Bought</th>")
                .append("<th>Total Bags</th>")
                .append("<th>Total Brokerage</th>")
                .append("</tr></thead>")
                .append("<tbody><tr>")
                .append("<td>").append(totalBagsSold).append("</td>")
                .append("<td>").append(totalBagsBought).append("</td>")
                .append("<td>").append(totalBags).append("</td>");

        if (brokeragePerBag != null) {
            html.append("<td><strong>")
                    .append(totalBags).append(" × ₹").append(formatCurrency(brokeragePerBag))
                    .append(" = ₹").append(formatCurrency(totalPayableBrokerage))
                    .append("</strong></td>");
        } else {
            html.append("<td><strong>₹").append(formatCurrency(totalPayableBrokerage)).append("</strong></td>");
        }

        html.append("</tr></tbody></table>");

        // === PAYMENT DETAILS SECTION ===
        html.append("<div class='payment-details-section'>")
                .append("<h4 style='margin: 8px 0 6px 0; font-size: 12px; text-align: center;'>💳 Payment Details</h4>")
                .append("<div class='payment-container'>");
        
        // Bank Details
        if (broker.getBankDetails() != null) {
            html.append("<div class='bank-details'>")
                    .append("<div class='payment-row'><span>Bank:</span> <strong>").append(broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A").append("</strong></div>")
                    .append("<div class='payment-row'><span>A/C No:</span> <strong>").append(broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A").append("</strong></div>")
                    .append("<div class='payment-row'><span>IFSC:</span> <strong>").append(broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A").append("</strong></div>")
                    .append("</div>");
        }
        
        // UPI Details
        html.append("<div class='upi-details'>")
                .append("<div class='payment-row'><span>Phone/UPI:</span> <strong>").append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A").append("</strong></div>")
                .append("<div class='upi-apps'>Paytm | PhonePe | GooglePay</div>")
                .append("</div>");
        
        // QR Code
        String qrBase64 = getQRCodeBase64();
        html.append("<div class='qr-section'>");
        if (qrBase64 != null) {
            html.append("<img src='data:image/png;base64,").append(qrBase64).append("' width='100' height='100' style='border: 1px solid #ccc;'/>")
                    .append("<div class='qr-amount'>₹").append(formatCurrency(totalPayableBrokerage)).append("</div>");
        } else {
            html.append("<div class='qr-placeholder'>")
                    .append("<div class='qr-text'>QR Code</div>")
                    .append("<div class='qr-amount'>₹").append(formatCurrency(totalPayableBrokerage)).append("</div>")
                    .append("</div>");
        }
        html.append("</div>")
                .append("</div></div>");

        // === FOOTER ===
        html.append("<div class='footer'>")
                .append("<p><em>Thank you for your business!</em></p>")
                .append("<p><strong>Contact:</strong> ")
                .append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A")
                .append("</div>");

        html.append("</body></html>");
        return html.toString().getBytes();
    }

    private String getPrintCSS(String paperSize, String orientation) {
        String pageSize = getPageSize(paperSize, orientation);

        return "@media print { .no-print { display: none !important; } }" +
                "@page { size: " + pageSize + "; margin: 0.4in; }" + // reduced margins
                "body { font-family: Arial, sans-serif; font-size: 10px; line-height: 1.2; color: #000; margin: 0; padding: 8px; }" +
                ".no-print { margin-bottom: 10px; }" +
                ".broker-firm-name { text-align: center; margin-bottom: 10px; padding: 8px; background-color: #52a2f2; color: black; border-radius: 4px; }" +
                ".broker-firm-name h1 { font-size: 20px; margin: 0; font-weight: bold; letter-spacing: 1px; }"+
                ".broker-row td { font-size: 14px; font-weight: bold; }"+
                ".broker-info { margin: 6px 0; border: 1px solid #000; padding: 4px; border-radius: 3px; background-color: #f9f9f9; }" +
                ".broker-info table { width: 100%; border-collapse: collapse; font-size: 10px; }" +
                ".broker-info td { padding: 2px 4px; vertical-align: top; }" +
                ".broker-info td:first-child, .broker-info td:nth-child(3) { font-weight: bold; width: 18%; }" +
                ".broker-info tr:nth-child(even) { background-color: #fdfdfd; }"+
                ".transactions-table { width: 100%; border-collapse: collapse; margin-top: 6px; }" +
                ".transactions-table th, .transactions-table td { border: 1px solid #000; padding: 2px 3px; font-size: 9px; line-height: 1.1; }"+
                ".transactions-table th { font-size: 10px; font-weight: bold; }"+
                ".transactions-table tbody tr { height: 18px; }"+
                ".transactions-table thead tr { height: 20px; }"+
                ".transactions-table th:nth-child(1), .transactions-table td:nth-child(1) { width: 4%; text-align: center; font-size: 8px; }" +  /* S.No */
                ".transactions-table th:nth-child(2), .transactions-table td:nth-child(2) { width: 8%; text-align: center; font-size: 8px; }"+  /* Date */
                ".transactions-table th:nth-child(3), .transactions-table td:nth-child(3) { width: 35%; text-align: left; font-size: 8px; padding-left: 2px; } "+                     /* Merchant Firm Name */
                ".transactions-table th:nth-child(4), .transactions-table td:nth-child(4) { width: 15%; text-align: left; font-size: 8px; padding-left: 2px; }  "+                    /* Product */
                ".transactions-table th:nth-child(5), .transactions-table td:nth-child(5) { width: 6%; text-align: center; font-size: 8px; } "+   /* Qty */
                ".transactions-table th:nth-child(6), .transactions-table td:nth-child(6) { width: 14%; text-align: right; font-size: 8px; padding-right: 2px; }"+   /* Rate */
                ".transactions-table th:nth-child(7), .transactions-table td:nth-child(7) { width: 14%; text-align: right; font-size: 8px; padding-right: 2px; }  "+ /* Brokerage */
                ".print-btn { background: #007bff; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 12px; }" +
                ".print-header { text-align: center; margin-bottom: 10px; border-bottom: 1px solid #000; padding-bottom: 5px; }" +
                ".print-header h1 { font-size: 18px; margin: 0; font-weight: bold; }" +
                ".header-info { display: grid; grid-template-columns: repeat(3, 1fr); gap: 4px; font-size: 11px; margin-top: 5px; }" +
                ".client-info { margin: 4px 0; font-size: 10px; }" +
                ".summary-table th { background-color: #f0f0f0; font-weight: bold; text-align: center; }"+
                ".summary-table td { text-align: center; }"+
                ".summary-table th, .summary-table td { padding: 3px 4px; font-size: 10px; border: 1px solid #000; line-height: 1.2; }"+
                ".summary-table { margin-top: 6px; width: 100%; border-collapse: collapse; }"+
                ".summary-table tbody tr { height: 22px; }"+
                ".summary-table thead tr { height: 24px; }"+
                ".transactions-table th { background-color: #f0f0f0; font-weight: bold; }" +
                ".total-row { background-color: #6ef59d; }" +
                ".payment-details-section { margin: 8px 0; border: 1px solid #000; padding: 6px; border-radius: 4px; background-color: #f9f9f9; }" +
                ".payment-container { display: flex; justify-content: space-between; align-items: flex-start; gap: 10px; }" +
                ".bank-details, .upi-details { flex: 1; }" +
                ".payment-row { font-size: 9px; margin-bottom: 2px; }" +
                ".payment-row span { display: inline-block; width: 50px; }" +
                ".upi-apps { font-size: 8px; color: #666; margin-top: 2px; }" +
                ".qr-section { flex: 0 0 80px; text-align: center; }" +
                ".qr-placeholder { width: 80px; height: 80px; border: 2px solid #000; display: flex; flex-direction: column; justify-content: center; align-items: center; background: white; }" +
                ".qr-text { font-size: 8px; font-weight: bold; }" +
                ".qr-amount { font-size: 20px; margin-top: 2px; }" +
                ".footer { text-align: center; margin-top: 10px; font-size: 10px; font-weight: bold; border-top: 1px solid #000; padding-top: 4px; }" +
                ".footer em { font-style: italic; font-weight: normal; display: block; margin-bottom: 3px; }" +
                ".city-distribution-table { width: 100%; border-collapse: collapse; margin-top: 6px; margin-bottom: 8px; }" +
                ".city-distribution-table th, .city-distribution-table td { border: 1px solid #000; padding: 3px 4px; font-size: 10px; line-height: 1.2; }" +
                ".city-distribution-table th { background-color: #f0f0f0; font-weight: bold; text-align: center; }" +
                ".city-distribution-table td { text-align: center; }" +
                ".city-distribution-table th:nth-child(1), .city-distribution-table td:nth-child(1) { width: 15%; }" +
                ".city-distribution-table th:nth-child(2), .city-distribution-table td:nth-child(2) { width: 60%; text-align: left; padding-left: 6px; }" +
                ".city-distribution-table th:nth-child(3), .city-distribution-table td:nth-child(3) { width: 25%; }";
    }
    
    private String getPageSize(String paperSize, String orientation) {
        String size;
        switch (paperSize.toLowerCase()) {
            case "a4": size = "A4"; break;
            case "a5": size = "A5"; break;
            case "legal": size = "legal"; break;
            case "letter": size = "letter"; break;
            default: size = "A4";
        }
        return size + " " + ("landscape".equals(orientation) ? "landscape" : "portrait");
    }
    
    @Override
    public byte[] generateCityWisePrintBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId,
                                           BigDecimal customBrokerage, String paperSize, String orientation,
                                           List<CityWiseBagDistributionDTO> cityDistribution) {
        try {
            return generateCityWiseBill(userDetail, broker, financialYearId, customBrokerage, paperSize, orientation, cityDistribution);
        } catch (Exception e) {
            log.error("Error generating city-wise print bill", e);
            throw new RuntimeException("Failed to generate city-wise print bill", e);
        }
    }
    
    private byte[] generateCityWiseBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId,
                                       BigDecimal customBrokerage, String paperSize, String orientation,
                                       List<CityWiseBagDistributionDTO> cityDistribution) throws IOException {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html><html><head>")
                .append("<meta charset='UTF-8'>")
                .append("<title>City-wise Print Bill</title>")
                .append("<style>")
                .append(getPrintCSS(paperSize, orientation))
                .append("</style>")
                .append("<script>")
                .append("function printBill() { window.print(); }")
                .append("</script>")
                .append("</head><body>");

        // Print Button
        html.append("<div class='no-print'>")
                .append("<button onclick='printBill()' class='print-btn'>🖨️ Print Bill</button>")
                .append("</div>");

        Optional<FinancialYear> financialYear = financialYearService.getFinancialYear(financialYearId);
        StringBuilder duration = new StringBuilder();
        if(financialYear.isPresent()){
            LocalDate start = financialYear.get().getStart();
            LocalDate end = financialYear.get().getEnd();
            duration.append(start.getMonth().toString()).append(" ").append(start.getYear()).append(" TO ").append(end.getMonth().toString()).append(" ").append(end.getYear());
        }
        
        // === HEADER ===
        html.append("<div class='broker-firm-name'>")
                .append("<h1>").append(broker.getBrokerageFirmName()).append("</h1>")
                .append("</div>");
        html.append("<div class='broker-info'>")
                .append("<table>")
                .append("<tr class='broker-row'><td><strong>Broker:</strong></td><td><strong>")
                .append(broker.getBrokerName() != null ? broker.getBrokerName() : "N/A")
                .append("</strong></td></tr>")
                .append("<td><strong>FY:</strong></td><td>").append(duration).append("</td></tr>")
                .append("<tr><td><strong>Date:</strong></td><td>").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("</td>")
                .append("<td><strong>Phone:</strong></td><td>").append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A").append("</td></tr>");

        if (broker.getBankDetails() != null) {
            html.append("<tr><td><strong>Bank:</strong></td><td>").append(broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A").append("</td>")
                    .append("<td><strong>A/C No:</strong></td><td>").append(broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A").append("</td></tr>")
                    .append("<tr><td><strong>IFSC:</strong></td><td>").append(broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A").append("</td>")
                    .append("<td><strong>UPI / Wallet:</strong></td><td>").append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A").append("</td></tr>");
        }
        html.append("</table></div>");

        // === MERCHANT INFO ===
        html.append("<div class='client-info'>")
                .append("<h3>")
                .append("Merchant Name: <strong>").append(userDetail.getUserBasicInfo().getFirmName()).append("</strong>")
                .append(" &nbsp; | &nbsp; City: <strong>")
                .append(userDetail.getUserBasicInfo().getCity() != null ? userDetail.getUserBasicInfo().getCity() : "N/A").append("</strong>")
                .append("</h3>")
                .append("</div>");

        // === CITY-WISE BAG DISTRIBUTION TABLE ===
        html.append("<h4 style='margin: 6px 0 4px 0; font-size: 12px;'>📍 City-wise Bag Distribution</h4>")
                .append("<table class='city-distribution-table'>")
                .append("<thead><tr><th>S.No</th><th>City Name</th><th>Bags</th></tr></thead><tbody>");
        
        int cityCounter = 1;
        for (CityWiseBagDistributionDTO city : cityDistribution) {
            html.append("<tr>")
                    .append("<td>").append(cityCounter++).append("</td>")
                    .append("<td>").append(city.getCityName()).append("</td>")
                    .append("<td>").append(city.getTotalBags()).append("</td>")
                    .append("</tr>");
        }
        html.append("</tbody></table>");

        // === SUMMARY ===
        long totalBagsSold = userDetail.getBrokerageSummary().getTotalBagsSold();
        long totalBagsBought = userDetail.getBrokerageSummary().getTotalBagsBought();
        long totalBags = totalBagsSold + totalBagsBought;

        BigDecimal brokeragePerBag = customBrokerage != null ? customBrokerage : null;
        BigDecimal totalPayableBrokerage;

        if (brokeragePerBag != null) {
            totalPayableBrokerage = brokeragePerBag.multiply(BigDecimal.valueOf(totalBags));
        } else {
            totalPayableBrokerage = userDetail.getBrokerageSummary().getTotalBrokeragePayable();
        }

        html.append("<table class='summary-table'>")
                .append("<thead><tr>")
                .append("<th>Bags Sold</th>")
                .append("<th>Bags Bought</th>")
                .append("<th>Total Bags</th>")
                .append("<th>Total Brokerage</th>")
                .append("</tr></thead>")
                .append("<tbody><tr>")
                .append("<td>").append(totalBagsSold).append("</td>")
                .append("<td>").append(totalBagsBought).append("</td>")
                .append("<td>").append(totalBags).append("</td>");

        if (brokeragePerBag != null) {
            html.append("<td><strong>")
                    .append(totalBags).append(" × ₹").append(formatCurrency(brokeragePerBag))
                    .append(" = ₹").append(formatCurrency(totalPayableBrokerage))
                    .append("</strong></td>");
        } else {
            html.append("<td><strong>₹").append(formatCurrency(totalPayableBrokerage)).append("</strong></td>");
        }

        html.append("</tr></tbody></table>");

        // === PAYMENT DETAILS SECTION ===
        html.append("<div class='payment-details-section'>")
                .append("<h4 style='margin: 8px 0 6px 0; font-size: 12px; text-align: center;'>💳 Payment Details</h4>")
                .append("<div class='payment-container'>");
        
        // Bank Details
        if (broker.getBankDetails() != null) {
            html.append("<div class='bank-details'>")
                    .append("<div class='payment-row'><span>Bank:</span> <strong>").append(broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A").append("</strong></div>")
                    .append("<div class='payment-row'><span>A/C No:</span> <strong>").append(broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A").append("</strong></div>")
                    .append("<div class='payment-row'><span>IFSC:</span> <strong>").append(broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A").append("</strong></div>")
                    .append("</div>");
        }
        
        // UPI Details
        html.append("<div class='upi-details'>")
                .append("<div class='payment-row'><span>Phone/UPI:</span> <strong>").append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A").append("</strong></div>")
                .append("<div class='upi-apps'>Paytm | PhonePe | GooglePay</div>")
                .append("</div>");
        
        // QR Code
        String qrBase64 = getQRCodeBase64();
        html.append("<div class='qr-section'>");
        if (qrBase64 != null) {
            html.append("<img src='data:image/png;base64,").append(qrBase64).append("' width='100' height='100' style='border: 1px solid #ccc;'/>")
                    .append("<div class='qr-amount'>₹").append(formatCurrency(totalPayableBrokerage)).append("</div>");
        } else {
            html.append("<div class='qr-placeholder'>")
                    .append("<div class='qr-text'>QR Code</div>")
                    .append("<div class='qr-amount'>₹").append(formatCurrency(totalPayableBrokerage)).append("</div>")
                    .append("</div>");
        }
        html.append("</div>")
                .append("</div></div>");

        // === FOOTER ===
        html.append("<div class='footer'>")
                .append("<p><em>Thank you for your business!</em></p>")
                .append("<p><strong>Contact:</strong> ")
                .append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A")
                .append("</div>");

        html.append("</body></html>");
        return html.toString().getBytes();
    }
}