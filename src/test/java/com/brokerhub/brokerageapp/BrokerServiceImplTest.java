package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerDTO;
import com.brokerhub.brokerageapp.dto.UpdateBrokerDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.mapper.BrokerDTOMapper;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import com.brokerhub.brokerageapp.utils.OtpUtil;
import com.brokerhub.brokerageapp.utils.EmailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerServiceImplTest {

    @Mock
    private BrokerRepository brokerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private BrokerBankDetailsService brokerBankDetailsService;

    @Mock
    private AddressService addressService;

    @Mock
    private BrokerDTOMapper brokerDTOMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OtpUtil otpUtil;

    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private BrokerServiceImpl brokerServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBroker_NewBroker() {
        BrokerDTO brokerDTO = new BrokerDTO();
        brokerDTO.setBrokerageFirmName("New Firm");
        brokerDTO.setEmail("new@firm.com");
        brokerDTO.setPhoneNumber("1234567890");
        brokerDTO.setPincode("123456");

        when(brokerRepository.findByBrokerageFirmName(anyString())).thenReturn(Optional.empty());
        when(brokerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(brokerRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(brokerDTOMapper.convertBrokerDTOtoBroker(brokerDTO)).thenReturn(new Broker());
        when(addressService.findAddressByPincode("123456")).thenReturn(null);

        ResponseEntity response = brokerServiceImpl.createBroker(brokerDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(brokerRepository, times(1)).save(any(Broker.class));
    }

    @Test
    void testCreateBroker_ExistingBroker() {
        BrokerDTO brokerDTO = new BrokerDTO();
        brokerDTO.setBrokerageFirmName("Existing Firm");
        brokerDTO.setEmail("existing@firm.com");
        brokerDTO.setPhoneNumber("0987654321");

        when(brokerRepository.findByBrokerageFirmName(anyString())).thenReturn(Optional.of(new Broker()));

        ResponseEntity response = brokerServiceImpl.createBroker(brokerDTO);

        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode());
        verify(brokerRepository, never()).save(any(Broker.class));
    }

    @Test
    void testUpdateBroker_ExistingBroker() {
        UpdateBrokerDTO updateBrokerDTO = new UpdateBrokerDTO();
        updateBrokerDTO.setBrokerId(1L);
        updateBrokerDTO.setBrokerName("Updated Name");

        Broker broker = new Broker();
        broker.setBrokerId(1L);

        when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));

        ResponseEntity response = brokerServiceImpl.updateBroker(updateBrokerDTO);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(brokerRepository, times(1)).save(broker);
    }

    @Test
    void testUpdateBroker_NonExistingBroker() {
        UpdateBrokerDTO updateBrokerDTO = new UpdateBrokerDTO();
        updateBrokerDTO.setBrokerId(1L);

        when(brokerRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity response = brokerServiceImpl.updateBroker(updateBrokerDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteBroker_ExistingBroker() {
        Long brokerId = 1L;

        when(brokerRepository.findById(brokerId)).thenReturn(Optional.of(new Broker()));

        ResponseEntity response = brokerServiceImpl.deleteBroker(brokerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(brokerRepository, times(1)).deleteById(brokerId);
    }

    @Test
    void testDeleteBroker_NonExistingBroker() {
        Long brokerId = 1L;

        when(brokerRepository.findById(brokerId)).thenReturn(Optional.empty());

        ResponseEntity response = brokerServiceImpl.deleteBroker(brokerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
