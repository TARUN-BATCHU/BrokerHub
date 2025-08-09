package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerageSummaryDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ExcelGenerationServiceImpl implements ExcelGenerationService {
    
    @Override
    public byte[] generateUserBrokerageExcel(UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createUserBrokerageSheet(workbook, userDetail, broker, financialYearId);
            return writeWorkbookToBytes(workbook);
        } catch (IOException e) {
            log.error("Error generating user brokerage Excel", e);
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
    
    private void createUserBrokerageSheet(Workbook workbook, UserBrokerageDetailDTO userDetail, Broker broker, Long financialYearId) {
        Sheet sheet = workbook.createSheet("User Brokerage Bill");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle boldStyle = createBoldStyle(workbook);
        
        int rowNum = 0;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "BROKERAGE BILL - " + broker.getBrokerageFirmName(), headerStyle);
        
        rowNum++; // Empty row
        
        // User Details
        createCell(sheet.createRow(rowNum++), 0, "Firm Name:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getUserBasicInfo().getFirmName(), null);
        
        createCell(sheet.createRow(rowNum++), 0, "Owner Name:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getUserBasicInfo().getOwnerName(), null);
        
        createCell(sheet.createRow(rowNum++), 0, "City:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getUserBasicInfo().getCity(), null);
        
        rowNum++; // Empty row
        
        // Summary
        createCell(sheet.createRow(rowNum++), 0, "SUMMARY", headerStyle);
        createCell(sheet.createRow(rowNum++), 0, "Total Bags Sold:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getBrokerageSummary().getTotalBagsSold().toString(), null);
        
        createCell(sheet.createRow(rowNum++), 0, "Total Bags Bought:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, userDetail.getBrokerageSummary().getTotalBagsBought().toString(), null);
        
        createCell(sheet.createRow(rowNum++), 0, "Total Brokerage Payable:", boldStyle);
        createCell(sheet.getRow(rowNum-1), 1, "₹" + userDetail.getBrokerageSummary().getTotalBrokeragePayable().toString(), null);
        
        rowNum++; // Empty row
        
        // Transaction Details
        createCell(sheet.createRow(rowNum++), 0, "TRANSACTION DETAILS", headerStyle);
        
        Row transHeaderRow = sheet.createRow(rowNum++);
        String[] headers = {"Transaction #", "Date", "Counter Party", "Product", "Quantity", "Rate", "Brokerage"};
        for (int i = 0; i < headers.length; i++) {
            createCell(transHeaderRow, i, headers[i], boldStyle);
        }
        
        for (UserBrokerageDetailDTO.TransactionDetail transaction : userDetail.getTransactionDetails()) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, transaction.getTransactionNumber().toString(), null);
            createCell(row, 1, transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), null);
            createCell(row, 2, transaction.getCounterPartyFirmName(), null);
            createCell(row, 3, transaction.getProductName(), null);
            createCell(row, 4, transaction.getQuantity().toString(), null);
            createCell(row, 5, "₹" + transaction.getProductCost().toString(), null);
            createCell(row, 6, "₹" + transaction.getBrokerage().toString(), null);
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
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
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }
    
    private CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
    
    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    private byte[] writeWorkbookToBytes(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}