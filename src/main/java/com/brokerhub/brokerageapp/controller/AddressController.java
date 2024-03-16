package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/Address")
public class AddressController {

    @Autowired
    AddressService addressService;


}
