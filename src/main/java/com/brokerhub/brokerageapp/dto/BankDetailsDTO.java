package com.brokerhub.brokerageapp.dto;

import lombok.Data;

@Data
public class BankDetailsDTO {

    private String ifscCode;
    private String bankName;
    private String branch;
    private String bankContact;
    private String bankAddress;
    private String bankCode;
    private String MICR;
    private Boolean RTGS;
    private Boolean IMPS;
    private Boolean UPI;
    private Boolean NEFT;
}

