package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
