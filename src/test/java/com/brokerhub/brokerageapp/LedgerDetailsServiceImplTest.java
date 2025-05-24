package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.LedgerDetailsRepository;
import com.brokerhub.brokerageapp.repository.LedgerRecordRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LedgerDetailsServiceImplTest {

    @Mock
    private LedgerDetailsRepository ledgerDetailsRepository;

    @Mock
    private LedgerRecordRepository ledgerRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DailyLedgerService dailyLedgerService;

    @Mock
    private BrokerRepository brokerRepository;

    @InjectMocks
    private LedgerDetailsServiceImpl ledgerDetailsServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLedgerDetails() {
        LedgerDetailsDTO ledgerDetailsDTO = new LedgerDetailsDTO();
        ledgerDetailsDTO.setDate(LocalDate.now());

        when(dailyLedgerService.getDailyLedger(any())).thenReturn(null);

        ResponseEntity<String> response = ledgerDetailsServiceImpl.createLedgerDetails(ledgerDetailsDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testGetAllLedgerDetails() {
        List<LedgerDetails> ledgerDetailsList = new ArrayList<>();
        ledgerDetailsList.add(new LedgerDetails());

        when(ledgerDetailsRepository.findAll()).thenReturn(ledgerDetailsList);

        List<LedgerDetails> result = ledgerDetailsServiceImpl.getAllLedgerDetails();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetLedgerDetailById_Existing() {
        LedgerDetails ledgerDetails = new LedgerDetails();

        when(ledgerDetailsRepository.findById(anyLong())).thenReturn(java.util.Optional.of(ledgerDetails));

        LedgerDetails result = ledgerDetailsServiceImpl.getLedgerDetailById(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void testGetLedgerDetailById_NonExisting() {
        when(ledgerDetailsRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        LedgerDetails result = ledgerDetailsServiceImpl.getLedgerDetailById(1L, 1L);

        assertNull(result);
    }
}
