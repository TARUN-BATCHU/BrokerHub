package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.GeneratedDocument;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.GeneratedDocumentRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BulkBillGenerationServiceImpl implements BulkBillGenerationService {
    
    @Autowired
    private BrokerageService brokerageService;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BrokerRepository brokerRepository;
    
    @Autowired
    private TenantContextService tenantContextService;
    
    @Autowired
    private GeneratedDocumentRepository documentRepository;
    
    @Autowired
    private ExcelGenerationService excelGenerationService;
    
    @Override
    @Async
    public void generateBulkBillsForCity(String city, Long brokerId, Long financialYearId) {
        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
            if (!brokerOpt.isPresent()) {
                log.error("Broker not found: {}", currentBrokerId);
                return;
            }
            Broker broker = brokerOpt.get();
            
            List<User> cityUsers = userRepository.findByBrokerBrokerIdAndAddressCity(currentBrokerId, city);
            log.info("Starting bulk bill generation for {} users in city: {}", cityUsers.size(), city);
            
            // Create document tracking record
            GeneratedDocument document = GeneratedDocument.builder()
                    .broker(broker)
                    .financialYearId(financialYearId)
                    .documentType("BULK_CITY_BILLS")
                    .status("GENERATING")
                    .city(city)
                    .createdAt(LocalDateTime.now())
                    .build();
            document = documentRepository.save(document);
            
            String outputDir = "bills/" + currentBrokerId + "/" + financialYearId + "/" + city + "/";
            createDirectoryIfNotExists(outputDir);
            
            for (User user : cityUsers) {
                try {
                    generateUserBill(user, broker, financialYearId, outputDir);
                } catch (Exception e) {
                    log.error("Failed to generate bill for user: {}", user.getUserId(), e);
                }
            }
            
            // Update document status
            document.setStatus("COMPLETED");
            document.setCompletedAt(LocalDateTime.now());
            document.setFilePath(outputDir);
            documentRepository.save(document);
            
            log.info("Completed bulk bill generation for city: {}", city);
        } catch (Exception e) {
            log.error("Error in bulk bill generation for city: {}", city, e);
            // Update document status to failed if exists
            try {
                List<GeneratedDocument> docs = documentRepository.findByBrokerBrokerIdAndStatusOrderByCreatedAtDesc(currentBrokerId, "GENERATING");
                docs.stream().filter(d -> city.equals(d.getCity())).findFirst().ifPresent(d -> {
                    d.setStatus("FAILED");
                    d.setCompletedAt(LocalDateTime.now());
                    documentRepository.save(d);
                });
            } catch (Exception ex) {
                log.error("Failed to update document status", ex);
            }
        }
    }
    
    @Override
    @Async
    public void generateBulkBillsForUsers(List<Long> userIds, Long brokerId, Long financialYearId) {
        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
            if (!brokerOpt.isPresent()) {
                log.error("Broker not found: {}", currentBrokerId);
                return;
            }
            Broker broker = brokerOpt.get();
            
            log.info("Starting bulk bill generation for {} users", userIds.size());
            
            // Create document tracking record
            GeneratedDocument document = GeneratedDocument.builder()
                    .broker(broker)
                    .financialYearId(financialYearId)
                    .documentType("BULK_USER_BILLS")
                    .status("GENERATING")
                    .userIds(userIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .createdAt(LocalDateTime.now())
                    .build();
            document = documentRepository.save(document);
            
            String outputDir = "bills/" + currentBrokerId + "/" + financialYearId + "/selected/";
            createDirectoryIfNotExists(outputDir);
            
            for (Long userId : userIds) {
                try {
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        generateUserBill(userOpt.get(), broker, financialYearId, outputDir);
                    }
                } catch (Exception e) {
                    log.error("Failed to generate bill for user: {}", userId, e);
                }
            }
            
            // Update document status
            document.setStatus("COMPLETED");
            document.setCompletedAt(LocalDateTime.now());
            document.setFilePath(outputDir);
            documentRepository.save(document);
            
            log.info("Completed bulk bill generation for selected users");
        } catch (Exception e) {
            log.error("Error in bulk bill generation for users", e);
            // Update document status to failed if exists
            try {
                List<GeneratedDocument> docs = documentRepository.findByBrokerBrokerIdAndStatusOrderByCreatedAtDesc(currentBrokerId, "GENERATING");
                docs.stream().filter(d -> "BULK_USER_BILLS".equals(d.getDocumentType())).findFirst().ifPresent(d -> {
                    d.setStatus("FAILED");
                    d.setCompletedAt(LocalDateTime.now());
                    documentRepository.save(d);
                });
            } catch (Exception ex) {
                log.error("Failed to update document status", ex);
            }
        }
    }
    
    private void generateUserBill(User user, Broker broker, Long financialYearId, String outputDir) throws IOException {
        UserBrokerageDetailDTO userDetail = brokerageService.getUserBrokerageDetailInFinancialYear(
            user.getUserId(), broker.getBrokerId(), financialYearId);
        
        byte[] billPdf = pdfGenerationService.generateUserBrokerageBill(userDetail, broker, financialYearId);
        
        String fileName = outputDir + "bill_" + user.getUserId() + "_" + user.getFirmName().replaceAll("[^a-zA-Z0-9]", "_") + ".html";
        
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(billPdf);
        }
        
        log.info("Generated bill for user: {} at {}", user.getFirmName(), fileName);
    }
    
    private void generateUserExcel(User user, Broker broker, Long financialYearId, String outputDir) throws IOException {
        UserBrokerageDetailDTO userDetail = brokerageService.getUserBrokerageDetailInFinancialYear(
            user.getUserId(), broker.getBrokerId(), financialYearId);
        
        byte[] excelData = excelGenerationService.generateUserBrokerageExcel(userDetail, broker, financialYearId);
        
        String fileName = outputDir + "excel_" + user.getUserId() + "_" + user.getFirmName().replaceAll("[^a-zA-Z0-9]", "_") + ".xlsx";
        
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(excelData);
        }
        
        log.info("Generated Excel for user: {} at {}", user.getFirmName(), fileName);
    }
    
    @Override
    @Async
    public void generateBulkExcelForCity(String city, Long brokerId, Long financialYearId) {
        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
            if (!brokerOpt.isPresent()) {
                log.error("Broker not found: {}", currentBrokerId);
                return;
            }
            Broker broker = brokerOpt.get();
            
            List<User> cityUsers = userRepository.findByBrokerBrokerIdAndAddressCity(currentBrokerId, city);
            log.info("Starting bulk Excel generation for {} users in city: {}", cityUsers.size(), city);
            
            GeneratedDocument document = GeneratedDocument.builder()
                    .broker(broker)
                    .financialYearId(financialYearId)
                    .documentType("BULK_CITY_EXCEL")
                    .status("GENERATING")
                    .city(city)
                    .createdAt(LocalDateTime.now())
                    .build();
            document = documentRepository.save(document);
            
            String outputDir = "excel/" + currentBrokerId + "/" + financialYearId + "/" + city + "/";
            createDirectoryIfNotExists(outputDir);
            
            for (User user : cityUsers) {
                try {
                    generateUserExcel(user, broker, financialYearId, outputDir);
                } catch (Exception e) {
                    log.error("Failed to generate Excel for user: {}", user.getUserId(), e);
                }
            }
            
            document.setStatus("COMPLETED");
            document.setCompletedAt(LocalDateTime.now());
            document.setFilePath(outputDir);
            documentRepository.save(document);
            
            log.info("Completed bulk Excel generation for city: {}", city);
        } catch (Exception e) {
            log.error("Error in bulk Excel generation for city: {}", city, e);
        }
    }
    
    @Override
    @Async
    public void generateBulkExcelForUsers(List<Long> userIds, Long brokerId, Long financialYearId) {
        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
            if (!brokerOpt.isPresent()) {
                log.error("Broker not found: {}", currentBrokerId);
                return;
            }
            Broker broker = brokerOpt.get();
            
            log.info("Starting bulk Excel generation for {} users", userIds.size());
            
            GeneratedDocument document = GeneratedDocument.builder()
                    .broker(broker)
                    .financialYearId(financialYearId)
                    .documentType("BULK_USER_EXCEL")
                    .status("GENERATING")
                    .userIds(userIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .createdAt(LocalDateTime.now())
                    .build();
            document = documentRepository.save(document);
            
            String outputDir = "excel/" + currentBrokerId + "/" + financialYearId + "/selected/";
            createDirectoryIfNotExists(outputDir);
            
            for (Long userId : userIds) {
                try {
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        generateUserExcel(userOpt.get(), broker, financialYearId, outputDir);
                    }
                } catch (Exception e) {
                    log.error("Failed to generate Excel for user: {}", userId, e);
                }
            }
            
            document.setStatus("COMPLETED");
            document.setCompletedAt(LocalDateTime.now());
            document.setFilePath(outputDir);
            documentRepository.save(document);
            
            log.info("Completed bulk Excel generation for selected users");
        } catch (Exception e) {
            log.error("Error in bulk Excel generation for users", e);
        }
    }
    
    private void createDirectoryIfNotExists(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}