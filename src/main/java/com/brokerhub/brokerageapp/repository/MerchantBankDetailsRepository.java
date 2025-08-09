package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.MerchantBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantBankDetailsRepository extends JpaRepository<MerchantBankDetails, Long> {

    // Multi-tenant aware queries - all include broker filtering
    MerchantBankDetails findByBrokerBrokerIdAndAccountNumber(Long brokerId, String accountNumber);

    List<MerchantBankDetails> findByBrokerBrokerId(Long brokerId);

    boolean existsByBrokerBrokerIdAndAccountNumber(Long brokerId, String accountNumber);

}
