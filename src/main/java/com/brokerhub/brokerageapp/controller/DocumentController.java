package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ApiResponse;
import com.brokerhub.brokerageapp.entity.GeneratedDocument;
import com.brokerhub.brokerageapp.repository.GeneratedDocumentRepository;
import com.brokerhub.brokerageapp.service.TenantContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BrokerHub/Documents")
@Slf4j
public class DocumentController {
    
    @Autowired
    private GeneratedDocumentRepository documentRepository;
    
    @Autowired
    private TenantContextService tenantContextService;
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<GeneratedDocument>>> getDocumentStatus() {
        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            List<GeneratedDocument> documents = documentRepository.findByBrokerBrokerIdOrderByCreatedAtDesc(currentBrokerId);
            return ResponseEntity.ok(ApiResponse.success(documents, "Document status retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting document status", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get document status: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{documentType}")
    public ResponseEntity<ApiResponse<List<GeneratedDocument>>> getDocumentStatusByType(
            @PathVariable String documentType) {
        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            List<GeneratedDocument> documents = documentRepository.findByBrokerIdAndDocumentType(currentBrokerId, documentType);
            return ResponseEntity.ok(ApiResponse.success(documents, "Document status retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting document status by type", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get document status: " + e.getMessage()));
        }
    }
}