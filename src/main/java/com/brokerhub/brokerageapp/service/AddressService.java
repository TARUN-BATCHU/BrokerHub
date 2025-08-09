package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BrokersAddress;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface AddressService {

    public boolean isCityExists(String city);

    public Address findAddressByPincode(String pincode);

    public Address saveAddress(Address address);

    public List<Address> getAllAddresses();

    public ResponseEntity createAddress(Address address);

    public ResponseEntity updateAddress(Address address);

    public BrokersAddress findBrokersAddressByPincode(String pincode) throws IOException, InterruptedException;

    public List<Object[]> getCitiesWithAddressId();
}
