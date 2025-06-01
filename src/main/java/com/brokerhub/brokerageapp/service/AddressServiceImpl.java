package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    TenantContextService tenantContextService;

    public boolean isCityExists(String city) {
        if(null != city) {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            boolean CityExists = addressRepository.existsByBrokerBrokerIdAndCity(currentBrokerId, city);
            return CityExists;
        }
        return false;
    }

    public Address findAddressByPincode(String pincode) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Address address = addressRepository.findByBrokerBrokerIdAndPincode(currentBrokerId, pincode);
        return address;
    }

    @Override
    public Address saveAddress(Address address) {
        // Set the broker for multi-tenant isolation if not already set
        if (address.getBroker() == null) {
            Broker currentBroker = tenantContextService.getCurrentBroker();
            address.setBroker(currentBroker);
        }
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAllAddresses() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        List<Address> addresses = addressRepository.findByBrokerBrokerId(currentBrokerId);
        if(addresses.size()>0){
            return addresses;
        }
        return null;
    }

    @Override
    public ResponseEntity createAddress(Address address) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Broker currentBroker = tenantContextService.getCurrentBroker();

        // Check if address already exists for this broker
        Optional<Address> existingAddress = addressRepository.findByBrokerBrokerIdAndCityAndAreaAndPincode(
            currentBrokerId, address.getCity(), address.getArea(), address.getPincode());
        if(existingAddress.isPresent()){
            return ResponseEntity.status(409).body("Address already exists for this broker");
        }

        // Set the broker for multi-tenant isolation
        address.setBroker(currentBroker);

        Address address1 = addressRepository.save(address);
        return ResponseEntity.ok().body("Address created successfully with id: "+address1.getAddressId());
    }

    @Override
    public ResponseEntity updateAddress(Address address) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();

        // Check if address exists and belongs to current broker
        Optional<Address> existingAddress = addressRepository.findByBrokerBrokerIdAndAddressId(currentBrokerId, address.getAddressId());
        if(!existingAddress.isPresent()){
            return ResponseEntity.status(404).body("Address does not exist or does not belong to current broker");
        }

        // Ensure the broker is set correctly
        address.setBroker(tenantContextService.getCurrentBroker());

        addressRepository.save(address);
        return ResponseEntity.ok().body("Address updated successfully");
    }
}
