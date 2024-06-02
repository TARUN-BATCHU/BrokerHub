package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LedgerDetailsService {
    ResponseEntity<String> createLedgerDetails(LedgerDetailsDTO ledgerDetailsDTO);

    List<LedgerDetailsDTO> getAllLedgerDetails();
}
