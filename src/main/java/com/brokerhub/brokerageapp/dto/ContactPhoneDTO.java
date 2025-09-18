package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactPhoneDTO {
    private Long id;
    private String phoneNumber;
    private String phoneType;
}