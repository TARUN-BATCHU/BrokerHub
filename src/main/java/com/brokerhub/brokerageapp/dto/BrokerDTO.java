package com.brokerhub.brokerageapp.dto;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
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

    private String BankName;
    private String AccountNumber;
    private String IfscCode;
    private String Branch;

}
