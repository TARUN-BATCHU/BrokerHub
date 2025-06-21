package com.brokerhub.brokerageapp.service;


import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.MerchantBankDetails;
import com.brokerhub.brokerageapp.repository.BrokerBankDetailsRepository;
import com.brokerhub.brokerageapp.repository.MerchantBankDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MerchantBankDetailsServiceImpl implements MerchantBankDetailsService {

    @Autowired
    MerchantBankDetailsRepository merchantBankDetailsRepository;

    @Autowired
    TenantContextService tenantContextService;

    public ResponseEntity<String> createMerchantBankDetails(MerchantBankDetails bankDetails) {
        // Get current broker for multi-tenant isolation
        Broker currentBroker = tenantContextService.getCurrentBroker();

        // Set the broker for multi-tenant isolation if not already set
        if (bankDetails.getBroker() == null) {
            bankDetails.setBroker(currentBroker);
        }

        if(!ifMerchantBankDetailsExists(bankDetails.getAccountNumber())){
            merchantBankDetailsRepository.save(bankDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("bank Details saved and linked to user account");
        }
        else{
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("bank details linked to user but same bank previously exists for this broker");
        }
    }


    @Override
    public MerchantBankDetails getMerchantBankDetailsByAccountNumber(String accountNumber) {
        if(ifMerchantBankDetailsExists(accountNumber)) {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            MerchantBankDetails bankDetails = merchantBankDetailsRepository.findByBrokerBrokerIdAndAccountNumber(currentBrokerId, accountNumber);
            return bankDetails;
        }
        return null;
    }

    public boolean ifMerchantBankDetailsExists(String accountNumber) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        boolean exists = merchantBankDetailsRepository.existsByBrokerBrokerIdAndAccountNumber(currentBrokerId, accountNumber);
        System.out.println("bank details exists for broker " + currentBrokerId + ": " + exists);
        return exists;
    }

    @Override
    public MerchantBankDetails saveMerchantBankDetails(MerchantBankDetails bankDetails) {
        // Set the broker for multi-tenant isolation if not already set
        if (bankDetails.getBroker() == null) {
            Broker currentBroker = tenantContextService.getCurrentBroker();
            bankDetails.setBroker(currentBroker);
        }
        return merchantBankDetailsRepository.save(bankDetails);
    }
}
