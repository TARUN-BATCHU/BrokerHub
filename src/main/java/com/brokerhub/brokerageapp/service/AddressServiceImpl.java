package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    AddressRepository addressRepository;

    public boolean isCityExists(String city) {
        boolean CityExists = addressRepository.existsByCity(city);
        return CityExists;
    }
}
