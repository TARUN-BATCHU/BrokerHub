package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ApiResponse;
import com.brokerhub.brokerageapp.dto.BrokerageSummaryDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.service.BrokerageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/BrokerHub/Brokerage")
@Slf4j
public class BrokerageController {
    
    @Autowired
    private BrokerageService brokerageService;
    
    @GetMapping("/total/{financialYearId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBrokerageInFinancialYear(
            @PathVariable Long financialYearId) {
        try {
            BigDecimal totalBrokerage = brokerageService.getTotalBrokerageInFinancialYear(null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success(totalBrokerage, "Total brokerage retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting total brokerage", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get total brokerage: " + e.getMessage()));
        }
    }
    
    @GetMapping("/summary/{financialYearId}")
    public ResponseEntity<ApiResponse<BrokerageSummaryDTO>> getBrokerageSummaryInFinancialYear(
            @PathVariable Long financialYearId) {
        try {
            BrokerageSummaryDTO summary = brokerageService.getBrokerageSummaryInFinancialYear(null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success(summary, "Brokerage summary retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting brokerage summary", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get brokerage summary: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/{financialYearId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getUserTotalBrokerageInFinancialYear(
            @PathVariable Long userId,
            @PathVariable Long financialYearId) {
        try {
            BigDecimal userBrokerage = brokerageService.getUserTotalBrokerageInFinancialYear(userId, null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success(userBrokerage, "User total brokerage retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting user total brokerage", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get user total brokerage: " + e.getMessage()));
        }
    }
    
    @GetMapping("/city/{city}/{financialYearId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getCityTotalBrokerageInFinancialYear(
            @PathVariable String city,
            @PathVariable Long financialYearId) {
        try {
            BigDecimal cityBrokerage = brokerageService.getCityTotalBrokerageInFinancialYear(city, null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success(cityBrokerage, "City total brokerage retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting city total brokerage", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get city total brokerage: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user-detail/{userId}/{financialYearId}")
    public ResponseEntity<ApiResponse<UserBrokerageDetailDTO>> getUserBrokerageDetailInFinancialYear(
            @PathVariable Long userId,
            @PathVariable Long financialYearId) {
        try {
            UserBrokerageDetailDTO userDetail = brokerageService.getUserBrokerageDetailInFinancialYear(userId, null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success(userDetail, "User brokerage detail retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting user brokerage detail", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get user brokerage detail: " + e.getMessage()));
        }
    }
    
    @GetMapping("/bill/{userId}/{financialYearId}")
    public ResponseEntity<byte[]> generateUserBrokerageBill(
            @PathVariable Long userId,
            @PathVariable Long financialYearId) {
        try {
            byte[] billPdf = brokerageService.generateUserBrokerageBill(userId, null, financialYearId);
            return ResponseEntity.ok()
                    .header("Content-Type", "text/html")
                    .header("Content-Disposition", "attachment; filename=brokerage-bill-" + userId + ".html")
                    .body(billPdf);
        } catch (Exception e) {
            log.error("Error generating user brokerage bill", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/excel/user/{userId}/{financialYearId}")
    public ResponseEntity<byte[]> generateUserBrokerageExcel(
            @PathVariable Long userId,
            @PathVariable Long financialYearId) {
        try {
            byte[] excelData = brokerageService.generateUserBrokerageExcel(userId, null, financialYearId);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=brokerage-bill-" + userId + ".xlsx")
                    .body(excelData);
        } catch (Exception e) {
            log.error("Error generating user brokerage Excel", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/excel/summary/{financialYearId}")
    public ResponseEntity<byte[]> generateBrokerageSummaryExcel(
            @PathVariable Long financialYearId) {
        try {
            byte[] excelData = brokerageService.generateBrokerageSummaryExcel(null, financialYearId);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=brokerage-summary-" + financialYearId + ".xlsx")
                    .body(excelData);
        } catch (Exception e) {
            log.error("Error generating brokerage summary Excel", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/excel/city/{city}/{financialYearId}")
    public ResponseEntity<byte[]> generateCityBrokerageExcel(
            @PathVariable String city,
            @PathVariable Long financialYearId) {
        try {
            byte[] excelData = brokerageService.generateCityBrokerageExcel(city, null, financialYearId);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=city-brokerage-" + city + "-" + financialYearId + ".xlsx")
                    .body(excelData);
        } catch (Exception e) {
            log.error("Error generating city brokerage Excel", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/bulk-bills/city/{city}/{financialYearId}")
    public ResponseEntity<ApiResponse<String>> generateBulkBillsForCity(
            @PathVariable String city,
            @PathVariable Long financialYearId) {
        try {
            brokerageService.generateBulkBillsForCity(city, null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success("Bulk bill generation started for city: " + city, "Request processed successfully"));
        } catch (Exception e) {
            log.error("Error generating bulk bills for city", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate bulk bills: " + e.getMessage()));
        }
    }
    
    @PostMapping("/bulk-bills/users/{financialYearId}")
    public ResponseEntity<ApiResponse<String>> generateBulkBillsForUsers(
            @RequestBody List<Long> userIds,
            @PathVariable Long financialYearId) {
        try {
            brokerageService.generateBulkBillsForUsers(userIds, null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success("Bulk bill generation started for " + userIds.size() + " users", "Request processed successfully"));
        } catch (Exception e) {
            log.error("Error generating bulk bills for users", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate bulk bills: " + e.getMessage()));
        }
    }
    
    @PostMapping("/bulk-excel/city/{city}/{financialYearId}")
    public ResponseEntity<ApiResponse<String>> generateBulkExcelForCity(
            @PathVariable String city,
            @PathVariable Long financialYearId) {
        try {
            brokerageService.generateBulkExcelForCity(city, null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success("Bulk Excel generation started for city: " + city, "Request processed successfully"));
        } catch (Exception e) {
            log.error("Error generating bulk Excel for city", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate bulk Excel: " + e.getMessage()));
        }
    }
    
    @PostMapping("/bulk-excel/users/{financialYearId}")
    public ResponseEntity<ApiResponse<String>> generateBulkExcelForUsers(
            @RequestBody List<Long> userIds,
            @PathVariable Long financialYearId) {
        try {
            brokerageService.generateBulkExcelForUsers(userIds, null, financialYearId);
            return ResponseEntity.ok(ApiResponse.success("Bulk Excel generation started for " + userIds.size() + " users", "Request processed successfully"));
        } catch (Exception e) {
            log.error("Error generating bulk Excel for users", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate bulk Excel: " + e.getMessage()));
        }
    }
}
