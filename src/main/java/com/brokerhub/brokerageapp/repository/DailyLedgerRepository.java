package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;

public interface DailyLedgerRepository extends JpaRepository<DailyLedger, Long> {

    public LedgerDetails findByDate(LocalDate date);
}
