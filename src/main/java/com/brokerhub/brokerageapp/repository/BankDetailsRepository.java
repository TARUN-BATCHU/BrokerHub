package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankDetailsRepository extends JpaRepository<BankDetails , Long> {

    BankDetails findByAccountNumber(String AccountNumber);
}
