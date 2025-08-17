package com.brokerhub.brokerageapp.service;


import com.brokerhub.brokerageapp.entity.MerchantBankDetails;
import org.springframework.http.ResponseEntity;

public interface MerchantBankDetailsService {

    ResponseEntity<String> createMerchantBankDetails(MerchantBankDetails bankDetails);

    MerchantBankDetails getMerchantBankDetailsByAccountNumber(String accountNumber);

    boolean ifMerchantBankDetailsExists(String accountNumber);

    MerchantBankDetails saveMerchantBankDetails(MerchantBankDetails bankDetails);
}
