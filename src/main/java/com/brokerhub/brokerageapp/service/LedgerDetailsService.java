package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.DisplayLedgerDetailDTO;
import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.dto.OptimizedLedgerDetailsDTO;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface LedgerDetailsService {
    ResponseEntity<String> createLedgerDetails(LedgerDetailsDTO ledgerDetailsDTO);

    List<LedgerDetails> getAllLedgerDetails();

    LedgerDetails getLedgerDetailById(Long ledgerDetailId, Long brokerId);

    LedgerDetails getLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId);

    OptimizedLedgerDetailsDTO getOptimizedLedgerDetailById(Long ledgerDetailId, Long brokerId);

    OptimizedLedgerDetailsDTO getOptimizedLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId);

    List<DisplayLedgerDetailDTO> getAllLedgerDetailsOnDate(LocalDate date, Long brokerId);

    List<LedgerDetailsDTO> getAllLedgerDetailsBySeller(Long sellerId, Long brokerId);
}
