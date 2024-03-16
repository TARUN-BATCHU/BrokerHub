package com.brokerhub.brokerageapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_type", insertable = false, updatable = false)
    private String userType;

    private String gstNumber;

    @NotNull
    private String firmName;

    private String ownerName;


    @OneToOne
    @NotNull
    private Address address;

    @Email
    private String email;

    @OneToOne
    @PrimaryKeyJoinColumn
    private BankDetails bankDetails;

    private List<String> phoneNumbers;

    @PositiveOrZero
    private Integer brokerageRate;

    @PositiveOrZero
    private Long totalBagsSold;

    @PositiveOrZero
    private Long totalBagsBought;

    @PositiveOrZero
    private Long payableAmount;

    @PositiveOrZero
    private Long receivableAmount;

    private Long totalPayableBrokerage;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Integer getBrokerageRate() {
        return brokerageRate;
    }

    public void setBrokerageRate(Integer brokerageRate) {
        this.brokerageRate = brokerageRate;
    }

    public Long getTotalBagsSold() {
        return totalBagsSold;
    }

    public void setTotalBagsSold(Long totalBagsSold) {
        this.totalBagsSold = totalBagsSold;
    }

    public Long getTotalBagsBought() {
        return totalBagsBought;
    }

    public void setTotalBagsBought(Long totalBagsBought) {
        this.totalBagsBought = totalBagsBought;
    }

    public Long getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Long payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Long getReceivableAmount() {
        return receivableAmount;
    }

    public void setReceivableAmount(Long receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    public Long getTotalPayableBrokerage() {
        return totalPayableBrokerage;
    }

    public void setTotalPayableBrokerage(Long totalPayableBrokerage) {
        this.totalPayableBrokerage = totalPayableBrokerage;
    }
}
