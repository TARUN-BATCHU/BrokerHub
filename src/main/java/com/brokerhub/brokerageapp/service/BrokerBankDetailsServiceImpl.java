package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.BrokerBankDetails;
import com.brokerhub.brokerageapp.repository.BrokerBankDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BrokerBankDetailsServiceImpl implements BrokerBankDetailsService {

    @Autowired
    BrokerBankDetailsRepository brokerBankDetailsRepository;

    @Autowired
    TenantContextService tenantContextService;

    public ResponseEntity<String> createBrokerBankDetails(BrokerBankDetails bankDetails) {
        if(!ifBrokerBankDetailsExists(bankDetails.getAccountNumber())){
            brokerBankDetailsRepository.save(bankDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("bank Details saved and linked to user account");
        }
        else{
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("bank details linked to user but same bank previously exists for this broker");
        }
    }

    @Override
    public BrokerBankDetails getBrokerBankDetailsByAccountNumber(String accountNumber) {
        if(ifBrokerBankDetailsExists(accountNumber)) {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            BrokerBankDetails bankDetails = brokerBankDetailsRepository.findByBrokerBrokerIdAndAccountNumber(currentBrokerId, accountNumber);
            return bankDetails;
        }
        return null;
    }

    public boolean ifBrokerBankDetailsExists(String accountNumber) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        boolean exists = brokerBankDetailsRepository.existsByBrokerBrokerIdAndAccountNumber(currentBrokerId, accountNumber);
        System.out.println("bank details exists for broker " + currentBrokerId + ": " + exists);
        return exists;
    }

    @Override
    public BrokerBankDetails saveBrokerBankDetails(BrokerBankDetails bankDetails) {
        // Set the broker for multi-tenant isolation if not already set
        if (bankDetails.getBroker() == null) {
            Broker currentBroker = tenantContextService.getCurrentBroker();
            bankDetails.setBroker(currentBroker);
        }
        return brokerBankDetailsRepository.save(bankDetails);
    }
}
