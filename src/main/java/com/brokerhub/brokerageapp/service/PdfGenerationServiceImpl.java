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
        try {
            return generateSimpleBill(userDetail, broker, financialYearId);
        } catch (Exception e) {
            log.error("Error generating PDF bill", e);
            throw new RuntimeException("Failed to generate PDF bill", e);
        }
    }
    
    private byte[] generateSimpleBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId) throws IOException {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html><html><head>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }")
            .append("table { width: 100%; border-collapse: collapse; margin: 10px 0; }")
            .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
            .append("th { background-color: #f2f2f2; }")
            .append(".header { text-align: center; margin-bottom: 20px; }")
            .append(".total { font-weight: bold; background-color: #f9f9f9; }")
            .append("</style></head><body>");
        
        // Header
        html.append("<div class='header'>")
            .append("<h1>BROKERAGE BILL</h1>")
            .append("<h3>").append(broker.getBrokerageFirmName()).append("</h3>")
            .append("<p>Financial Year: ").append(financialYearId).append("</p>")
            .append("<p>Generated on: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("</p>")
            .append("</div>");
        
        // User Details
        html.append("<h3>Bill To:</h3>")
            .append("<p><strong>Firm Name:</strong> ").append(userDetail.getUserBasicInfo().getFirmName()).append("</p>")
            .append("<p><strong>Owner Name:</strong> ").append(userDetail.getUserBasicInfo().getOwnerName()).append("</p>")
            .append("<p><strong>City:</strong> ").append(userDetail.getUserBasicInfo().getCity()).append("</p>");
        
        // Summary
        html.append("<h3>Brokerage Summary:</h3>")
            .append("<table>")
            .append("<tr><td>Total Bags Sold</td><td>").append(userDetail.getBrokerageSummary().getTotalBagsSold()).append("</td></tr>")
            .append("<tr><td>Total Bags Bought</td><td>").append(userDetail.getBrokerageSummary().getTotalBagsBought()).append("</td></tr>")
            .append("<tr><td>Total Amount Earned</td><td>₹").append(userDetail.getBrokerageSummary().getTotalAmountEarned()).append("</td></tr>")
            .append("<tr><td>Total Amount Paid</td><td>₹").append(userDetail.getBrokerageSummary().getTotalAmountPaid()).append("</td></tr>")
            .append("<tr class='total'><td>Total Brokerage Payable</td><td>₹").append(userDetail.getBrokerageSummary().getTotalBrokeragePayable()).append("</td></tr>")
            .append("</table>");
        
        // Transaction Details
        html.append("<h3>Transaction Details:</h3>")
            .append("<table>")
            .append("<tr><th>Transaction #</th><th>Date</th><th>Counter Party</th><th>Product</th><th>Quantity</th><th>Rate</th><th>Brokerage</th></tr>");
        
        for (UserBrokerageDetailDTO.TransactionDetail transaction : userDetail.getTransactionDetails()) {
            html.append("<tr>")
                .append("<td>").append(transaction.getTransactionNumber()).append("</td>")
                .append("<td>").append(transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("</td>")
                .append("<td>").append(transaction.getCounterPartyFirmName()).append("</td>")
                .append("<td>").append(transaction.getProductName()).append("</td>")
                .append("<td>").append(transaction.getQuantity()).append("</td>")
                .append("<td>₹").append(transaction.getProductCost()).append("</td>")
                .append("<td>₹").append(transaction.getBrokerage()).append("</td>")
                .append("</tr>");
        }
        
        html.append("</table>");
        
        // Bank Details
        if (broker.getBankDetails() != null) {
            html.append("<h3>Payment Details:</h3>")
                .append("<p><strong>Bank:</strong> ").append(broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A").append("</p>")
                .append("<p><strong>Account Number:</strong> ").append(broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A").append("</p>")
                .append("<p><strong>IFSC Code:</strong> ").append(broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A").append("</p>");
        }
        
        html.append("</body></html>");
        
        // For now, return HTML as bytes (in production, use a proper PDF library like iText)
        return html.toString().getBytes();
    }
}