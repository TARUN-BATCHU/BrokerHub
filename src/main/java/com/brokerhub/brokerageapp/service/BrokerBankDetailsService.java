package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.BrokerBankDetails;
import org.springframework.http.ResponseEntity;

public interface BrokerBankDetailsService {

    ResponseEntity<String> createBrokerBankDetails(BrokerBankDetails bankDetails);

    BrokerBankDetails getBrokerBankDetailsByAccountNumber(String accountNumber);

    boolean ifBrokerBankDetailsExists(String accountNumber);

    BrokerBankDetails saveBrokerBankDetails(BrokerBankDetails bankDetails);
}
