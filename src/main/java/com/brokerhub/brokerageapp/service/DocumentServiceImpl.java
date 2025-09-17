package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.GeneratedDocument;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.GeneratedDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService {
    
    @Autowired
    private GeneratedDocumentRepository documentRepository;
    
    @Autowired
    private TenantContextService tenantContextService;
    
    @Autowired
    private BrokerRepository brokerRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<GeneratedDocument> getBrokerDocuments() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Fetching documents for broker ID: {}", currentBrokerId);
        List<GeneratedDocument> documents = documentRepository.findByBrokerBrokerIdAndStatusNotOrderByCreatedAtDesc(currentBrokerId, "DOWNLOADED");
        log.info("Found {} active documents for broker ID: {}", documents.size(), currentBrokerId);
        // Initialize the broker proxy to avoid lazy loading issues
        documents.forEach(doc -> doc.getBroker().getBrokerId());
        return documents;
    }
    
    @Override
    @Transactional
    public Resource downloadDocument(Long documentId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Optional<GeneratedDocument> docOpt = documentRepository.findById(documentId);
        
        if (!docOpt.isPresent() || !docOpt.get().getBroker().getBrokerId().equals(currentBrokerId)) {
            throw new RuntimeException("Document not found");
        }
        
        GeneratedDocument document = docOpt.get();
        if (!"COMPLETED".equals(document.getStatus())) {
            throw new RuntimeException("Document not ready");
        }
        
        try {
            String zipPath = createZipFile(document);
            
            // Mark document as downloaded
            document.setStatus("DOWNLOADED");
            documentRepository.save(document);
            log.info("Document {} marked as downloaded", documentId);
            
            return new FileSystemResource(zipPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create download", e);
        }
    }
    
    @Override
    public String getDocumentFilename(Long documentId) {
        Optional<GeneratedDocument> docOpt = documentRepository.findById(documentId);
        if (!docOpt.isPresent()) {
            return "document.zip";
        }
        
        GeneratedDocument doc = docOpt.get();
        String type = doc.getDocumentType().toLowerCase().replace("_", "-");
        String identifier = doc.getCity() != null ? doc.getCity() : "selected-users";
        
        return String.format("%s-%s-FY%d.zip", type, identifier, doc.getFinancialYearId());
    }
    
    private String createZipFile(GeneratedDocument document) throws IOException {
        String zipFileName = "temp/" + document.getDocumentId() + ".zip";
        File zipFile = new File(zipFileName);
        zipFile.getParentFile().mkdirs();
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File sourceDir = new File(document.getFilePath());
            if (sourceDir.exists() && sourceDir.isDirectory()) {
                addDirectoryToZip(sourceDir, sourceDir.getName(), zos);
            }
        }
        
        return zipFileName;
    }
    
    private void addDirectoryToZip(File dir, String baseName, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirectoryToZip(file, baseName + "/" + file.getName(), zos);
                } else {
                    ZipEntry entry = new ZipEntry(baseName + "/" + file.getName());
                    zos.putNextEntry(entry);
                    Files.copy(file.toPath(), zos);
                    zos.closeEntry();
                }
            }
        }
    }
    
    @Override
    public String createTestDocument() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Creating test document for broker ID: {}", currentBrokerId);
        
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found: " + currentBrokerId);
        }
        
        GeneratedDocument testDoc = GeneratedDocument.builder()
                .broker(brokerOpt.get())
                .financialYearId(2023L)
                .documentType("TEST_DOCUMENT")
                .status("COMPLETED")
                .city("TEST_CITY")
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .filePath("test/path")
                .build();
        
        GeneratedDocument saved = documentRepository.save(testDoc);
        log.info("Test document created with ID: {}", saved.getDocumentId());
        
        return "Test document created with ID: " + saved.getDocumentId();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GeneratedDocument> getAllDocumentsForDebug() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        log.info("Fetching ALL documents for broker ID: {}", currentBrokerId);
        List<GeneratedDocument> documents = documentRepository.findByBrokerBrokerIdOrderByCreatedAtDesc(currentBrokerId);
        log.info("Found {} total documents for broker ID: {}", documents.size(), currentBrokerId);
        // Initialize the broker proxy to avoid lazy loading issues
        documents.forEach(doc -> doc.getBroker().getBrokerId());
        return documents;
    }
}