package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contact_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactSection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String sectionName;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Long brokerId;
    
    @Column
    private Long parentSectionId;
    
    @OneToMany(mappedBy = "contactSection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContactSectionMapping> contactMappings;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}