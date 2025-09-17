package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ApiResponse;
import com.brokerhub.brokerageapp.entity.GeneratedDocument;
import com.brokerhub.brokerageapp.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BrokerHub/Documents")
@Slf4j
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<GeneratedDocument>>> getDocumentStatus() {
        try {
            List<GeneratedDocument> documents = documentService.getBrokerDocuments();
            return ResponseEntity.ok(ApiResponse.success(documents, "Documents retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving documents", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve documents"));
        }
    }
    
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Resource resource = documentService.downloadDocument(documentId);
            String filename = documentService.getDocumentFilename(documentId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading document", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/test-create")
    public ResponseEntity<ApiResponse<String>> testCreateDocument() {
        try {
            String result = documentService.createTestDocument();
            return ResponseEntity.ok(ApiResponse.success(result, "Test document created"));
        } catch (Exception e) {
            log.error("Error creating test document", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create test document: " + e.getMessage()));
        }
    }
    
    @GetMapping("/debug/all")
    public ResponseEntity<ApiResponse<List<GeneratedDocument>>> getAllDocumentsDebug() {
        try {
            List<GeneratedDocument> documents = documentService.getAllDocumentsForDebug();
            return ResponseEntity.ok(ApiResponse.success(documents, "All documents retrieved"));
        } catch (Exception e) {
            log.error("Error retrieving all documents", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve documents"));
        }
    }
}