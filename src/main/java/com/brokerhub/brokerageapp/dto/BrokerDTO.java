package com.brokerhub.brokerageapp.dto;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

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

    public Long getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Long brokerId) {
        this.brokerId = brokerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getIfscCode() {
        return IfscCode;
    }

    public void setIfscCode(String ifscCode) {
        IfscCode = ifscCode;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }
}
