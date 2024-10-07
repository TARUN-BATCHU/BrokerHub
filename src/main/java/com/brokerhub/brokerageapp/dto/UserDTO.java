package com.brokerhub.brokerageapp.dto;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    @Id
    private Long userId;
    private String userType;
    private String gstNumber;
    private String firmName;
    private String ownerName;
    private String city;
    private String area;
    private String pincode;
    private String email;
    private String BankName;
    private String AccountNumber;
    private String IfscCode;
    private String Branch;
    private List<String> phoneNumbers;
    private Integer brokerageRate;
    private String shopNumber;
    private String byProduct;

}
