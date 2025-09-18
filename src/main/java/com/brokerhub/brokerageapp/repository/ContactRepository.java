package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    List<Contact> findByBrokerIdOrderByUserNameAsc(Long brokerId);
    
    @Query("SELECT DISTINCT c FROM Contact c JOIN c.sectionMappings sm WHERE sm.contactSection.id = :sectionId AND c.brokerId = :brokerId ORDER BY c.userName ASC")
    List<Contact> findByContactSectionIdAndBrokerIdOrderByUserNameAsc(@Param("sectionId") Long sectionId, @Param("brokerId") Long brokerId);
    
    Optional<Contact> findByIdAndBrokerId(Long id, Long brokerId);
    
    Page<Contact> findByBrokerIdOrderByUserNameAsc(Long brokerId, Pageable pageable);
    
    @Query("SELECT c FROM Contact c WHERE c.brokerId = :brokerId AND " +
           "(LOWER(c.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.firmName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.gstNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Contact> searchContacts(@Param("brokerId") Long brokerId, 
                                @Param("search") String search, 
                                Pageable pageable);
    
    @Query("SELECT COUNT(DISTINCT c) FROM Contact c JOIN c.sectionMappings sm WHERE sm.contactSection.id = :sectionId AND c.brokerId = :brokerId")
    long countByContactSectionIdAndBrokerId(@Param("sectionId") Long sectionId, @Param("brokerId") Long brokerId);
}