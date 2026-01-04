package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.CityWiseBagDistributionDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.repository.UserRepository;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PdfGenerationServiceImpl implements PdfGenerationService {

    @Autowired
    FinancialYearService financialYearService;
    
    @Autowired
    UserRepository userRepository;
    
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
        return generateUserBrokerageBill(userDetail, broker, financialYearId, (BigDecimal) null);
    }
    
    public byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, Long userId) {
        return generateUserBrokerageBill(userDetail, broker, financialYearId, (BigDecimal) null, userId);
    }
    
    @Override
    public byte[] generateUserBrokerageBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage, Long userId) {
        try {
            return generateSimpleBill(userDetail, broker, financialYearId, customBrokerage, userId);
        } catch (Exception e) {
            log.error("Error generating PDF bill", e);
            throw new RuntimeException("Failed to generate PDF bill", e);
        }
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
        return generateSimpleBill(userDetail, broker, financialYearId, customBrokerage, null);
    }
    
    private byte[] generateSimpleBill(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage, Long userId) throws IOException {
        StringBuilder html = new StringBuilder();
        
        // Get financial year details
        Optional<FinancialYear> financialYear = financialYearService.getFinancialYear(financialYearId);
        String fyDisplay = "FY " + financialYearId;
        if (financialYear.isPresent()) {
            LocalDate start = financialYear.get().getStart();
            LocalDate end = financialYear.get().getEnd();
            fyDisplay = start.getYear() + "-" + end.getYear();
        }
        
        // Get merchant's current brokerage rate if no custom brokerage provided
        BigDecimal merchantBrokerageRate = userId != null ? 
            getMerchantBrokerageRateByUserId(userId) : 
            getMerchantBrokerageRate(userDetail.getUserBasicInfo().getFirmName(), broker.getBrokerId());
        
        String billId = "BH-" + System.currentTimeMillis() % 100000;
        
        html.append("<!DOCTYPE html><html><head>")
            .append("<meta charset='UTF-8'>")
            .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
            .append("<title>Brokerage Bill</title>")
            .append("<style>")
            .append(getCompactCSS())
            .append("</style></head><body>");
        
        html.append("<div class='bill-container'>");
        
        // 1. BROKER INFO SECTION
        html.append("<div class='header-section'>")
            .append("<div class='broker-info'>")
            .append("<div class='broker-logo'></div>")
            .append("<div class='broker-details'>")
            .append("<h1 class='firm-name'> 🏢 ").append(broker.getBrokerageFirmName()).append("</h1>")
            .append("<div class='broker-name'>Proprietor: ").append(broker.getBrokerName() != null ? broker.getBrokerName() : "N/A").append("</div>")
            .append("<div class='fy-info'>Financial Year: ").append(fyDisplay).append("</div>")
            .append("</div>")
            .append("<div class='bill-meta'>")
            .append("<div class='bill-date'>📅 Bill print Date : ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("</div>")
            .append("<div class='bill-id'>🆔 Bill ID : ").append(billId).append("</div>")
            .append("</div>")
            .append("</div>");
        
        // 2. MERCHANT INFO SECTION
        html.append("<div class='merchant-section'>")
            .append("<h2 class='section-title'>📋 Merchant Information</h2>")
            .append("<div class='merchant-info'>")
            .append("<div class='info-item'><span class='label'>🏪 Firm Name:</span> <span class='value'>").append(userDetail.getUserBasicInfo().getFirmName()).append("</span></div>")
            .append("<div class='info-item'><span class='label'>📍 City:</span> <span class='value'>").append(userDetail.getUserBasicInfo().getCity() != null ? userDetail.getUserBasicInfo().getCity() : "N/A").append("</span></div>")
            .append("</div>")
            .append("</div>");
        
        // 3. SUMMARY SECTION
        long totalBagsSold = userDetail.getBrokerageSummary().getTotalBagsSold();
        long totalBagsBought = userDetail.getBrokerageSummary().getTotalBagsBought();
        long totalBags = totalBagsSold + totalBagsBought;
        BigDecimal brokeragePerBag = customBrokerage != null ? customBrokerage : merchantBrokerageRate;
        BigDecimal totalBrokerage = calculateTotalBrokerage(userDetail, customBrokerage != null ? customBrokerage : merchantBrokerageRate);
        
        html.append("<div class='summary-section'>")
            .append("<h2 class='section-title'>📊 Summary</h2>")
            .append("<div class='summary-grid'>")
            .append("<div class='summary-card sold'><div class='card-icon'>📤</div><div class='card-content'><div class='card-number'>").append(totalBagsSold).append("</div><div class='card-label'>Total Bags Sold</div></div></div>")
            .append("<div class='summary-card bought'><div class='card-icon'>📥</div><div class='card-content'><div class='card-number'>").append(totalBagsBought).append("</div><div class='card-label'>Total Bags Bought</div></div></div>")
            .append("<div class='summary-card total'><div class='card-icon'>📦</div><div class='card-content'><div class='card-number'>").append(totalBags).append("</div><div class='card-label'>Total Bags</div></div></div>")
            .append("<div class='summary-card rate'><div class='card-icon'>💰</div><div class='card-content'><div class='card-number'>₹").append(formatCurrency(brokeragePerBag)).append("</div><div class='card-label'>Brokerage per Bag</div></div></div>")
            .append("<div class='summary-card brokerage'><div class='card-icon'>🎯</div><div class='card-content'><div class='card-number'>₹").append(formatCurrency(totalBrokerage)).append("</div><div class='card-label'>Total Brokerage</div></div></div>")
            .append("</div>")
            .append("</div>");
        
        // 4. TRANSACTION DETAILS SECTION
        html.append("<div class='transactions-section'>")
            .append("<h2 class='section-title'>📋 Transaction Details</h2>")
            .append("<div class='table-wrapper'>")
            .append("<table class='transactions-table'>")
            .append("<thead><tr>")
            .append("<th>S.No</th>")
            .append("<th>Date</th>")
            .append("<th>Merchant Firm</th>")
            .append("<th>City</th>")
            .append("<th>Product</th>")
            .append("<th>Quantity</th>")
            .append("<th>Amount</th>")
            .append("<th>Brokerage</th>")
            .append("<th>Type</th>")
            .append("</tr></thead><tbody>");
        
        int sno = 1;
        for (UserBrokerageDetailDTO.TransactionDetail transaction : userDetail.getTransactionDetails()) {
            BigDecimal transactionBrokerage = customBrokerage != null ? 
                customBrokerage.multiply(BigDecimal.valueOf(transaction.getQuantity())) : 
                (merchantBrokerageRate.compareTo(BigDecimal.ZERO) > 0 ? 
                    merchantBrokerageRate.multiply(BigDecimal.valueOf(transaction.getQuantity())) : 
                    transaction.getBrokerage());
            
            // Use the transaction type from the DTO
            String transactionType = transaction.getTransactionType();
            String typeIcon;
            if ("SOLD".equals(transactionType)) {
                typeIcon = "<span class='type-sold'>↗️  Sold </span>";
            } else if ("BOUGHT".equals(transactionType)) {
                typeIcon = "<span class='type-bought'>↙️ Bought</span>";
            } else {
                // Fallback in case transactionType is null or unexpected
                typeIcon = "<span class='type-unknown'>❓ Unknown</span>";
            }
            
            html.append("<tr>")
                .append("<td class='sno'>").append(sno++).append("</td>")
                .append("<td class='date'>").append(transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("</td>")
                .append("<td class='merchant'>").append(transaction.getCounterPartyFirmName()).append("</td>")
                .append("<td class='city'>").append(transaction.getCounterPartyCity() != null ? transaction.getCounterPartyCity() : "N/A").append("</td>")
                .append("<td class='product'>").append(transaction.getProductName()).append("</td>")
                .append("<td class='quantity'>").append(transaction.getQuantity()).append("</td>")
                .append("<td class='amount'>₹").append(formatCurrency(convertToBigDecimal(transaction.getProductCost()))).append("</td>")
                .append("<td class='brokerage'>₹").append(formatCurrency(transactionBrokerage)).append("</td>")
                .append("<td class='type'>").append(typeIcon).append("</td>")
                .append("</tr>");
        }
        
        html.append("</tbody></table></div></div>");

        // 3.5 CHARTS SECTION
        html.append("<div class='charts-section'>")
                .append("<h2 class='section-title'>📊 Business Analytics</h2>")
                .append("<div class='charts-grid'>")
                // Product Distribution Chart
                .append("<div class='chart-item'>")
                .append("<h3 class='chart-title'>Product Distribution</h3>")
                .append("<div class='chart-container'>")
                .append("<canvas id='productChart'></canvas>")
                .append("</div>")
                .append("</div>")
                // City Distribution Chart
                .append("<div class='chart-item'>")
                .append("<h3 class='chart-title'>City Distribution</h3>")
                .append("<div class='chart-container'>")
                .append("<canvas id='cityChart'></canvas>")
                .append("</div>")
                .append("</div>")
                // Monthly Trend Chart
                .append("<div class='chart-item chart-full'>")
                .append("<h3 class='chart-title'>Monthly Business Trend</h3>")
                .append("<div class='chart-container'>")
                .append("<canvas id='monthlyChart'></canvas>")
                .append("</div>")
                .append("</div>")
                .append("</div>")
                .append("</div>");


        // 5. PAYMENT DETAILS SECTION
        html.append("<div class='payment-section'>")
            .append("<h2 class='section-title'>💳 Payment Details</h2>")
            .append("<div class='payment-content'>")
            .append("<div class='payment-info'>")
            .append("<div class='brokerage-amount'>")
            .append("<center> <div class='amount-label'>Total Brokerage Amount</div> </center>")
            .append("<center> <div class='amount-value'>₹").append(formatCurrency(totalBrokerage)).append("</div> </center>")
            .append("</div>");
        
        if (broker.getBankDetails() != null) {
            html.append("<div class='bank-details'>")
                .append("<h3>🏦 Bank Details</h3>")
                .append("<div class='bank-info'>")
                .append("<div class='bank-item'><span class='bank-label'>Bank Name:</span> <span class='bank-value'>").append(broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A").append("</span></div>")
                .append("<div class='bank-item'><span class='bank-label'>Account No:</span> <span class='bank-value'>").append(broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A").append("</span></div>")
                .append("<div class='bank-item'><span class='bank-label'>IFSC Code:</span> <span class='bank-value'>").append(broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A").append("</span></div>")
                .append("</div>")
                .append("</div>");
        }
        
        html.append("</div>");
        
        // QR Code section
        String qrBase64 = getQRCodeBase64();
        html.append("<div class='qr-section'>")
            .append("<h3>📱 Scan to Pay</h3>");
        if (qrBase64 != null) {
            html.append("<img src='data:image/png;base64,").append(qrBase64).append("' class='qr-code' alt='QR Code'/>")
                .append("<div class='qr-amount'>₹").append(formatCurrency(totalBrokerage)).append("</div>");
        } else {
            html.append("<div class='qr-placeholder'>")
                .append("<div class='qr-text'>QR Code</div>")
                .append("<div class='qr-amount'>₹").append(formatCurrency(totalBrokerage)).append("</div>")
                .append("</div>");
        }
        html.append("</div>");
        
        html.append("</div>"); // Close payment-section
        
        // 6. ABOUT BROKER HUB & THANKS SECTION
        html.append("<div class='footer-section'>")
            .append("<div class='about-section'>")
            .append("<h3>🚀 About BrokerHub</h3>")
            .append("<p>BrokerHub is a comprehensive multi-user brokerage management platform designed to streamline agricultural commodity trading operations. Our platform provides efficient transaction management, automated billing, and detailed reporting capabilities.</p>")
            .append("</div>")
            .append("<div class='thanks-section'>")
            .append("<h3>🙏 Thank You</h3>")
            .append("<p>Thank you for choosing our brokerage services. We appreciate your trust and look forward to continued business partnership.</p>")
            .append("</div>")
            .append("<div class='developer-section'>")
            .append("<h3>👨‍💻 Developer Information</h3>")
            .append("<div class='developer-info'>")
            .append("<div class='dev-item'><span class='dev-label'>📞 Contact:</span> <span class='dev-value'>8332827443</span></div>")
            .append("<div class='dev-item'><span class='dev-label'>📧 Email:</span> <span class='dev-value'>tarunbatchu2000@gmail.com</span></div>")
            .append("<div class='dev-item'><span class='dev-label'>🌐 Platform:</span> <span class='dev-value'>BrokerHub Multi-User System</span></div>")
            .append("</div>")
            .append("</div>")
            .append("<div class='footer-note'>")
            .append("<p>📄 This is a computer-generated document. No signature required.</p>")
            .append("<p>⚡ Powered by BrokerHub - Making Brokerage Management Simple & Efficient</p>")
            .append("</div>")
            .append("</div>");

        // Add chart data as JavaScript variables
        html.append("<script>")
            .append("const chartData = {")
            .append("products: {");
        
        // Product data from DTO
        for (UserBrokerageDetailDTO.ProductSummary product : userDetail.getBrokerageSummary().getProductsBought()) {
            html.append("'").append(product.getProductName()).append("':").append(product.getTotalBags()).append(",");
        }
        for (UserBrokerageDetailDTO.ProductSummary product : userDetail.getBrokerageSummary().getProductsSold()) {
            html.append("'").append(product.getProductName()).append("':").append(product.getTotalBags()).append(",");
        }
        html.append("},")
            .append("cities: {");
        
        // City data from DTO
        for (UserBrokerageDetailDTO.CitySummary city : userDetail.getBrokerageSummary().getCitiesSoldTo()) {
            html.append("'").append(city.getCity()).append("':").append(city.getTotalBags()).append(",");
        }
        for (UserBrokerageDetailDTO.CitySummary city : userDetail.getBrokerageSummary().getCitiesBoughtFrom()) {
            html.append("'").append(city.getCity()).append("':").append(city.getTotalBags()).append(",");
        }
        html.append("},")
            .append("monthly: {");
        
        // Monthly data from transactions - sorted chronologically with missing months filled
        java.util.Map<java.time.YearMonth, Long> tempMonthlyData = new java.util.HashMap<>();
        for (UserBrokerageDetailDTO.TransactionDetail transaction : userDetail.getTransactionDetails()) {
            java.time.YearMonth yearMonth = java.time.YearMonth.from(transaction.getTransactionDate());
            tempMonthlyData.put(yearMonth, tempMonthlyData.getOrDefault(yearMonth, 0L) + transaction.getQuantity());
        }
        
        // Fill missing months between first and last transaction month
        java.util.Map<String, Long> monthlyData = new java.util.LinkedHashMap<>();
        if (!tempMonthlyData.isEmpty()) {
            java.time.YearMonth firstMonth = tempMonthlyData.keySet().stream().min(java.time.YearMonth::compareTo).get();
            java.time.YearMonth lastMonth = tempMonthlyData.keySet().stream().max(java.time.YearMonth::compareTo).get();
            
            java.time.YearMonth current = firstMonth;
            while (!current.isAfter(lastMonth)) {
                String monthKey = current.getMonth().toString().substring(0,3) + " " + current.getYear();
                monthlyData.put(monthKey, tempMonthlyData.getOrDefault(current, 0L));
                current = current.plusMonths(1);
            }
        }
        for (java.util.Map.Entry<String, Long> entry : monthlyData.entrySet()) {
            html.append("'").append(entry.getKey()).append("':").append(entry.getValue()).append(",");
        }
        html.append("}")
            .append("};")
            .append("</script>")
            
            .append("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>")
            .append("<script>")
            .append("setTimeout(function(){")
            .append("try {")
            
            // Product Distribution Chart
            .append("const productCtx = document.getElementById('productChart');")
            .append("if(productCtx && chartData.products) {")
            .append("const productLabels = Object.keys(chartData.products);")
            .append("const productValues = Object.values(chartData.products);")
            .append("if(productLabels.length > 0) {")
            .append("new Chart(productCtx, { type: 'doughnut', data: { labels: productLabels, datasets: [{ ")
            .append(" data: productValues, backgroundColor: ['#0078D7','#28A745','#FF6B35','#8E24AA','#17A2B8','#FFC107','#DC3545'] }]}, ")
            .append(" options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom', labels: { font: { size: 8 } } } } } });")
            .append("}")
            .append("}")
            
            // City Distribution Chart
            .append("const cityCtx = document.getElementById('cityChart');")
            .append("if(cityCtx && chartData.cities) {")
            .append("const cityLabels = Object.keys(chartData.cities);")
            .append("const cityValues = Object.values(chartData.cities);")
            .append("if(cityLabels.length > 0) {")
            .append("new Chart(cityCtx, { type: 'doughnut', data: { labels: cityLabels, datasets: [{ ")
            .append(" data: cityValues, backgroundColor: ['#FF6B35','#8E24AA','#17A2B8','#FFC107','#DC3545','#6F42C1','#28A745'] }]}, ")
            .append(" options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom', labels: { font: { size: 8 } } } } } });")
            .append("}")
            .append("}")
            
            // Monthly Trend Chart
            .append("const monthlyCtx = document.getElementById('monthlyChart');")
            .append("if(monthlyCtx && chartData.monthly) {")
            .append("const monthlyLabels = Object.keys(chartData.monthly);")
            .append("const monthlyValues = Object.values(chartData.monthly);")
            .append("if(monthlyLabels.length > 0) {")
            .append("new Chart(monthlyCtx, { type: 'line', data: { labels: monthlyLabels, datasets: [{ ")
            .append(" label: 'Quantity', data: monthlyValues, borderColor: '#0078D7', backgroundColor: 'rgba(0,120,215,0.1)', ")
            .append(" tension: 0.4, fill: true }]}, ")
            .append(" options: { responsive: true, maintainAspectRatio: false, scales: { y: { beginAtZero: true } }, plugins: { legend: { display: false } } } });")
            .append("}")
            .append("}")
            
            .append("} catch(error) { console.error('Chart error:', error); }")
            .append("}, 500);")
            .append("</script>");

        html.append("</div></body></html>");
        
        return html.toString().getBytes();
    }

    private String getCompactCSS() {
        return """
    @page {
        size: A4 portrait;
        margin: 15mm;
    }
    
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: 'Segoe UI', Arial, sans-serif;
        background: #fff;
        padding: 0;
        color: #333;
        font-size: 12px;
        line-height: 1.3;
    }

    .bill-container {
        background: #fff;
        width: 100%;
        max-width: 210mm;
        margin: 0 auto;
        padding: 20px;
        position: relative;
        overflow: hidden;
    }

    .bill-container > * {
        position: relative;
        z-index: 2;
        display: block;
        width: 100%;
        margin-bottom: 15px;
    }

    /* ---------- WATERMARKS ---------- */
    .bill-container::before {
        content: "BROKERHUB";
        position: absolute;
        top: 12%;
        left: 50%;
        transform: translateX(-50%);
        font-size: 80px;
        font-weight: 700;
        color: rgba(0, 120, 215, 0.05);
        z-index: 0;
        pointer-events: none;
        user-select: none;
        white-space: nowrap;
    }

    .bill-container::after {
        content: "SIRI BROKERS";
        position: absolute;
        bottom: 10%;
        left: 50%;
        transform: translateX(-50%);
        font-size: 70px;
        font-weight: 700;
        color: rgba(255, 140, 0, 0.06);
        z-index: 0;
        pointer-events: none;
        user-select: none;
        white-space: nowrap;
    }

    /* ---------- HEADER ---------- */
    .header-section {
                     display: block; /* stack items vertically */
                     border-bottom: 2px solid #0078D7;
                     padding-bottom: 12px;
                     margin-bottom: 15px;
    }

    .firm-name {
        font-size: 26px;
        color: #0078D7;
        font-weight: 1200;
        letter-spacing: 0.5px;
    }

    .broker-name {
        font-size: 14px;
        color: #444;
        margin-top: 4px;
    }

    .fy-info {
        font-size: 13px;
        color: #666;
    }

    .bill-meta {
        text-align: right;
        font-size: 13px;
        color: #333;
    }

    .bill-meta div {
        margin-bottom: 4px;
    }

    /* ---------- SECTION HEADINGS ---------- */
    .section-title {
        background: linear-gradient(to right, #0078D7, #00a6ed);
        color: #fff;
        padding: 8px 14px;
        border-radius: 6px;
        font-size: 15px;
        margin: 15px 0 10px 0;
        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    }

    /* ---------- MERCHANT SECTION ---------- */
    .merchant-section {
        margin-bottom: 15px;
    }

    .merchant-info {
        padding: 8px;
        background: #f8fbff;
        border: 1px solid #dde8ff;
        border-radius: 4px;
    }

    .info-item {
        display: inline-block;
        margin-right: 20px;
        font-size: 11px;
    }

    .info-item .label {
        font-weight: bold;
        color: #666;
    }

    .info-item .value {
        color: #333;
        font-weight: 500;
    }

    /* ---------- SUMMARY SECTION ---------- */
    .summary-section {
        margin-bottom: 20px;
    }

    .summary-grid {
        display: grid;
        grid-template-columns: repeat(5, 1fr);
        gap: 8px;
        margin-bottom: 15px;
    }

    .summary-card {
        background: #f7faff;
        border: 1px solid #dde8ff;
        border-radius: 6px;
        text-align: center;
        padding: 8px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        min-height: 50px;
    }

    .card-icon {
        font-size: 16px;
        margin-bottom: 4px;
    }

    .card-number {
        font-size: 14px;
        color: #0078D7;
        font-weight: bold;
        line-height: 1.1;
    }

    .card-label {
        font-size: 9px;
        color: #555;
        margin-top: 2px;
        line-height: 1.1;
    }

    /* ---------- TRANSACTION TABLE ---------- */
    .transactions-section {
        margin-bottom: 20px;
    }

    .table-wrapper {
        overflow-x: auto;
        margin-bottom: 15px;
    }

    .transactions-table {
        width: 100%;
        border-collapse: collapse;
        border: 1px solid #ccc;
        font-size: 10px;
    }

    .transactions-table th, .transactions-table td {
        border: 1px solid #e0e0e0;
        padding: 4px 6px;
        text-align: center;
        vertical-align: middle;
    }

    .transactions-table th {
        background: #0078D7;
        color: white;
        font-size: 10px;
        font-weight: bold;
    }

    .transactions-table tr:nth-child(even) {
        background: #f8fbff;
    }

    .transactions-table .sno { width: 6%; }
    .transactions-table .date { width: 12%; }
    .transactions-table .merchant { width: 25%; text-align: left; }
    .transactions-table .city { width: 12%; text-align: left; }
    .transactions-table .product { width: 8%; text-align: left; }
    .transactions-table .quantity { width: 8%; }
    .transactions-table .amount { width: 8%; text-align: right; }
    .transactions-table .brokerage { width: 8%; text-align: right; }
    .transactions-table .type { width: 13%; }

    .type-sold {
        background: #32cd32;
        color: black;
        padding: 1px 4px;
        border-radius: 3px;
        font-size: 8px;
        font-weight: bold;
    }

    .type-bought {
        background: #ffc107;
        color: black;
        padding: 1px 4px;
        border-radius: 3px;
        font-size: 8px;
        font-weight: bold;
    }

    .type-unknown {
        background: #ffc107;
        color: black;
        padding: 1px 4px;
        border-radius: 3px;
        font-size: 8px;
        font-weight: bold;
    }

    /* ---------- CHARTS SECTION ---------- */
    .charts-section {
        margin: 15px 0 30px 0;
        page-break-inside: avoid;
    }

    .charts-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 15px;
        margin-top: 10px;
    }

    .chart-item {
        background: #f8fbff;
        border: 1px solid #dde8ff;
        border-radius: 8px;
        padding: 10px;
        text-align: center;
    }

    .chart-full {
        grid-column: 1 / -1;
    }

    .chart-title {
        color: #0078D7;
        font-size: 12px;
        margin-bottom: 8px;
        font-weight: bold;
    }

    .chart-container {
        position: relative;
        width: 100%;
        height: 200px;
        margin: 0 auto;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .chart-full .chart-container {
        height: 250px;
    }

    canvas {
        max-width: 100%;
        max-height: 100%;
        display: block;
    }

    /* ---------- PAYMENT SECTION ---------- */
    .payment-section {
        margin-top: 20px;
        page-break-inside: avoid;
    }

    .payment-content {
        width: 100%;
    }

    .payment-info {
        margin-bottom: 15px;
    }

    .brokerage-amount {
        font-size: 16px;
        font-weight: bold;
        color: #0078D7;
        margin-bottom: 8px;
    }

    .amount-label {
        font-size: 12px;
        color: #666;
    }

    .amount-value {
        font-size: 18px;
        font-weight: bold;
        color: #0078D7;
    }

    .bank-details {
        margin-top: 10px;
        margin-bottom: 15px;
    }

    .bank-details h3 {
        font-size: 12px;
        margin-bottom: 5px;
        color: #333;
    }

    .bank-info {
        font-size: 10px;
    }

    .bank-item {
        margin-bottom: 3px;
    }

    .bank-label {
        font-weight: bold;
        color: #666;
    }

    .bank-value {
        color: #333;
    }

    .qr-section {
        text-align: center;
        margin-top: 15px;
    }

    .qr-section h3 {
        font-size: 11px;
        margin-bottom: 5px;
    }

    .qr-code, .qr-placeholder {
        width: 80px;
        height: 80px;
        border: 1px solid #0078D7;
        border-radius: 4px;
        display: block;
        margin: 5px auto;
    }

    .qr-placeholder {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }

    .qr-amount {
        font-size: 10px;
        font-weight: bold;
        color: #0078D7;
        margin-top: 3px;
    }

    /* ---------- FOOTER SECTION ---------- */
    .footer-section {
        border-top: 1px dashed #ccc;
        padding-top: 10px;
        margin-top: 15px;
        font-size: 9px;
        color: #555;
        text-align: center;
        page-break-inside: avoid;
    }

    .about-section, .thanks-section, .developer-section {
        margin-bottom: 8px;
    }

    .about-section h3, .thanks-section h3, .developer-section h3 {
        color: #0078D7;
        margin-bottom: 3px;
        font-size: 10px;
    }

    .about-section p, .thanks-section p {
        font-size: 9px;
        line-height: 1.2;
    }

    .developer-info {
        font-size: 8px;
    }

    .dev-item {
        margin-bottom: 2px;
    }

    .dev-label {
        font-weight: bold;
        color: #666;
    }

    .dev-value {
        color: #333;
    }

    .footer-note {
        margin-top: 8px;
        color: #777;
        font-size: 8px;
    }
    """;
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
    
    private BigDecimal calculateTotalBrokerage(UserBrokerageDetailDTO userDetail, BigDecimal brokerageRate) {
        if (brokerageRate == null) {
            return userDetail.getBrokerageSummary().getTotalBrokeragePayable();
        }
        
        return userDetail.getTransactionDetails().stream()
            .map(transaction -> brokerageRate.multiply(BigDecimal.valueOf(transaction.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal getMerchantBrokerageRate(String firmName, Long brokerId) {
        try {
            Optional<User> userOpt = userRepository.findByBrokerBrokerIdAndFirmName(brokerId, firmName);
            if (userOpt.isPresent() && userOpt.get().getBrokerageRate() != null) {
                return BigDecimal.valueOf(userOpt.get().getBrokerageRate());
            }
        } catch (Exception e) {
            log.warn("Could not fetch merchant brokerage rate for firm: {}", firmName, e);
        }
        return BigDecimal.ZERO;
    }
    
    private BigDecimal getMerchantBrokerageRateByUserId(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent() && userOpt.get().getBrokerageRate() != null) {
                return BigDecimal.valueOf(userOpt.get().getBrokerageRate());
            }
        } catch (Exception e) {
            log.warn("Could not fetch merchant brokerage rate for userId: {}", userId, e);
        }
        return BigDecimal.ZERO;
    }
    
    public byte[] generateUserBrokerageBillPdf(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage) {
        try {
            // Generate HTML content first
            byte[] htmlContent = generateSimpleBill(userDetail, broker, financialYearId, customBrokerage);
            String htmlString = new String(htmlContent);
            
            // Convert HTML to PDF using iText
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            
            // Create PDF writer and document
            PdfWriter pdfWriter = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            
            // Set converter properties for better PDF rendering
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setCharset("UTF-8");
            
            // Convert HTML to PDF
            HtmlConverter.convertToPdf(htmlString, pdfDocument, converterProperties);
            
            // Close the document
            pdfDocument.close();
            
            return pdfOutputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating PDF bill", e);
            throw new RuntimeException("Failed to generate PDF bill: " + e.getMessage(), e);
        }
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
        
        // Get merchant's current brokerage rate if no custom brokerage provided
        BigDecimal merchantBrokerageRate = getMerchantBrokerageRate(userDetail.getUserBasicInfo().getFirmName(), broker.getBrokerId());
        
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
                // 1st row: Broker + Phone Number
                .append("<tr class='broker-row'>")
                .append("<td><strong>Broker : </strong></td><td>")
                .append(broker.getBrokerName() != null ? broker.getBrokerName() : "N/A")
                .append("</td>")
                .append("<td><strong>Phone : </strong></td><td>")
                .append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A")
                .append("</td>")
                .append("</tr>")
                // 2nd row: FY + Bill Print Date
                .append("<tr>")
                .append("<td><strong>Financial Year : </strong></td><td>")
                .append(duration)
                .append("</td>")
                .append("<td><strong>Bill Print Date : </strong></td><td>")
                .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</div>");


        // === MERCHANT INFO (Compact & Highlighted) ===
        html.append("<div class='client-info'>")
                .append("<h2>")
                .append("Merchant Name: <strong>").append(userDetail.getUserBasicInfo().getFirmName()).append("</strong>")
                .append(" &nbsp; | &nbsp; City: <strong>")
                .append(userDetail.getUserBasicInfo().getCity() != null ? userDetail.getUserBasicInfo().getCity() : "N/A").append("</strong>")
                .append("</h2>")
                .append("</div>");

        // === TRANSACTIONS TABLE ===
        html.append("<h4 style='margin: 6px 0 4px 0; font-size: 14px;'>Transaction Details</h4>")
                .append("<table class='transactions-table'>")
                .append("<thead><tr><th>S.No</th><th>DATE</th><th>MERCHANT FIRM NAME</th><th>CITY</th><th>PRODUCT</th><th>Qty</th><th>RATE</th><th>BROKERAGE</th></tr></thead><tbody>");

        int counter = 1;
        for (UserBrokerageDetailDTO.TransactionDetail transaction : userDetail.getTransactionDetails()) {
            BigDecimal transactionBrokerage = customBrokerage != null ?
                    customBrokerage.multiply(BigDecimal.valueOf(transaction.getQuantity())) :
                    (merchantBrokerageRate.compareTo(BigDecimal.ZERO) > 0 ? 
                        merchantBrokerageRate.multiply(BigDecimal.valueOf(transaction.getQuantity())) : 
                        transaction.getBrokerage());
            if(customBrokerage==null){
                if(merchantBrokerageRate.compareTo(BigDecimal.ZERO) > 0) {
                    totalBrokerage = totalBrokerage.add(merchantBrokerageRate.multiply(BigDecimal.valueOf(transaction.getQuantity())));
                } else {
                    totalBrokerage = totalBrokerage.add(transaction.getBrokerage());
                }
            }

            html.append("<tr>")
                    .append("<td>").append(counter++).append("</td>") // Auto sequence number
                    .append("<td>").append(transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yy"))).append("</td>")
                    .append("<td>").append(transaction.getCounterPartyFirmName()).append("</td>")
                    .append("<td>").append(transaction.getCounterPartyCity() != null ? transaction.getCounterPartyCity() : "N/A").append("</td>")
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

        BigDecimal brokeragePerBag = customBrokerage != null ? customBrokerage : merchantBrokerageRate;
        BigDecimal totalPayableBrokerage;

        if (customBrokerage != null) {
            totalPayableBrokerage = customBrokerage.multiply(BigDecimal.valueOf(totalBags));
        } else if (merchantBrokerageRate.compareTo(BigDecimal.ZERO) > 0) {
            totalPayableBrokerage = merchantBrokerageRate.multiply(BigDecimal.valueOf(totalBags));
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
        html.append("<div class='payment-wrapper'>")
                .append("<div class='payment-details-section'>")
                .append("<h4 style='margin: 8px 0 6px 0; font-size: 12px;'>💳 Payment Details</h4>");
        
        // Bank Details
        if (broker.getBankDetails() != null) {
            html.append("<div class='bank-details'>")
                    .append("<div class='payment-row'><span>Bank : </span> <strong>").append(broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A").append("</strong></div>")
                    .append("<div class='payment-row'><span>A/C No : </span> <strong>").append(broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A").append("</strong></div>")
                    .append("<div class='payment-row'><span>IFSC : </span> <strong>").append(broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A").append("</strong></div>")
                    .append("</div>");
        }
        
        // UPI Details
        html.append("<div class='upi-details'>")
                .append("<div class='payment-row'><span>UPI : </span> <strong>").append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A").append("</strong></div>")
                .append("<div class='payment-row'><span>UPI ID : </span> <strong>9848543443@ptaxis</strong></div>")
                .append("<div class='upi-apps'>Paytm | PhonePe | GooglePay</div>")
                .append("</div>")
                .append("</div>");
        
        // QR Code (outside border)
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
                .append("</div>");

        // === FOOTER ===
        html.append("<div class='footer'>")
                .append("<p><em>Thank you for your business!</em></p>")
                .append("<p><strong>Contact:</strong> ")
                .append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A")
                .append("<p><em>For Software Contact : 📞 8332827443  📩 tarunbatchu2000@gmail.com </em></p>")
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
                ".client-info { margin: 6px 0; border: 1px solid #000; padding: 4px; border-radius: 3px; background-color: #f9f9f9; }" +
                ".transactions-table { width: 100%; border-collapse: collapse; margin-top: 6px; }" +
                ".transactions-table th, .transactions-table td { border: 1px solid #ccc; padding: 2px 3px; font-size: 9px; line-height: 1.1; }"+
                ".transactions-table th { font-size: 12px; font-weight: bold; }"+
                ".transactions-table tbody tr { height: 18px; }"+
                ".transactions-table thead tr { height: 20px; }"+
                ".transactions-table th:nth-child(1), .transactions-table td:nth-child(1) { width: 4%; text-align: center; font-size: 10px; }" +  /* S.No */
                ".transactions-table th:nth-child(2), .transactions-table td:nth-child(2) { width: 8%; text-align: center; font-size: 10px; }"+  /* Date */
                ".transactions-table th:nth-child(3), .transactions-table td:nth-child(3) { width: 30%; text-align: left; font-size: 10px; padding-left: 2px; } "+                     /* Merchant Firm Name */
                ".transactions-table th:nth-child(4), .transactions-table td:nth-child(4) { width: 12%; text-align: left; font-size: 10px; padding-left: 2px; } "+                     /* City */
                ".transactions-table th:nth-child(5), .transactions-table td:nth-child(5) { width: 8%; text-align: left; font-size: 10px; padding-left: 2px; }  "+                    /* Product - more decreased */
                ".transactions-table th:nth-child(6), .transactions-table td:nth-child(6) { width: 6%; text-align: center; font-size: 10px; } "+   /* Qty */
                ".transactions-table th:nth-child(7), .transactions-table td:nth-child(7) { width: 8%; text-align: right; font-size: 10px; padding-right: 2px; }"+   /* Rate - more decreased */
                ".transactions-table th:nth-child(8), .transactions-table td:nth-child(8) { width: 8%; text-align: right; font-size: 10px; padding-right: 2px; }  "+ /* Brokerage - more decreased */
                ".print-btn { background: #007bff; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 12px; }" +
                ".print-header { text-align: center; margin-bottom: 10px; border-bottom: 1px solid #000; padding-bottom: 5px; }" +
                ".print-header h1 { font-size: 18px; margin: 0; font-weight: bold; }" +
                ".header-info { display: grid; grid-template-columns: repeat(3, 1fr); gap: 4px; font-size: 11px; margin-top: 5px; }" +
                ".client-info { margin: 4px 0; font-size: 10px; }" +
                ".summary-table { margin: 6px auto; width: 50%; border-collapse: collapse; }" +
                ".summary-table th { background-color: #f0f0f0; font-weight: bold; text-align: center; }"+
                ".summary-table td { text-align: center; }"+
                ".summary-table th, .summary-table td { padding: 3px 4px; font-size: 10px; border: 1px solid #ccc; line-height: 1.2; }"+
                ".summary-table tbody tr { height: 22px; }"+
                ".summary-table thead tr { height: 24px; }"+
                ".transactions-table th { background-color: #f0f0f0; font-weight: bold; }" +
               "@media print { body { -webkit-print-color-adjust: exact; color-adjust: exact; } }" +
                ".total-row { background-color: #6ef59d; }" +
                ".payment-wrapper { display: flex; justify-content: space-between; align-items: flex-start; gap: 10px; margin: 8px 0; }" +
                ".payment-details-section { flex: 0 1 auto; width: fit-content; max-width: 60%; border: 1px solid #ccc; padding: 6px; border-radius: 4px; background-color: #f9f9f9; }" +
                ".bank-details, .upi-details { margin-bottom: 4px; }" +
                ".payment-row { font-size: 9px; margin-bottom: 2px; }" +
                ".payment-row span { display: inline-block; width: 50px; }" +
                ".upi-apps { font-size: 8px; color: #666; margin-top: 2px; }" +
                ".qr-section { flex: 0 0 80px; text-align: center; }" +
                ".qr-placeholder { width: 80px; height: 80px; border: 2px solid #ccc; display: flex; flex-direction: column; justify-content: center; align-items: center; background: white; }" +
                ".qr-text { font-size: 8px; font-weight: bold; }" +
                ".qr-amount { font-size: 20px; margin-top: 2px; }" +
                ".footer { text-align: center; margin-top: 10px; font-size: 10px; font-weight: bold; border-top: 1px solid #ccc; padding-top: 4px; }" +
                ".footer em { font-style: italic; font-weight: normal; display: block; margin-bottom: 3px; }" +
                ".city-distribution-table { width: 100%; border-collapse: collapse; margin-top: 6px; margin-bottom: 8px; }" +
                ".city-distribution-table th, .city-distribution-table td { border: 1px solid #ccc; padding: 3px 4px; font-size: 10px; line-height: 1.2; }" +
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
        
        // Get merchant's current brokerage rate if no custom brokerage provided
        BigDecimal merchantBrokerageRate = getMerchantBrokerageRate(userDetail.getUserBasicInfo().getFirmName(), broker.getBrokerId());
        
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

        BigDecimal brokeragePerBag = customBrokerage != null ? customBrokerage : merchantBrokerageRate;
        BigDecimal totalPayableBrokerage;

        if (customBrokerage != null) {
            totalPayableBrokerage = customBrokerage.multiply(BigDecimal.valueOf(totalBags));
        } else if (merchantBrokerageRate.compareTo(BigDecimal.ZERO) > 0) {
            totalPayableBrokerage = merchantBrokerageRate.multiply(BigDecimal.valueOf(totalBags));
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
        html.append("<div class='payment-wrapper'>")
                .append("<div class='payment-details-section'>")
                .append("<h4 style='margin: 8px 0 6px 0; font-size: 12px;'>💳 Payment Details</h4>");
        
        // Bank Details
        if (broker.getBankDetails() != null) {
            html.append("<div class='bank-details'>")
                    .append("<div class='payment-row'><span>Bank : </span> <strong>").append(broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A").append("</strong></div>")
                    .append("<div class='payment-row'><span>A/C No : </span> <strong>").append(broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A").append("</strong></div>")
                    .append("<div class='payment-row'><span>IFSC : </span> <strong>").append(broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A").append("</strong></div>")
                    .append("</div>");
        }
        
        // UPI Details
        html.append("<div class='upi-details'>")
                .append("<div class='payment-row'><span>UPI : </span> <strong>").append(broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A").append("</strong></div>")
                .append("<div class='payment-row'><span>UPI ID : </span> <strong>9848543443@ptaxis</strong></div>")
                .append("<div class='upi-apps'>Paytm | PhonePe | GooglePay</div>")
                .append("</div>")
                .append("</div>");
        
        // QR Code (outside border)
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
                .append("</div>");

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