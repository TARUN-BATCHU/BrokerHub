package com.brokerhub.brokerageapp.repository;


import com.brokerhub.brokerageapp.entity.BrokerBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrokerBankDetailsRepository extends JpaRepository<BrokerBankDetails, Long> {

    // Multi-tenant aware queries - all include broker filtering
    BrokerBankDetails findByBrokerBrokerIdAndAccountNumber(Long brokerId, String accountNumber);

    List<BrokerBankDetails> findByBrokerBrokerId(Long brokerId);

    boolean existsByBrokerBrokerIdAndAccountNumber(Long brokerId, String accountNumber);

}
