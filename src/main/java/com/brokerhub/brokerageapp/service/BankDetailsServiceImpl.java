package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.BankDetails;
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

    public ResponseEntity<String> createBankDetails(BankDetails bankDetails) {
        if(!ifBankDetailsExists(bankDetails.getAccountNumber())){
            bankDetailsRepository.save(bankDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("bank Details saved and linked to user account");
        }
        else{
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("bank details linked to user but same bank previously exists");
        }
    }

    @Override
    public BankDetails getBankDetailsByAccountNumber(String accountNumber) {
        if(ifBankDetailsExists(accountNumber)) {
            BankDetails bankDetails = bankDetailsRepository.findByAccountNumber(accountNumber);
            return bankDetails;
        }
        return null;
    }

    public boolean ifBankDetailsExists(String accountNumber) {
        BankDetails bankDetails = bankDetailsRepository.findByAccountNumber(accountNumber);
        System.out.println("bank details empty: " + bankDetails);
        if(null==bankDetails){
            return false;
        }
        return true;
    }

    @Override
    public BankDetails saveBankDetails(BankDetails bankDetails) {
        return bankDetailsRepository.save(bankDetails);
    }
}
