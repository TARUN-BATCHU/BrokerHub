package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneratedDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    private Broker broker;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private Long financialYearId;
    
    private String documentType; // BROKERAGE_BILL, BULK_CITY_BILLS, BULK_USER_BILLS
    
    private String fileName;
    
    private String filePath;
    
    private String status; // GENERATING, COMPLETED, FAILED
    
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    private String city; // For bulk city bills
    
    private String userIds; // Comma-separated user IDs for bulk user bills
}