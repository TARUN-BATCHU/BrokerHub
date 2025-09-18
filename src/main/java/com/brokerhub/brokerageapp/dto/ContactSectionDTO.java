package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactSectionDTO {
    private Long id;
    private String sectionName;
    private String description;
    private Long brokerId;
    private Long parentSectionId;
    private String parentSectionName;
    private List<ContactSectionDTO> childSections;
    private List<ContactDTO> contacts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}