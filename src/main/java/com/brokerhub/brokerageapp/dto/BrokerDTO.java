package com.brokerhub.brokerageapp.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrokerDTO {

    @Id
    private Long brokerId;

    private String userName;

    private String password;

    private String brokerName;

    private String brokerageFirmName;

    private String pincode;

    private String email;

    private String phoneNumber;

    private String accountNumber;

    private String ifscCode;

}
