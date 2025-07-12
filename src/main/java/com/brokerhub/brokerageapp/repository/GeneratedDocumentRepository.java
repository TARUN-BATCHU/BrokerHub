package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.GeneratedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, Long> {
    
    List<GeneratedDocument> findByBrokerBrokerIdAndStatusOrderByCreatedAtDesc(Long brokerId, String status);
    
    List<GeneratedDocument> findByBrokerBrokerIdOrderByCreatedAtDesc(Long brokerId);
    
    @Query("SELECT gd FROM GeneratedDocument gd WHERE gd.broker.brokerId = :brokerId AND gd.documentType = :documentType ORDER BY gd.createdAt DESC")
    List<GeneratedDocument> findByBrokerIdAndDocumentType(@Param("brokerId") Long brokerId, @Param("documentType") String documentType);
}