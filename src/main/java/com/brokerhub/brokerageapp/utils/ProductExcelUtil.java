package com.brokerhub.brokerageapp.utils;

import com.brokerhub.brokerageapp.dto.ProductDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProductExcelUtil {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<ProductDTO> excelToProductDTOs(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<ProductDTO> products = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header row
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                ProductDTO productDTO = new ProductDTO();

                // Column mapping: productName, productBrokerage, quantity, price, quality, imgLink
                Cell productNameCell = currentRow.getCell(0);
                if (productNameCell != null && productNameCell.getCellType() != CellType.BLANK) {
                    productDTO.setProductName(getCellValueAsString(productNameCell).trim());
                }

                Cell brokerageCell = currentRow.getCell(1);
                if (brokerageCell != null && brokerageCell.getCellType() != CellType.BLANK) {
                    try {
                        productDTO.setProductBrokerage((float) brokerageCell.getNumericCellValue());
                    } catch (Exception e) {
                        // Handle string values
                        String brokerageStr = getCellValueAsString(brokerageCell).trim();
                        if (!brokerageStr.isEmpty()) {
                            productDTO.setProductBrokerage(Float.parseFloat(brokerageStr));
                        }
                    }
                }

                Cell quantityCell = currentRow.getCell(2);
                if (quantityCell != null && quantityCell.getCellType() != CellType.BLANK) {
                    try {
                        productDTO.setQuantity((int) quantityCell.getNumericCellValue());
                    } catch (Exception e) {
                        String quantityStr = getCellValueAsString(quantityCell).trim();
                        if (!quantityStr.isEmpty()) {
                            productDTO.setQuantity(Integer.parseInt(quantityStr));
                        }
                    }
                }

                Cell priceCell = currentRow.getCell(3);
                if (priceCell != null && priceCell.getCellType() != CellType.BLANK) {
                    try {
                        productDTO.setPrice((int) priceCell.getNumericCellValue());
                    } catch (Exception e) {
                        String priceStr = getCellValueAsString(priceCell).trim();
                        if (!priceStr.isEmpty()) {
                            productDTO.setPrice(Integer.parseInt(priceStr));
                        }
                    }
                }

                Cell qualityCell = currentRow.getCell(4);
                if (qualityCell != null && qualityCell.getCellType() != CellType.BLANK) {
                    productDTO.setQuality(getCellValueAsString(qualityCell).trim());
                }

                Cell imgLinkCell = currentRow.getCell(5);
                if (imgLinkCell != null && imgLinkCell.getCellType() != CellType.BLANK) {
                    productDTO.setImgLink(getCellValueAsString(imgLinkCell).trim());
                }

                products.add(productDTO);
                rowNumber++;
            }

            workbook.close();
            return products;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}