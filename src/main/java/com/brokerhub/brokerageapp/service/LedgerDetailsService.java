package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.DisplayLedgerDetailDTO;
import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.dto.NewLedgerRequestDTO;
import com.brokerhub.brokerageapp.dto.OptimizedLedgerDetailsDTO;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface LedgerDetailsService {
    ResponseEntity<Long> createLedgerDetails(LedgerDetailsDTO ledgerDetailsDTO);
    
    ResponseEntity<Long> createLedgerDetailsFromNames(NewLedgerRequestDTO newLedgerRequestDTO);
    
    Long getNextTransactionNumber(Long financialYearId);

    List<LedgerDetails> getAllLedgerDetails();

    LedgerDetails getLedgerDetailById(Long ledgerDetailId, Long brokerId);

    LedgerDetails getLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId, Long financialYearId);

    OptimizedLedgerDetailsDTO getOptimizedLedgerDetailById(Long ledgerDetailId, Long brokerId);

    OptimizedLedgerDetailsDTO getOptimizedLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId, Long financialYearId);

    List<DisplayLedgerDetailDTO> getAllLedgerDetailsOnDate(LocalDate date, Long brokerId, Long financialYearId);

    List<LedgerDetailsDTO> getAllLedgerDetailsBySeller(Long sellerId, Long brokerId);

    ResponseEntity<String> updateLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId, Long financialYearId, LedgerDetailsDTO ledgerDetailsDTO);
}
