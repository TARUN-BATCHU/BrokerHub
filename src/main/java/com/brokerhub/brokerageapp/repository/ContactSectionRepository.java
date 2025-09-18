package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.ContactSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactSectionRepository extends JpaRepository<ContactSection, Long> {
    List<ContactSection> findByBrokerIdOrderBySectionNameAsc(Long brokerId);
    List<ContactSection> findByBrokerIdAndParentSectionIdOrderBySectionNameAsc(Long brokerId, Long parentSectionId);
    List<ContactSection> findByBrokerIdAndParentSectionIdIsNullOrderBySectionNameAsc(Long brokerId);
    Optional<ContactSection> findByIdAndBrokerId(Long id, Long brokerId);
    boolean existsBySectionNameAndBrokerIdAndParentSectionId(String sectionName, Long brokerId, Long parentSectionId);
}