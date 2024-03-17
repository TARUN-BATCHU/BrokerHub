package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;

@Entity
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankDetailsId;

    private String bankName;

    private String accountNumber;

    private String isfcCode;

    private String branch;

    public Long getBankDetailsId() {
        return bankDetailsId;
    }

    public void setBankDetailsId(Long bankDetailsId) {
        this.bankDetailsId = bankDetailsId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIsfcCode() {
        return isfcCode;
    }

    public void setIsfcCode(String isfcCode) {
        this.isfcCode = isfcCode;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
