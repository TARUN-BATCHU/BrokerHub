package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.ContactAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactAddressRepository extends JpaRepository<ContactAddress, Long> {
    List<ContactAddress> findByContactId(Long contactId);
    void deleteByContactId(Long contactId);
}