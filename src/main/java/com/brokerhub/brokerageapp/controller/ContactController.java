package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.ApiResponse;
import com.brokerhub.brokerageapp.dto.ContactDTO;
import com.brokerhub.brokerageapp.dto.ContactSectionDTO;
import com.brokerhub.brokerageapp.security.SecurityContextUtil;
import com.brokerhub.brokerageapp.service.ContactService;
import com.brokerhub.brokerageapp.service.TenantContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BrokerHub/api/contacts")
@RequiredArgsConstructor
@Slf4j
public class ContactController {
    
    private final ContactService contactService;
    
    // Section Management APIs

    @Autowired
    SecurityContextUtil securityContextUtil;

    @Autowired
    TenantContextService tenantContextService;


    
    @PostMapping("/sections")
    public ResponseEntity<ApiResponse<ContactSectionDTO>> createSection(@RequestBody ContactSectionDTO sectionDTO) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            sectionDTO.setBrokerId(brokerId);
            
            ContactSectionDTO created = contactService.createSection(sectionDTO);
            return ResponseEntity.ok(new ApiResponse<>("true", "Section created successfully", created));
        } catch (Exception e) {
            log.error("Error creating section", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<ContactSectionDTO>>> getAllSections() {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            List<ContactSectionDTO> sections = contactService.getAllSections(brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Sections retrieved successfully", sections));
        } catch (Exception e) {
            log.error("Error retrieving sections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("false", "Failed to retrieve sections", null));
        }
    }
    
    @GetMapping("/sections/{sectionId}")
    public ResponseEntity<ApiResponse<ContactSectionDTO>> getSectionById(@PathVariable Long sectionId) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            ContactSectionDTO section = contactService.getSectionById(sectionId, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Section retrieved successfully", section));
        } catch (Exception e) {
            log.error("Error retrieving section", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @PutMapping("/sections/{sectionId}")
    public ResponseEntity<ApiResponse<ContactSectionDTO>> updateSection(
            @PathVariable Long sectionId, @RequestBody ContactSectionDTO sectionDTO) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            ContactSectionDTO updated = contactService.updateSection(sectionId, sectionDTO, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Section updated successfully", updated));
        } catch (Exception e) {
            log.error("Error updating section", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSection(@PathVariable Long sectionId) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            contactService.deleteSection(sectionId, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Section deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting section", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    // Contact Management APIs
    
    @PostMapping
    public ResponseEntity<ApiResponse<ContactDTO>> createContact(@RequestBody ContactDTO contactDTO) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            contactDTO.setBrokerId(brokerId);
            
            ContactDTO created = contactService.createContact(contactDTO);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contact created successfully", created));
        } catch (Exception e) {
            log.error("Error creating contact", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactDTO>>> getAllContacts() {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            List<ContactDTO> contacts = contactService.getAllContacts(brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contacts retrieved successfully", contacts));
        } catch (Exception e) {
            log.error("Error retrieving contacts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("false", "Failed to retrieve contacts", null));
        }
    }
    
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<ApiResponse<List<ContactDTO>>> getContactsBySection(@PathVariable Long sectionId) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            List<ContactDTO> contacts = contactService.getContactsBySection(sectionId, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contacts retrieved successfully", contacts));
        } catch (Exception e) {
            log.error("Error retrieving contacts by section", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @GetMapping("/sections/root")
    public ResponseEntity<ApiResponse<List<ContactSectionDTO>>> getRootSections() {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            List<ContactSectionDTO> sections = contactService.getRootSections(brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Root sections retrieved successfully", sections));
        } catch (Exception e) {
            log.error("Error retrieving root sections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("false", "Failed to retrieve root sections", null));
        }
    }
    
    @GetMapping("/sections/{parentId}/children")
    public ResponseEntity<ApiResponse<List<ContactSectionDTO>>> getChildSections(@PathVariable Long parentId) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            List<ContactSectionDTO> sections = contactService.getChildSections(parentId, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Child sections retrieved successfully", sections));
        } catch (Exception e) {
            log.error("Error retrieving child sections", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @PostMapping("/{contactId}/sections/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> addContactToSection(
            @PathVariable Long contactId, @PathVariable Long sectionId) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            contactService.addContactToSection(contactId, sectionId, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contact added to section successfully", null));
        } catch (Exception e) {
            log.error("Error adding contact to section", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{contactId}/sections/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> removeContactFromSection(
            @PathVariable Long contactId, @PathVariable Long sectionId) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            contactService.removeContactFromSection(contactId, sectionId, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contact removed from section successfully", null));
        } catch (Exception e) {
            log.error("Error removing contact from section", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @GetMapping("/{contactId}")
    public ResponseEntity<ApiResponse<ContactDTO>> getContactById(@PathVariable Long contactId) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            ContactDTO contact = contactService.getContactById(contactId, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contact retrieved successfully", contact));
        } catch (Exception e) {
            log.error("Error retrieving contact", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @PutMapping("/{contactId}")
    public ResponseEntity<ApiResponse<ContactDTO>> updateContact(
            @PathVariable Long contactId, @RequestBody ContactDTO contactDTO) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            ContactDTO updated = contactService.updateContact(contactId, contactDTO, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contact updated successfully", updated));
        } catch (Exception e) {
            log.error("Error updating contact", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{contactId}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long contactId) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            contactService.deleteContact(contactId, brokerId);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contact deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting contact", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("false", e.getMessage(), null));
        }
    }
    
    // Search and Pagination APIs
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ContactDTO>>> searchContacts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            Pageable pageable = PageRequest.of(page, size);
            Page<ContactDTO> contacts = contactService.searchContacts(brokerId, query, pageable);
            return ResponseEntity.ok(new ApiResponse<>("true", "Search completed successfully", contacts));
        } catch (Exception e) {
            log.error("Error searching contacts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("false", "Search failed", null));
        }
    }
    
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<ContactDTO>>> getContactsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long brokerId = tenantContextService.getCurrentBrokerId();
            Pageable pageable = PageRequest.of(page, size);
            Page<ContactDTO> contacts = contactService.getContactsPaginated(brokerId, pageable);
            return ResponseEntity.ok(new ApiResponse<>("true", "Contacts retrieved successfully", contacts));
        } catch (Exception e) {
            log.error("Error retrieving paginated contacts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("false", "Failed to retrieve contacts", null));
        }
    }
}