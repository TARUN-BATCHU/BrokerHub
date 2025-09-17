package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class BulkBillGenerationServiceImpl implements BulkBillGenerationService {
    
    @Autowired
    @Lazy
    private BrokerageService brokerageService;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ExcelGenerationService excelGenerationService;
    
    @Override
    public byte[] generateBulkBillsHtmlSync(List<Long> userIds, Broker broker, Long financialYearId) {
        try {
            log.info("Starting synchronous HTML bulk bill generation for {} users", userIds.size());
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            
            int successCount = 0;
            for (Long userId : userIds) {
                try {
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        
                        // Generate user brokerage detail
                        UserBrokerageDetailDTO userDetail = brokerageService.getUserBrokerageDetailInFinancialYear(
                            userId, broker.getBrokerId(), financialYearId);
                        
                        // Generate HTML bill
                        byte[] billHtml = pdfGenerationService.generateUserBrokerageBill(userDetail, broker, financialYearId);
                        
                        // Add to zip
                        String fileName = "bill_" + userId + "_" + sanitizeFileName(user.getFirmName()) + ".html";
                        ZipEntry entry = new ZipEntry(fileName);
                        zos.putNextEntry(entry);
                        zos.write(billHtml);
                        zos.closeEntry();
                        
                        successCount++;
                        log.debug("Generated HTML bill for user: {}", user.getFirmName());
                    } else {
                        log.warn("User not found: {}", userId);
                    }
                } catch (Exception e) {
                    log.error("Failed to generate HTML bill for user: {}", userId, e);
                }
            }
            
            zos.close();
            log.info("Completed HTML bulk bill generation. Generated {} out of {} files", successCount, userIds.size());
            
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error in HTML bulk bill generation", e);
            throw new RuntimeException("Failed to generate HTML bulk bills: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] generateBulkBillsExcelSync(List<Long> userIds, Broker broker, Long financialYearId) {
        try {
            log.info("Starting synchronous Excel bulk bill generation for {} users", userIds.size());
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            
            int successCount = 0;
            for (Long userId : userIds) {
                try {
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        
                        // Generate user brokerage detail
                        UserBrokerageDetailDTO userDetail = brokerageService.getUserBrokerageDetailInFinancialYear(
                            userId, broker.getBrokerId(), financialYearId);
                        
                        // Generate Excel bill
                        byte[] excelData = excelGenerationService.generateUserBrokerageExcel(userDetail, broker, financialYearId);
                        
                        // Add to zip
                        String fileName = sanitizeFileName(user.getFirmName()) + "-brokerage-bill-FY" + financialYearId + ".xlsx";
                        ZipEntry entry = new ZipEntry(fileName);
                        zos.putNextEntry(entry);
                        zos.write(excelData);
                        zos.closeEntry();
                        
                        successCount++;
                        log.debug("Generated Excel bill for user: {}", user.getFirmName());
                    } else {
                        log.warn("User not found: {}", userId);
                    }
                } catch (Exception e) {
                    log.error("Failed to generate Excel bill for user: {}", userId, e);
                }
            }
            
            zos.close();
            log.info("Completed Excel bulk bill generation. Generated {} out of {} files", successCount, userIds.size());
            
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error in Excel bulk bill generation", e);
            throw new RuntimeException("Failed to generate Excel bulk bills: " + e.getMessage(), e);
        }
    }
    
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "Unknown";
        }
        return fileName.replaceAll("[^a-zA-Z0-9\\s-_]", "").replaceAll("\\s+", "-");
    }
    

}