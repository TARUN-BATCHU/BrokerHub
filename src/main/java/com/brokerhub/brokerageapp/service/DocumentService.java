package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.GeneratedDocument;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentService {
    List<GeneratedDocument> getBrokerDocuments();
    Resource downloadDocument(Long documentId);
    String getDocumentFilename(Long documentId);
    String createTestDocument();
    List<GeneratedDocument> getAllDocumentsForDebug();
}