package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
