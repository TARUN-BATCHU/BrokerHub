package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.DailyLedgerRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DailyLedgerServiceImplTest {

    @Mock
    private DailyLedgerRepository dailyLedgerRepository;

    @Mock
    private FinancialYearRepository financialYearRepository;

    @InjectMocks
    private DailyLedgerServiceImpl dailyLedgerServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDailyLedger_ValidData() {
        Long financialYearId = 1L;
        LocalDate date = LocalDate.of(2023, 10, 10);
        FinancialYear financialYear = new FinancialYear();
        financialYear.setStart(LocalDate.of(2023, 1, 1));
        financialYear.setEnd(LocalDate.of(2023, 12, 31));

        when(financialYearRepository.findById(financialYearId)).thenReturn(Optional.of(financialYear));
        when(dailyLedgerRepository.findByDate(date)).thenReturn(null);

        Long ledgerId = dailyLedgerServiceImpl.createDailyLedger(financialYearId, date);

        assertNotNull(ledgerId);
        verify(dailyLedgerRepository, times(1)).save(any(DailyLedger.class));
    }

    @Test
    void testCreateDailyLedger_InvalidFinancialYear() {
        Long financialYearId = 1L;
        LocalDate date = LocalDate.of(2023, 10, 10);

        when(financialYearRepository.findById(financialYearId)).thenReturn(Optional.empty());

        Long ledgerId = dailyLedgerServiceImpl.createDailyLedger(financialYearId, date);

        assertNull(ledgerId);
    }

    @Test
    void testGetDailyLedgerId_ExistingLedger() {
        LocalDate date = LocalDate.of(2023, 10, 10);
        DailyLedger dailyLedger = new DailyLedger();
        dailyLedger.setDailyLedgerId(1L);

        when(dailyLedgerRepository.findByDate(date)).thenReturn(dailyLedger);

        Long ledgerId = dailyLedgerServiceImpl.getDailyLedgerId(date);

        assertEquals(1L, ledgerId);
    }

    @Test
    void testGetDailyLedgerId_NonExistingLedger() {
        LocalDate date = LocalDate.of(2023, 10, 10);

        when(dailyLedgerRepository.findByDate(date)).thenReturn(null);

        Long ledgerId = dailyLedgerServiceImpl.getDailyLedgerId(date);

        assertNull(ledgerId);
    }

    @Test
    void testGetDailyLedger_ExistingLedger() {
        LocalDate date = LocalDate.of(2023, 10, 10);
        DailyLedger dailyLedger = new DailyLedger();
        dailyLedger.setDailyLedgerId(1L);

        when(dailyLedgerRepository.findByDate(date)).thenReturn(dailyLedger);
        when(dailyLedgerRepository.findById(1L)).thenReturn(Optional.of(dailyLedger));

        DailyLedger result = dailyLedgerServiceImpl.getDailyLedger(date);

        assertNotNull(result);
    }

    @Test
    void testGetDailyLedger_NonExistingLedger() {
        LocalDate date = LocalDate.of(2023, 10, 10);

        when(dailyLedgerRepository.findByDate(date)).thenReturn(null);

        DailyLedger result = dailyLedgerServiceImpl.getDailyLedger(date);

        assertNull(result);
    }
}
