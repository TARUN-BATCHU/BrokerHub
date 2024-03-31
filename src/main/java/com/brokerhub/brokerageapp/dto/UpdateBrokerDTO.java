package com.brokerhub.brokerageapp.dto;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
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

    public Long getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Long brokerId) {
        this.brokerId = brokerId;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerageFirmName() {
        return brokerageFirmName;
    }

    public void setBrokerageFirmName(String brokerageFirmName) {
        this.brokerageFirmName = brokerageFirmName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getTotalBrokerage() {
        return totalBrokerage;
    }

    public void setTotalBrokerage(BigDecimal totalBrokerage) {
        this.totalBrokerage = totalBrokerage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
