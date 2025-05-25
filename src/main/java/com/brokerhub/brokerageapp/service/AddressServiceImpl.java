package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Address;
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

    public boolean isCityExists(String city) {
        if(null != city) {
            boolean CityExists = addressRepository.existsByCity(city);
            return CityExists;
        }
        return false;
    }

    public Address findAddressByPincode(String pincode) {
        Address address = addressRepository.findByPincode(pincode);
        return address;
    }

    @Override
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        if(addresses.size()>0){
            return addresses;
        }
        return null;
    }

    @Override
    public ResponseEntity createAddress(Address address) {
        Optional<Address> existingAddress = addressRepository.findByCityAndAreaAndPincode(address.getCity(), address.getArea(), address.getPincode());
        if(existingAddress.isPresent()){
            return ResponseEntity.status(409).body("Address already exists");
        }
        Address address1 = addressRepository.save(address);
        return ResponseEntity.ok().body("Address created successfully with id: "+address1.getAddressId());
    }

    @Override
    public ResponseEntity updateAddress(Address address) {
        Optional<Address> existingAddress = addressRepository.findByAddressId(address.getAddressId());
        if(!existingAddress.isPresent()){
            return ResponseEntity.status(404).body("Address does not exists");
        }
        addressRepository.save(address);
        return ResponseEntity.ok().body("Address updated successfully ");
    }
}
