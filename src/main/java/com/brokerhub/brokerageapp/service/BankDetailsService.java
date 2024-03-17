package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.BankDetails;
import org.springframework.http.ResponseEntity;

public interface BankDetailsService {

    ResponseEntity<String> createBankDetails(BankDetails bankDetails);
}
