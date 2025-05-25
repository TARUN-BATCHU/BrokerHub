package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Long findAddressIdByCityAndArea(String city, String area);

    Address findByCityAndArea(String city, String area);

    Address findByPincode(String pinode);

    boolean existsByCity(String city);

    Optional<Address> findByCityAndAreaAndPincode(String city, String area, String pincode);

    Optional<Address> findByAddressId(Long addressId);
}
