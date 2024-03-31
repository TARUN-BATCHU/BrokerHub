package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Address;

public interface AddressService {

    public boolean isCityExists(String city);

    public Address findAddressByPincode(String pincode);
}
