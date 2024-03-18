package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.BankDetails;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface BankDetailsService {

    ResponseEntity<String> createBankDetails(BankDetails bankDetails);

    BankDetails getBankDetailsByAccountNumber(String accountNumber);

    boolean ifBankDetailsExists(String accountNumber);
}
