package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact_phones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactPhone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 15)
    private String phoneNumber;
    
    @Column(length = 50)
    private String phoneType; // Primary, Secondary, WhatsApp, etc.
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;
}