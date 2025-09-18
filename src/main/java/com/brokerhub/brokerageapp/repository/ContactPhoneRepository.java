package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.ContactPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactPhoneRepository extends JpaRepository<ContactPhone, Long> {
    List<ContactPhone> findByContactId(Long contactId);
    void deleteByContactId(Long contactId);
}