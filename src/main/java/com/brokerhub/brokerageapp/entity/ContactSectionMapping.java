package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_section_mappings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactSectionMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_section_id", nullable = false)
    private ContactSection contactSection;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}