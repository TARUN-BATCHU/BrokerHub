package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerageSummaryDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ExcelGenerationServiceImpl implements ExcelGenerationService {
    
    @Override
    public byte[] generateUserBrokerageExcel(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createUserBrokerageSheet(workbook, userDetail, broker, financialYearId, null);
            return writeWorkbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating user brokerage Excel", e);
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }
    
    @Override
    public byte[] generateUserBrokerageExcel(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createUserBrokerageSheet(workbook, userDetail, broker, financialYearId, customBrokerage);
            return writeWorkbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating user brokerage Excel with custom brokerage", e);
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }
    
    @Override
    public byte[] generateBrokerageSummaryExcel(BrokerageSummaryDTO summary, Broker broker, Long financialYearId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createSummarySheet(workbook, summary, broker, financialYearId);
            return writeWorkbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating brokerage summary Excel", e);
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }
    
    @Override
    public byte[] generateCityBrokerageExcel(String city, List<UserBrokerageDetailDTO> cityUsers, Broker broker, Long financialYearId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createCityBrokerageSheet(workbook, city, cityUsers, broker, financialYearId);
            return writeWorkbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating city brokerage Excel", e);
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }
    
    private void createUserBrokerageSheet(Workbook workbook, UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId, BigDecimal customBrokerage) {
        Sheet sheet = workbook.createSheet("Brokerage Bill");
        sheet.setDisplayGridlines(false);
        
        // Create enhanced styles
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle boldStyle = createBoldStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);
        
        int rowNum = 0;
        
        // Main Title with enhanced styling
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BROKERAGE BILL");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));
        
        rowNum++; // Empty row
        
        // Broker Details Section
        Row brokerHeaderRow = sheet.createRow(rowNum++);
        Cell brokerHeaderCell = brokerHeaderRow.createCell(0);
        brokerHeaderCell.setCellValue("BROKER DETAILS");
        brokerHeaderCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));
        
        createBrokerDetailsSection(sheet, broker, financialYearId, rowNum, boldStyle, dataStyle);
        rowNum += 7; // Broker details take 7 rows
        
        rowNum++; // Empty row
        
        // Merchant Details Section
        Row merchantHeaderRow = sheet.createRow(rowNum++);
        Cell merchantHeaderCell = merchantHeaderRow.createCell(0);
        merchantHeaderCell.setCellValue("MERCHANT DETAILS");
        merchantHeaderCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));
        
        createMerchantDetailsSection(sheet, userDetail, rowNum, boldStyle, dataStyle);
        rowNum += 3; // Merchant details take 3 rows
        
        rowNum++; // Empty row
        
        // Transaction Details with new column order
        Row transHeaderRow1 = sheet.createRow(rowNum++);
        Cell transHeaderCell = transHeaderRow1.createCell(0);
        transHeaderCell.setCellValue("TRANSACTION DETAILS");
        transHeaderCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));
        
        Row transHeaderRow = sheet.createRow(rowNum++);
        String[] headers = {"S.No", "Date", "Buyer Name", "Product", "Quantity", "Rate", "Brokerage", "Transaction Number"};
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = transHeaderRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(boldStyle);
        }
        
        // Transaction data with totals calculation
        Long totalQuantity = 0L;
        BigDecimal totalBrokerage = BigDecimal.ZERO;
        int serialNo = 1;
        
        for (UserBrokerageDetailDTO.TransactionDetail transaction : userDetail.getTransactionDetails()) {
            Row row = sheet.createRow(rowNum++);
            BigDecimal transactionBrokerage = customBrokerage != null ? 
                customBrokerage.multiply(BigDecimal.valueOf(transaction.getQuantity())) : 
                transaction.getBrokerage();
            
            createCell(row, 0, String.valueOf(serialNo++), dataStyle);
            createCell(row, 1, transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), dataStyle);
            createCell(row, 2, transaction.getCounterPartyFirmName(), dataStyle);
            createCell(row, 3, transaction.getProductName(), dataStyle);
            createCell(row, 4, transaction.getQuantity().toString(), dataStyle);
            createCell(row, 5, "₹" + transaction.getProductCost().toString(), currencyStyle);
            createCell(row, 6, "₹" + transactionBrokerage.toString(), currencyStyle);
            createCell(row, 7, transaction.getTransactionNumber().toString(), dataStyle);
            
            totalQuantity += transaction.getQuantity();
            totalBrokerage = totalBrokerage.add(transactionBrokerage);
        }
        
        // Add totals row
        Row totalRow = sheet.createRow(rowNum++);
        createCell(totalRow, 0, "", totalStyle);
        createCell(totalRow, 1, "", totalStyle);
        createCell(totalRow, 2, "", totalStyle);
        createCell(totalRow, 3, "TOTAL:", totalStyle);
        createCell(totalRow, 4, totalQuantity.toString(), totalStyle);
        createCell(totalRow, 5, "", totalStyle);
        createCell(totalRow, 6, "₹" + totalBrokerage.toString(), totalStyle);
        createCell(totalRow, 7, "", totalStyle);
        
        rowNum++; // Empty row
        
        // Summary Section
        Row summaryHeaderRow = sheet.createRow(rowNum++);
        Cell summaryHeaderCell = summaryHeaderRow.createCell(0);
        summaryHeaderCell.setCellValue("SUMMARY");
        summaryHeaderCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));
        
        createSummarySection(sheet, userDetail, totalBrokerage, customBrokerage, rowNum, boldStyle, dataStyle, currencyStyle);
        
        // Auto-size columns and apply formatting
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 3000));
        }
        
        // Set row heights for better appearance
        for (int i = 0; i <= rowNum + 10; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                row.setHeightInPoints(20);
            }
        }
    }
    
    private void createSummarySheet(Workbook workbook, BrokerageSummaryDTO summary, Broker broker, Long financialYearId) {
        Sheet sheet = workbook.createSheet("Brokerage Summary");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle boldStyle = createBoldStyle(workbook);
        
        int rowNum = 0;
        
        // Header
        createCell(sheet.createRow(rowNum++), 0, "BROKERAGE SUMMARY - " + broker.getBrokerageFirmName(), headerStyle);
        createCell(sheet.createRow(rowNum++), 0, "Financial Year: " + financialYearId, null);
        
        rowNum++; // Empty row
        
        // Total Summary
        createCell(sheet.createRow(rowNum++), 0, "Total Brokerage Earned:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, "₹" + summary.getTotalBrokerageEarned().toString(), null);
        
        createCell(sheet.createRow(rowNum++), 0, "From Sellers:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, "₹" + summary.getTotalBrokerageFromSellers().toString(), null);
        
        createCell(sheet.createRow(rowNum++), 0, "From Buyers:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, "₹" + summary.getTotalBrokerageFromBuyers().toString(), null);
        
        rowNum++; // Empty row
        
        // City-wise breakdown
        createCell(sheet.createRow(rowNum++), 0, "CITY-WISE BROKERAGE", headerStyle);
        Row cityHeaderRow = sheet.createRow(rowNum++);
        createCell(cityHeaderRow, 0, "City", boldStyle);
        createCell(cityHeaderRow, 1, "Total Brokerage", boldStyle);
        
        for (BrokerageSummaryDTO.CityBrokerageDTO cityBrokerage : summary.getCityWiseBrokerage()) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, cityBrokerage.getCity(), null);
            createCell(row, 1, "₹" + cityBrokerage.getTotalBrokerage().toString(), null);
        }
        
        rowNum++; // Empty row
        
        // Product-wise breakdown
        createCell(sheet.createRow(rowNum++), 0, "PRODUCT-WISE BROKERAGE", headerStyle);
        Row productHeaderRow = sheet.createRow(rowNum++);
        createCell(productHeaderRow, 0, "Product", boldStyle);
        createCell(productHeaderRow, 1, "Total Brokerage", boldStyle);
        
        for (BrokerageSummaryDTO.ProductBrokerageDTO productBrokerage : summary.getProductWiseBrokerage()) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, productBrokerage.getProductName(), null);
            createCell(row, 1, "₹" + productBrokerage.getTotalBrokerage().toString(), null);
        }
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void createCityBrokerageSheet(Workbook workbook, String city, List<UserBrokerageDetailDTO> cityUsers, Broker broker, Long financialYearId) {
        Sheet sheet = workbook.createSheet("City Brokerage - " + city);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle boldStyle = createBoldStyle(workbook);
        
        int rowNum = 0;
        
        // Header
        createCell(sheet.createRow(rowNum++), 0, "CITY BROKERAGE REPORT - " + city, headerStyle);
        createCell(sheet.createRow(rowNum++), 0, broker.getBrokerageFirmName(), null);
        
        rowNum++; // Empty row
        
        // User Summary
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Firm Name", "Owner Name", "Total Bags Sold", "Total Bags Bought", "Total Brokerage"};
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], boldStyle);
        }
        
        for (UserBrokerageDetailDTO user : cityUsers) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, user.getUserBasicInfo().getFirmName(), null);
            createCell(row, 1, user.getUserBasicInfo().getOwnerName(), null);
            createCell(row, 2, user.getBrokerageSummary().getTotalBagsSold().toString(), null);
            createCell(row, 3, user.getBrokerageSummary().getTotalBagsBought().toString(), null);
            createCell(row, 4, "₹" + user.getBrokerageSummary().getTotalBrokeragePayable().toString(), null);
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }
    
    private CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }
    
    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    private void createBrokerDetailsSection(Sheet sheet, Broker broker, Long financialYearId, int startRow, CellStyle boldStyle, CellStyle dataStyle) {
        int rowNum = startRow;
        
        createCell(sheet.createRow(rowNum++), 0, "Broker Firm Name:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, broker.getBrokerageFirmName(), dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "Broker Name:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, broker.getBrokerName(), dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "City:", boldStyle);
        String city = (broker.getAddress() != null) ? broker.getAddress().getCity() : "N/A";
        createCell(sheet.getRow(rowNum-1), 1, city, dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "Phone Number:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, broker.getPhoneNumber() != null ? broker.getPhoneNumber() : "N/A", dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "Email:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, broker.getEmail() != null ? broker.getEmail() : "N/A", dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "Bank Details:", boldStyle);
        String bankDetails = "N/A";
        if (broker.getBankDetails() != null) {
            bankDetails = String.format("%s - %s (IFSC: %s)", 
                broker.getBankDetails().getBankName() != null ? broker.getBankDetails().getBankName() : "N/A",
                broker.getBankDetails().getAccountNumber() != null ? broker.getBankDetails().getAccountNumber() : "N/A",
                broker.getBankDetails().getIfscCode() != null ? broker.getBankDetails().getIfscCode() : "N/A");
        }
        createCell(sheet.getRow(rowNum-1), 1, bankDetails, dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "Period:", boldStyle);
        String period = getFinancialYearPeriod(financialYearId);
        createCell(sheet.getRow(rowNum-1), 1, period, dataStyle);
    }
    
    private void createMerchantDetailsSection(Sheet sheet, UserBrokerageDetailDTO userDetail, int startRow, CellStyle boldStyle, CellStyle dataStyle) {
        int rowNum = startRow;
        
        createCell(sheet.createRow(rowNum++), 0, "Merchant Firm Name:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getUserBasicInfo().getFirmName(), dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "Owner Name:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getUserBasicInfo().getOwnerName(), dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "City:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getUserBasicInfo().getCity(), dataStyle);
    }
    
    private String getFinancialYearPeriod(Long financialYearId) {
        if (financialYearId == null) {
            return "N/A";
        }
        
        int startYear = financialYearId.intValue();
        int endYear = startYear + 1;
        
        return String.format("01-04-%d to 31-03-%d", startYear, endYear);
    }
    
    private void createSummarySection(Sheet sheet, UserBrokerageDetailDTO userDetail, BigDecimal totalBrokerage, BigDecimal customBrokerage, int startRow, CellStyle boldStyle, CellStyle dataStyle, CellStyle currencyStyle) {
        int rowNum = startRow;
        
        createCell(sheet.createRow(rowNum++), 0, "Total Bags Sold:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getBrokerageSummary().getTotalBagsSold().toString(), dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "Total Bags Bought:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getBrokerageSummary().getTotalBagsBought().toString(), dataStyle);
        
        createCell(sheet.createRow(rowNum++), 0, "Total Brokerage Payable:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, "₹" + totalBrokerage.toString(), currencyStyle);
        
        if (customBrokerage != null) {
            createCell(sheet.createRow(rowNum++), 0, "Custom Brokerage Rate:", boldStyle);
            createCell(sheet.getRow(rowNum-1), 1, "₹" + customBrokerage.toString() + " per bag", dataStyle);
        }
    }
    
    private byte[] writeWorkbookToBytes(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private BigDecimal calculateTotalBrokerage(UserBrokerageDetailDTO userDetail, BigDecimal customBrokerage) {
        if (customBrokerage == null) {
            // If no custom brokerage, calculate from transaction details to ensure accuracy
            BigDecimal calculatedTotal = userDetail.getTransactionDetails().stream()
                .map(UserBrokerageDetailDTO.TransactionDetail::getBrokerage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Use the calculated total if it's greater than 0, otherwise use the summary value
            return calculatedTotal.compareTo(BigDecimal.ZERO) > 0 ? 
                calculatedTotal : 
                (userDetail.getBrokerageSummary().getTotalBrokeragePayable() != null ? 
                    userDetail.getBrokerageSummary().getTotalBrokeragePayable() : BigDecimal.ZERO);
        }
        
        return userDetail.getTransactionDetails().stream()
            .map(transaction -> customBrokerage.multiply(BigDecimal.valueOf(transaction.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}