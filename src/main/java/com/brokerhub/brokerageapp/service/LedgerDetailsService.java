package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface LedgerDetailsService {
    ResponseEntity<String> createLedgerDetails(LedgerDetailsDTO ledgerDetailsDTO);

    List<LedgerDetails> getAllLedgerDetails();

    LedgerDetails getLedgerDetailById(Long ledgerDetailId, Long brokerId);

}
