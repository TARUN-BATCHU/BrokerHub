package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfGenerationServiceImpl implements PdfGenerationService {
    
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
               "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; padding: 20px; }" +
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
}