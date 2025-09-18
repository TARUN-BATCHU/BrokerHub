package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.ContactSectionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactSectionMappingRepository extends JpaRepository<ContactSectionMapping, Long> {
    
    List<ContactSectionMapping> findByContactId(Long contactId);
    
    List<ContactSectionMapping> findByContactSectionId(Long sectionId);
    
    void deleteByContactId(Long contactId);
    
    void deleteByContactIdAndContactSectionId(Long contactId, Long sectionId);
    
    @Query("SELECT csm FROM ContactSectionMapping csm WHERE csm.contactSection.id IN :sectionIds")
    List<ContactSectionMapping> findBySectionIds(@Param("sectionIds") List<Long> sectionIds);
    
    boolean existsByContactIdAndContactSectionId(Long contactId, Long sectionId);
}