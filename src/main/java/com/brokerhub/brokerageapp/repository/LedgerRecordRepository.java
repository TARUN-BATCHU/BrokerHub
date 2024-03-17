package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.LedgerRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRecordRepository extends JpaRepository<LedgerRecord, Long> {
}
