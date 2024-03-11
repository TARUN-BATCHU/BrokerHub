package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;

@Entity
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BankDetailsId;

    private String BankName;

    private String AccountNumber;

    private String IsfcCode;

    private String Branch;

    public Long getBankDetailsId() {
        return BankDetailsId;
    }

    public void setBankDetailsId(Long bankDetailsId) {
        BankDetailsId = bankDetailsId;
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
}
