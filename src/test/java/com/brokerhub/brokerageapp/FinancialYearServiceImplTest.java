package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialYearServiceImplTest {

    @Mock
    private FinancialYearRepository financialYearRepository;
    
    @Mock
    private BrokerRepository brokerRepository;

    @InjectMocks
    private FinancialYearServiceImpl financialYearServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFinancialYear_NewYear() {
        FinancialYear financialYear = new FinancialYear();
        financialYear.setStart(LocalDate.of(2023, 1, 1));
        financialYear.setEnd(LocalDate.of(2023, 12, 31));
        financialYear.setForBills(false);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(mockBroker));
        when(financialYearRepository.findOverlappingYears(any(), any())).thenReturn(new ArrayList<>());
        when(financialYearRepository.findByStartAndEnd(any(), any())).thenReturn(new ArrayList<>());

        ResponseEntity<String> response = financialYearServiceImpl.createFinancialYear(financialYear, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(financialYearRepository, times(1)).save(any(FinancialYear.class));
    }

    @Test
    void testCreateFinancialYear_ExistingYear() {
        FinancialYear financialYear = new FinancialYear();
        financialYear.setStart(LocalDate.of(2023, 1, 1));
        financialYear.setEnd(LocalDate.of(2023, 12, 31));
        financialYear.setForBills(false);

        List<FinancialYear> existingYears = new ArrayList<>();
        existingYears.add(financialYear);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(mockBroker));
        when(financialYearRepository.findOverlappingYears(any(), any())).thenReturn(existingYears);
        when(financialYearRepository.findByStartAndEnd(any(), any())).thenReturn(existingYears);

        ResponseEntity<String> response = financialYearServiceImpl.createFinancialYear(financialYear, 1L);

        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode());
    }

    @Test
    void testGetAllFinancialYearIds() {
        FinancialYear financialYear1 = new FinancialYear();
        financialYear1.setYearId(1L);
        FinancialYear financialYear2 = new FinancialYear();
        financialYear2.setYearId(2L);

        List<FinancialYear> financialYears = new ArrayList<>();
        financialYears.add(financialYear1);
        financialYears.add(financialYear2);

        when(financialYearRepository.findAll()).thenReturn(financialYears);

        List<Long> ids = financialYearServiceImpl.getAllFinancialYearIds();

        assertNotNull(ids);
        assertEquals(2, ids.size());
        assertTrue(ids.contains(1L));
        assertTrue(ids.contains(2L));
    }
}
