package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactAddressDTO {
    private Long id;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String addressType;
}