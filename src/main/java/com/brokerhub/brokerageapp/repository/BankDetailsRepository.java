package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BankDetailsRepository extends JpaRepository<BankDetails , Long> {

    // Multi-tenant aware queries - all include broker filtering
    BankDetails findByBrokerBrokerIdAndAccountNumber(Long brokerId, String accountNumber);

    List<BankDetails> findByBrokerBrokerId(Long brokerId);

    boolean existsByBrokerBrokerIdAndAccountNumber(Long brokerId, String accountNumber);

    Optional<BankDetails> findByBrokerBrokerIdAndBankDetailsId(Long brokerId, Long bankDetailsId);

    // Legacy methods (deprecated - use broker-aware versions)
    @Deprecated
    BankDetails findByAccountNumber(String AccountNumber);
}
