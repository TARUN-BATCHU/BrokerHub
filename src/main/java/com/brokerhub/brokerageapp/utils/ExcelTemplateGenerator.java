package com.brokerhub.brokerageapp.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExcelTemplateGenerator {

    private static final String[] HEADERS = {
        "userType", "gstNumber", "firmName", "ownerName", "city", "area", "pincode",
        "email", "bankName", "accountNumber", "ifscCode", "branch", "phoneNumbers",
        "brokerageRate", "shopNumber", "byProduct", "addressHint", "collectionRote"
    };

    private static final String[] SAMPLE_DATA = {
        "TRADER", "GST123456789", "ABC Trading Co", "John Doe", "Mumbai", "Andheri", "400058",
        "john@abctrading.com", "HDFC Bank", "1234567890", "HDFC0001234", "Andheri Branch", "9876543210,9876543211",
        "5", "Shop-101", "", "Near Main Market", "Route-A"
    };

    private static final String[] SAMPLE_DATA_MILLER = {
        "MILLER", "GST987654321", "XYZ Mills Pvt Ltd", "Jane Smith", "Delhi", "Karol Bagh", "110005",
        "jane@xyzmills.com", "SBI", "0987654321", "SBIN0001234", "Karol Bagh Branch", "9123456789",
        "3", "Mill-205", "Rice Bran", "Industrial Area Gate 2", "Route-B"
    };

    private static final String[] MANDATORY_SAMPLE = {
        "TRADER", "GST111222333", "Sample Firm Name", "", "Sample City", "", "",
        "", "", "", "", "", "9999999999",
        "10", "", "", "", ""
    };

    public static ResponseEntity<ByteArrayResource> generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Create sample data rows
            Row sampleRow1 = sheet.createRow(1);
            for (int i = 0; i < SAMPLE_DATA.length; i++) {
                Cell cell = sampleRow1.createCell(i);
                cell.setCellValue(SAMPLE_DATA[i]);
            }

            Row sampleRow2 = sheet.createRow(2);
            for (int i = 0; i < SAMPLE_DATA_MILLER.length; i++) {
                Cell cell = sampleRow2.createCell(i);
                cell.setCellValue(SAMPLE_DATA_MILLER[i]);
            }

            // Add mandatory fields only sample
            Row sampleRow3 = sheet.createRow(3);
            for (int i = 0; i < MANDATORY_SAMPLE.length; i++) {
                Cell cell = sampleRow3.createCell(i);
                cell.setCellValue(MANDATORY_SAMPLE[i]);
            }

            // Auto-size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Create instructions sheet
            Sheet instructionsSheet = workbook.createSheet("Instructions");
            Row instructionRow = instructionsSheet.createRow(0);
            instructionRow.createCell(0).setCellValue("Instructions for Bulk User Upload:");

            String[] instructions = {
                "",
                "1. Fill the 'Users' sheet with user data",
                "2. MANDATORY fields: userType, gstNumber, firmName, city, phoneNumbers, brokerageRate",
                "3. All other fields are OPTIONAL",
                "4. userType: TRADER or MILLER",
                "5. phoneNumbers: Use comma-separated values for multiple numbers",
                "6. Save the file as .xlsx format",
                "7. Upload the file using the bulk upload endpoint",
                "",
                "Field Descriptions:",
                "- userType: TRADER or MILLER (MANDATORY)",
                "- gstNumber: GST registration number (MANDATORY)",
                "- firmName: Company/Firm name (MANDATORY)",
                "- ownerName: Owner's name (optional)",
                "- city: City name (MANDATORY)",
                "- area: Area/locality (optional)",
                "- pincode: Postal code (optional)",
                "- email: Email address (optional)",
                "- bankName: Bank name (optional)",
                "- accountNumber: Bank account number (optional)",
                "- ifscCode: Bank IFSC code (optional)",
                "- branch: Bank branch name (optional)",
                "- phoneNumbers: Phone numbers, comma-separated (MANDATORY)",
                "- brokerageRate: Brokerage rate percentage (MANDATORY)",
                "- shopNumber: Shop/office number (optional)",
                "- byProduct: By-product name (optional, used for MILLER type)",
                "- addressHint: Additional address information (optional)",
                "- collectionRote: Collection route information (optional)",
                "",
                "Notes:",
                "- Duplicate firm names or GST numbers will be rejected",
                "- Invalid email formats will be rejected",
                "- Numeric fields should contain only numbers",
                "- Empty rows will be skipped",
                "- All MANDATORY fields must have values"
            };

            for (int i = 0; i < instructions.length; i++) {
                Row row = instructionsSheet.createRow(i + 1);
                row.createCell(0).setCellValue(instructions[i]);
            }

            instructionsSheet.autoSizeColumn(0);

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_bulk_upload_template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel template: " + e.getMessage());
        }
    }
}
