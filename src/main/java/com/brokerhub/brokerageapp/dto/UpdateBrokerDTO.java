package com.brokerhub.brokerageapp.dto;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBrokerDTO {

    @Id
    private Long brokerId;
    @NotNull
    private String brokerName;
    @NotNull
    private String brokerageFirmName;
    private String email;
    private String phoneNumber;
    private BigDecimal totalBrokerage;

    private String UserName;

}
