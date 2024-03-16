package com.brokerhub.brokerageapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;


@Entity
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brokerId;

    @NotNull
    @NotBlank(message = "please enter the username")
    @Column(unique = true)
    private String userName;

    @NotNull
    @Size(min = 6, max = 20)
    private String password;

    @NotNull
    private String brokerName;

    @NotNull
    private String brokerageFirmName;

    @OneToOne
    private Address address;

    @Email
    private String email;

    @Size(min = 10)
    private String phoneNumber;

    @PositiveOrZero
    private BigDecimal totalBrokerage;

    @OneToOne
    private BankDetails bankDetails;

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }
}
