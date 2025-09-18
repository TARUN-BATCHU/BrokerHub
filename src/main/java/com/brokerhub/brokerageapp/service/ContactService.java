package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.ContactDTO;
import com.brokerhub.brokerageapp.dto.ContactSectionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {
    
    // Section Management
    ContactSectionDTO createSection(ContactSectionDTO sectionDTO);
    List<ContactSectionDTO> getAllSections(Long brokerId);
    List<ContactSectionDTO> getRootSections(Long brokerId);
    List<ContactSectionDTO> getChildSections(Long parentSectionId, Long brokerId);
    ContactSectionDTO getSectionById(Long sectionId, Long brokerId);
    ContactSectionDTO updateSection(Long sectionId, ContactSectionDTO sectionDTO, Long brokerId);
    void deleteSection(Long sectionId, Long brokerId);
    
    // Contact Management
    ContactDTO createContact(ContactDTO contactDTO);
    List<ContactDTO> getAllContacts(Long brokerId);
    List<ContactDTO> getContactsBySection(Long sectionId, Long brokerId);
    List<ContactDTO> getContactsBySections(List<Long> sectionIds, Long brokerId);
    ContactDTO getContactById(Long contactId, Long brokerId);
    ContactDTO updateContact(Long contactId, ContactDTO contactDTO, Long brokerId);
    void deleteContact(Long contactId, Long brokerId);
    
    // Section Assignment
    void addContactToSection(Long contactId, Long sectionId, Long brokerId);
    void removeContactFromSection(Long contactId, Long sectionId, Long brokerId);
    
    // Search and Pagination
    Page<ContactDTO> searchContacts(Long brokerId, String search, Pageable pageable);
    Page<ContactDTO> getContactsPaginated(Long brokerId, Pageable pageable);
}