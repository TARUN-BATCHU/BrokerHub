package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BrokersAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokersAddressRepository extends JpaRepository<BrokersAddress, Long>  {

    BrokersAddress findByPincode(String pincode);
}
