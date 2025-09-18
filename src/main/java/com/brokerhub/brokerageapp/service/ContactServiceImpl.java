package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.*;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.exception.ResourceNotFoundException;
import com.brokerhub.brokerageapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContactServiceImpl implements ContactService {
    
    private final ContactSectionRepository sectionRepository;
    private final ContactRepository contactRepository;
    private final ContactPhoneRepository phoneRepository;
    private final ContactAddressRepository addressRepository;
    private final ContactSectionMappingRepository mappingRepository;
    
    @Override
    public ContactSectionDTO createSection(ContactSectionDTO sectionDTO) {
        if (sectionRepository.existsBySectionNameAndBrokerIdAndParentSectionId(
                sectionDTO.getSectionName(), sectionDTO.getBrokerId(), sectionDTO.getParentSectionId())) {
            throw new IllegalArgumentException("Section with this name already exists in this parent");
        }
        
        ContactSection section = new ContactSection();
        section.setSectionName(sectionDTO.getSectionName());
        section.setDescription(sectionDTO.getDescription());
        section.setBrokerId(sectionDTO.getBrokerId());
        section.setParentSectionId(sectionDTO.getParentSectionId());
        
        ContactSection saved = sectionRepository.save(section);
        return mapToSectionDTO(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContactSectionDTO> getAllSections(Long brokerId) {
        return sectionRepository.findByBrokerIdOrderBySectionNameAsc(brokerId)
                .stream()
                .map(this::mapToSectionDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContactSectionDTO> getRootSections(Long brokerId) {
        return sectionRepository.findByBrokerIdAndParentSectionIdIsNullOrderBySectionNameAsc(brokerId)
                .stream()
                .map(this::mapToSectionDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContactSectionDTO> getChildSections(Long parentSectionId, Long brokerId) {
        return sectionRepository.findByBrokerIdAndParentSectionIdOrderBySectionNameAsc(brokerId, parentSectionId)
                .stream()
                .map(this::mapToSectionDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ContactSectionDTO getSectionById(Long sectionId, Long brokerId) {
        ContactSection section = sectionRepository.findByIdAndBrokerId(sectionId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        return mapToSectionDTO(section);
    }
    
    @Override
    public ContactSectionDTO updateSection(Long sectionId, ContactSectionDTO sectionDTO, Long brokerId) {
        ContactSection section = sectionRepository.findByIdAndBrokerId(sectionId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        
        section.setSectionName(sectionDTO.getSectionName());
        section.setDescription(sectionDTO.getDescription());
        section.setParentSectionId(sectionDTO.getParentSectionId());
        
        ContactSection updated = sectionRepository.save(section);
        return mapToSectionDTO(updated);
    }
    
    @Override
    public void deleteSection(Long sectionId, Long brokerId) {
        ContactSection section = sectionRepository.findByIdAndBrokerId(sectionId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        
        long contactCount = contactRepository.countByContactSectionIdAndBrokerId(sectionId, brokerId);
        if (contactCount > 0) {
            throw new IllegalArgumentException("Cannot delete section with existing contacts");
        }
        
        sectionRepository.delete(section);
    }
    
    @Override
    public ContactDTO createContact(ContactDTO contactDTO) {
        Contact contact = new Contact();
        contact.setFirmName(contactDTO.getFirmName());
        contact.setUserName(contactDTO.getUserName());
        contact.setGstNumber(contactDTO.getGstNumber());
        contact.setAdditionalInfo(contactDTO.getAdditionalInfo());
        contact.setBrokerId(contactDTO.getBrokerId());
        
        Contact saved = contactRepository.save(contact);
        
        // Create section mappings
        if (contactDTO.getSectionIds() != null) {
            for (Long sectionId : contactDTO.getSectionIds()) {
                ContactSection section = sectionRepository.findByIdAndBrokerId(sectionId, contactDTO.getBrokerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + sectionId));
                
                ContactSectionMapping mapping = new ContactSectionMapping();
                mapping.setContact(saved);
                mapping.setContactSection(section);
                mappingRepository.save(mapping);
            }
        }
        
        // Save phone numbers
        if (contactDTO.getPhoneNumbers() != null) {
            for (ContactPhoneDTO phoneDTO : contactDTO.getPhoneNumbers()) {
                ContactPhone phone = new ContactPhone();
                phone.setPhoneNumber(phoneDTO.getPhoneNumber());
                phone.setPhoneType(phoneDTO.getPhoneType());
                phone.setContact(saved);
                phoneRepository.save(phone);
            }
        }
        
        // Save addresses
        if (contactDTO.getAddresses() != null) {
            for (ContactAddressDTO addressDTO : contactDTO.getAddresses()) {
                ContactAddress address = new ContactAddress();
                address.setAddress(addressDTO.getAddress());
                address.setCity(addressDTO.getCity());
                address.setState(addressDTO.getState());
                address.setPincode(addressDTO.getPincode());
                address.setAddressType(addressDTO.getAddressType());
                address.setContact(saved);
                addressRepository.save(address);
            }
        }
        
        return getContactById(saved.getId(), contactDTO.getBrokerId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> getAllContacts(Long brokerId) {
        return contactRepository.findByBrokerIdOrderByUserNameAsc(brokerId)
                .stream()
                .map(this::mapToContactDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> getContactsBySection(Long sectionId, Long brokerId) {
        return contactRepository.findByContactSectionIdAndBrokerIdOrderByUserNameAsc(sectionId, brokerId)
                .stream()
                .map(this::mapToContactDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> getContactsBySections(List<Long> sectionIds, Long brokerId) {
        List<ContactSectionMapping> mappings = mappingRepository.findBySectionIds(sectionIds);
        return mappings.stream()
                .map(mapping -> mapping.getContact())
                .filter(contact -> contact.getBrokerId().equals(brokerId))
                .distinct()
                .map(this::mapToContactDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ContactDTO getContactById(Long contactId, Long brokerId) {
        Contact contact = contactRepository.findByIdAndBrokerId(contactId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        return mapToContactDTO(contact);
    }
    
    @Override
    public ContactDTO updateContact(Long contactId, ContactDTO contactDTO, Long brokerId) {
        Contact contact = contactRepository.findByIdAndBrokerId(contactId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        
        contact.setFirmName(contactDTO.getFirmName());
        contact.setUserName(contactDTO.getUserName());
        contact.setGstNumber(contactDTO.getGstNumber());
        contact.setAdditionalInfo(contactDTO.getAdditionalInfo());
        
        contactRepository.save(contact);
        
        // Update section mappings
        mappingRepository.deleteByContactId(contactId);
        if (contactDTO.getSectionIds() != null) {
            for (Long sectionId : contactDTO.getSectionIds()) {
                ContactSection section = sectionRepository.findByIdAndBrokerId(sectionId, brokerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + sectionId));
                
                ContactSectionMapping mapping = new ContactSectionMapping();
                mapping.setContact(contact);
                mapping.setContactSection(section);
                mappingRepository.save(mapping);
            }
        }
        
        // Update phone numbers
        phoneRepository.deleteByContactId(contactId);
        if (contactDTO.getPhoneNumbers() != null) {
            for (ContactPhoneDTO phoneDTO : contactDTO.getPhoneNumbers()) {
                ContactPhone phone = new ContactPhone();
                phone.setPhoneNumber(phoneDTO.getPhoneNumber());
                phone.setPhoneType(phoneDTO.getPhoneType());
                phone.setContact(contact);
                phoneRepository.save(phone);
            }
        }
        
        // Update addresses
        addressRepository.deleteByContactId(contactId);
        if (contactDTO.getAddresses() != null) {
            for (ContactAddressDTO addressDTO : contactDTO.getAddresses()) {
                ContactAddress address = new ContactAddress();
                address.setAddress(addressDTO.getAddress());
                address.setCity(addressDTO.getCity());
                address.setState(addressDTO.getState());
                address.setPincode(addressDTO.getPincode());
                address.setAddressType(addressDTO.getAddressType());
                address.setContact(contact);
                addressRepository.save(address);
            }
        }
        
        return getContactById(contactId, brokerId);
    }
    
    @Override
    public void deleteContact(Long contactId, Long brokerId) {
        Contact contact = contactRepository.findByIdAndBrokerId(contactId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        
        mappingRepository.deleteByContactId(contactId);
        phoneRepository.deleteByContactId(contactId);
        addressRepository.deleteByContactId(contactId);
        contactRepository.delete(contact);
    }
    
    @Override
    public void addContactToSection(Long contactId, Long sectionId, Long brokerId) {
        Contact contact = contactRepository.findByIdAndBrokerId(contactId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        
        ContactSection section = sectionRepository.findByIdAndBrokerId(sectionId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        
        if (!mappingRepository.existsByContactIdAndContactSectionId(contactId, sectionId)) {
            ContactSectionMapping mapping = new ContactSectionMapping();
            mapping.setContact(contact);
            mapping.setContactSection(section);
            mappingRepository.save(mapping);
        }
    }
    
    @Override
    public void removeContactFromSection(Long contactId, Long sectionId, Long brokerId) {
        Contact contact = contactRepository.findByIdAndBrokerId(contactId, brokerId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        
        mappingRepository.deleteByContactIdAndContactSectionId(contactId, sectionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContactDTO> searchContacts(Long brokerId, String search, Pageable pageable) {
        Page<Contact> contacts = contactRepository.searchContacts(brokerId, search, pageable);
        return contacts.map(this::mapToContactDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContactDTO> getContactsPaginated(Long brokerId, Pageable pageable) {
        Page<Contact> contacts = contactRepository.findByBrokerIdOrderByUserNameAsc(brokerId, pageable);
        return contacts.map(this::mapToContactDTO);
    }
    
    private ContactSectionDTO mapToSectionDTO(ContactSection section) {
        ContactSectionDTO dto = new ContactSectionDTO();
        dto.setId(section.getId());
        dto.setSectionName(section.getSectionName());
        dto.setDescription(section.getDescription());
        dto.setBrokerId(section.getBrokerId());
        dto.setParentSectionId(section.getParentSectionId());
        dto.setCreatedAt(section.getCreatedAt());
        dto.setUpdatedAt(section.getUpdatedAt());
        
        // Get parent section name if exists
        if (section.getParentSectionId() != null) {
            sectionRepository.findById(section.getParentSectionId())
                    .ifPresent(parent -> dto.setParentSectionName(parent.getSectionName()));
        }
        
        return dto;
    }
    
    private ContactDTO mapToContactDTO(Contact contact) {
        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
        dto.setFirmName(contact.getFirmName());
        dto.setUserName(contact.getUserName());
        dto.setGstNumber(contact.getGstNumber());
        dto.setAdditionalInfo(contact.getAdditionalInfo());
        dto.setBrokerId(contact.getBrokerId());
        dto.setCreatedAt(contact.getCreatedAt());
        dto.setUpdatedAt(contact.getUpdatedAt());
        
        // Map section information
        List<ContactSectionMapping> mappings = mappingRepository.findByContactId(contact.getId());
        List<Long> sectionIds = mappings.stream()
                .map(mapping -> mapping.getContactSection().getId())
                .collect(Collectors.toList());
        List<String> sectionNames = mappings.stream()
                .map(mapping -> mapping.getContactSection().getSectionName())
                .collect(Collectors.toList());
        
        dto.setSectionIds(sectionIds);
        dto.setSectionNames(sectionNames);
        
        // Map phone numbers
        List<ContactPhoneDTO> phones = phoneRepository.findByContactId(contact.getId())
                .stream()
                .map(phone -> new ContactPhoneDTO(phone.getId(), phone.getPhoneNumber(), phone.getPhoneType()))
                .collect(Collectors.toList());
        dto.setPhoneNumbers(phones);
        
        // Map addresses
        List<ContactAddressDTO> addresses = addressRepository.findByContactId(contact.getId())
                .stream()
                .map(addr -> new ContactAddressDTO(addr.getId(), addr.getAddress(), 
                        addr.getCity(), addr.getState(), addr.getPincode(), addr.getAddressType()))
                .collect(Collectors.toList());
        dto.setAddresses(addresses);
        
        return dto;
    }
}