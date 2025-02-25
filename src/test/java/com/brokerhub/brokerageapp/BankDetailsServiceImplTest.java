package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.BankDetails;
import com.brokerhub.brokerageapp.repository.BankDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankDetailsServiceImplTest {

    @Mock
    private BankDetailsRepository bankDetailsRepository;

    @InjectMocks
    private BankDetailsServiceImpl bankDetailsServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBankDetails_NewBankDetails() {
        BankDetails bankDetails = new BankDetails();
        bankDetails.setAccountNumber("123456789");

        when(bankDetailsRepository.findByAccountNumber("123456789")).thenReturn(null);

        ResponseEntity<String> response = bankDetailsServiceImpl.createBankDetails(bankDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("bank Details saved and linked to user account", response.getBody());
        verify(bankDetailsRepository, times(1)).save(bankDetails);
    }

    @Test
    void testCreateBankDetails_ExistingBankDetails() {
        BankDetails bankDetails = new BankDetails();
        bankDetails.setAccountNumber("123456789");

        when(bankDetailsRepository.findByAccountNumber("123456789")).thenReturn(bankDetails);

        ResponseEntity<String> response = bankDetailsServiceImpl.createBankDetails(bankDetails);

        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode());
        assertEquals("bank details linked to user but same bank previously exists", response.getBody());
        verify(bankDetailsRepository, never()).save(bankDetails);
    }

    @Test
    void testGetBankDetailsByAccountNumber_ExistingAccount() {
        BankDetails bankDetails = new BankDetails();
        bankDetails.setAccountNumber("123456789");

        when(bankDetailsRepository.findByAccountNumber("123456789")).thenReturn(bankDetails);

        BankDetails result = bankDetailsServiceImpl.getBankDetailsByAccountNumber("123456789");

        assertNotNull(result);
        assertEquals("123456789", result.getAccountNumber());
    }

    @Test
    void testGetBankDetailsByAccountNumber_NonExistingAccount() {
        when(bankDetailsRepository.findByAccountNumber("123456789")).thenReturn(null);

        BankDetails result = bankDetailsServiceImpl.getBankDetailsByAccountNumber("123456789");

        assertNull(result);
    }

    @Test
    void testIfBankDetailsExists_True() {
        BankDetails bankDetails = new BankDetails();
        bankDetails.setAccountNumber("123456789");

        when(bankDetailsRepository.findByAccountNumber("123456789")).thenReturn(bankDetails);

        boolean exists = bankDetailsServiceImpl.ifBankDetailsExists("123456789");

        assertTrue(exists);
    }

    @Test
    void testIfBankDetailsExists_False() {
        when(bankDetailsRepository.findByAccountNumber("123456789")).thenReturn(null);

        boolean exists = bankDetailsServiceImpl.ifBankDetailsExists("123456789");

        assertFalse(exists);
    }
}
