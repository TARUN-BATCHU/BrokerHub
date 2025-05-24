package com.brokerhub.brokerageapp.utils;

import com.brokerhub.brokerageapp.dto.UserDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERS = {"userType", "gstNumber", "firmName", "ownerName", "city", "area", "pincode",
                              "email", "bankName", "accountNumber", "ifscCode", "branch", "phoneNumbers",
                              "brokerageRate", "shopNumber", "byProduct"};
    static String SHEET = "Users";

    public static boolean hasExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return TYPE.equals(contentType) ||
               "application/vnd.ms-excel".equals(contentType) ||
               file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".xlsx");
    }

    public static List<UserDTO> excelToUserDTOs(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);

            if (sheet == null) {
                sheet = workbook.getSheetAt(0); // Use first sheet if "Users" sheet not found
            }

            Iterator<Row> rows = sheet.iterator();
            List<UserDTO> userDTOs = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header row
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                UserDTO userDTO = new UserDTO();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0: // userType
                            userDTO.setUserType(getCellValueAsString(currentCell));
                            break;
                        case 1: // gstNumber
                            userDTO.setGstNumber(getCellValueAsString(currentCell));
                            break;
                        case 2: // firmName
                            userDTO.setFirmName(getCellValueAsString(currentCell));
                            break;
                        case 3: // ownerName
                            userDTO.setOwnerName(getCellValueAsString(currentCell));
                            break;
                        case 4: // city
                            userDTO.setCity(getCellValueAsString(currentCell));
                            break;
                        case 5: // area
                            userDTO.setArea(getCellValueAsString(currentCell));
                            break;
                        case 6: // pincode
                            userDTO.setPincode(getCellValueAsString(currentCell));
                            break;
                        case 7: // email
                            userDTO.setEmail(getCellValueAsString(currentCell));
                            break;
                        case 8: // bankName
                            userDTO.setBankName(getCellValueAsString(currentCell));
                            break;
                        case 9: // accountNumber
                            userDTO.setAccountNumber(getCellValueAsString(currentCell));
                            break;
                        case 10: // ifscCode
                            userDTO.setIfscCode(getCellValueAsString(currentCell));
                            break;
                        case 11: // branch
                            userDTO.setBranch(getCellValueAsString(currentCell));
                            break;
                        case 12: // phoneNumbers (comma-separated)
                            String phoneNumbersStr = getCellValueAsString(currentCell);
                            if (phoneNumbersStr != null && !phoneNumbersStr.trim().isEmpty()) {
                                List<String> phoneNumbers = Arrays.asList(phoneNumbersStr.split(","));
                                userDTO.setPhoneNumbers(phoneNumbers.stream()
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .toList());
                            }
                            break;
                        case 13: // brokerageRate
                            String brokerageRateStr = getCellValueAsString(currentCell);
                            if (brokerageRateStr != null && !brokerageRateStr.trim().isEmpty()) {
                                try {
                                    userDTO.setBrokerageRate(Integer.parseInt(brokerageRateStr.trim()));
                                } catch (NumberFormatException e) {
                                    // Set default or skip
                                    userDTO.setBrokerageRate(0);
                                }
                            }
                            break;
                        case 14: // shopNumber
                            userDTO.setShopNumber(getCellValueAsString(currentCell));
                            break;
                        case 15: // byProduct (for MILLER type)
                            userDTO.setByProduct(getCellValueAsString(currentCell));
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }

                // Only add if firmName is present (required field)
                if (userDTO.getFirmName() != null && !userDTO.getFirmName().trim().isEmpty()) {
                    userDTOs.add(userDTO);
                }

                rowNumber++;
            }

            workbook.close();
            return userDTOs;

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convert numeric to string, removing decimal if it's a whole number
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
