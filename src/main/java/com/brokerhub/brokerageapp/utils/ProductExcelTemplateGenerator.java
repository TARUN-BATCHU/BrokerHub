package com.brokerhub.brokerageapp.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProductExcelTemplateGenerator {

    public static ResponseEntity<ByteArrayResource> generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "productName", "productBrokerage", "quantity", "price", "quality", "imgLink"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Add sample data
            Object[][] sampleData = {
                {"Wheat", 5.5, 100, 2500, "Premium", ""},
                {"Rice", 4.0, 200, 3000, "Grade A", ""},
                {"Corn", 3.5, 150, 2000, "Standard", ""}
            };

            for (int i = 0; i < sampleData.length; i++) {
                Row row = sheet.createRow(i + 1);
                Object[] rowData = sampleData[i];
                
                for (int j = 0; j < rowData.length; j++) {
                    Cell cell = row.createCell(j);
                    if (rowData[j] instanceof String) {
                        cell.setCellValue((String) rowData[j]);
                    } else if (rowData[j] instanceof Number) {
                        cell.setCellValue(((Number) rowData[j]).doubleValue());
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Add instructions sheet
            Sheet instructionsSheet = workbook.createSheet("Instructions");
            Row instructionRow = instructionsSheet.createRow(0);
            instructionRow.createCell(0).setCellValue("Product Bulk Upload Instructions:");
            
            String[] instructions = {
                "",
                "Required Fields:",
                "- productName: Name of the product (Required)",
                "",
                "Optional Fields:",
                "- productBrokerage: Brokerage rate per unit (default: 0.0)",
                "- quantity: Available quantity (default: 0)",
                "- price: Price per unit (default: 0)",
                "- quality: Quality grade/description",
                "- imgLink: Image URL for the product",
                "",
                "Notes:",
                "- Only productName is mandatory",
                "- Numeric fields will default to 0 if empty",
                "- Duplicate product names are allowed",
                "- All fields except productName can be left empty"
            };

            for (int i = 0; i < instructions.length; i++) {
                Row row = instructionsSheet.createRow(i + 1);
                row.createCell(0).setCellValue(instructions[i]);
            }

            instructionsSheet.autoSizeColumn(0);

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product_bulk_upload_template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel template", e);
        }
    }
}