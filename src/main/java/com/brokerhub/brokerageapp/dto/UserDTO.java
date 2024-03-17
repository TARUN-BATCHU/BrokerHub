package com.brokerhub.brokerageapp.dto;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

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
    private String IsfcCode;
    private String Branch;
    private List<String> phoneNumbers;
    private Integer brokerageRate;
    private String shopNumber;
    private String byProduct;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getIsfcCode() {
        return IsfcCode;
    }

    public void setIsfcCode(String isfcCode) {
        IsfcCode = isfcCode;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
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

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }

    public String getByProduct() {
        return byProduct;
    }

    public void setByProduct(String byProduct) {
        this.byProduct = byProduct;
    }
}
