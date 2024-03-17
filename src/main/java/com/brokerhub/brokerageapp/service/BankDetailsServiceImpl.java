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
        System.out.println("bank name given by user is"+bankDetails.getBankName());
        System.out.println("bank account number given by user is"+bankDetails.getAccountNumber());
        if(!ifBankDetailsExists(bankDetails.getAccountNumber())){
            bankDetailsRepository.save(bankDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("bank Details saved and linked to user account");
        }
        else{
            bankDetailsRepository.save(bankDetails);
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("bank details linked to user but same bank previously exists");
        }
    }


//    private boolean ifBankDetailsExists(String accountNumber) {
//        Optional<BankDetails> bankDetailsOptional = Optional.ofNullable(bankDetailsRepository.findByAccountNumber(accountNumber));
//        BankDetails bankDetails = bankDetailsOptional.orElse(null);
//        if (bankDetails == null) {
//            System.out.println("Bank details not found for account number: " + accountNumber);
//            return false;
//        } else {
//            System.out.println("Bank details found: " + bankDetails);
//            return true;
//        }
//    }
    private boolean ifBankDetailsExists(String accountNumber) {
        BankDetails bankDetails = bankDetailsRepository.findByAccountNumber(accountNumber);
        System.out.println("bank details empty: " + bankDetails);
        if(null==bankDetails){
            return false;
        }
        return true;
    }



}
