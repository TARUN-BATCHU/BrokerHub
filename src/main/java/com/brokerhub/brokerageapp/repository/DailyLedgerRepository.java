package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLedgerRepository extends JpaRepository<DailyLedger, Long> {
}
