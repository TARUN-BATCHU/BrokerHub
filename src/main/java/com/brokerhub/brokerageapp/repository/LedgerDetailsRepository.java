package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.LedgerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerDetailsRepository extends JpaRepository<LedgerDetails, Long> {
}
