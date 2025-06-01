package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.BankDetails;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.repository.BankDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankDetailsServiceImpl implements BankDetailsService{

    @Autowired
    BankDetailsRepository bankDetailsRepository;

    @Autowired
    TenantContextService tenantContextService;

    public ResponseEntity<String> createBankDetails(BankDetails bankDetails) {
        // Get current broker for multi-tenant isolation
        Broker currentBroker = tenantContextService.getCurrentBroker();

        // Set the broker for multi-tenant isolation if not already set
        if (bankDetails.getBroker() == null) {
            bankDetails.setBroker(currentBroker);
        }

        if(!ifBankDetailsExists(bankDetails.getAccountNumber())){
            bankDetailsRepository.save(bankDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("bank Details saved and linked to user account");
        }
        else{
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("bank details linked to user but same bank previously exists for this broker");
        }
    }

    @Override
    public BankDetails getBankDetailsByAccountNumber(String accountNumber) {
        if(ifBankDetailsExists(accountNumber)) {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            BankDetails bankDetails = bankDetailsRepository.findByBrokerBrokerIdAndAccountNumber(currentBrokerId, accountNumber);
            return bankDetails;
        }
        return null;
    }

    public boolean ifBankDetailsExists(String accountNumber) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        boolean exists = bankDetailsRepository.existsByBrokerBrokerIdAndAccountNumber(currentBrokerId, accountNumber);
        System.out.println("bank details exists for broker " + currentBrokerId + ": " + exists);
        return exists;
    }

    @Override
    public BankDetails saveBankDetails(BankDetails bankDetails) {
        // Set the broker for multi-tenant isolation if not already set
        if (bankDetails.getBroker() == null) {
            Broker currentBroker = tenantContextService.getCurrentBroker();
            bankDetails.setBroker(currentBroker);
        }
        return bankDetailsRepository.save(bankDetails);
    }
}
