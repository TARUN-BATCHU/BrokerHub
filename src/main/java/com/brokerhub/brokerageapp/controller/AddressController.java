package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BrokerHub/Address")
public class AddressController {

    @Autowired
    AddressService addressService;


    @GetMapping("/getAllAddresses")
    public List<Address> getAllAddresses(){
        return addressService.getAllAddresses();
    }

    @PostMapping("/createAddress")
    public ResponseEntity createAddress(@RequestBody Address address){
        if(address.getCity() == null || address.getArea() == null){
            return new ResponseEntity("City and area are mandatory", HttpStatus.BAD_REQUEST);
        }
        return addressService.createAddress(address);
    }

    @PutMapping("/updateAddress")
    public ResponseEntity updateAddress(@RequestBody Address address){
        if(address.getCity() == null || address.getArea() == null){
            return new ResponseEntity("City and area are mandatory", HttpStatus.BAD_REQUEST);
        }
        return addressService.updateAddress(address);
    }


}
