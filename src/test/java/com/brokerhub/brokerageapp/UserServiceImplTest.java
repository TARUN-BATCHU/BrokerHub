package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.mapper.UserDTOMapper;
import com.brokerhub.brokerageapp.repository.AddressRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserDTOMapper userDTOMapper;

    @Mock
    private BankDetailsService bankDetailsService;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_NewUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirmName("New Firm");
        userDTO.setGstNumber("GST123");

        when(userRepository.findByFirmName("New Firm")).thenReturn(Optional.empty());
        when(userRepository.findByGstNumber("GST123")).thenReturn(Optional.empty());
        when(userDTOMapper.convertUserDTOtoUser(userDTO)).thenReturn(new User());

        ResponseEntity response = userServiceImpl.createUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_ExistingUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirmName("Existing Firm");
        userDTO.setGstNumber("GST123");

        when(userRepository.findByFirmName("Existing Firm")).thenReturn(Optional.of(new User()));

        ResponseEntity response = userServiceImpl.createUser(userDTO);

        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setAddress(new Address());
        user.setBankDetails(new BankDetails());

        when(addressService.findAddressByPincode(anyString())).thenReturn(new Address());
        when(bankDetailsService.getBankDetailsByAccountNumber(anyString())).thenReturn(new BankDetails());

        User updatedUser = userServiceImpl.updateUser(user);

        assertNotNull(updatedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser_ExistingUser() {
        Long userId = 1L;
        User user = new User();
        user.setFirmName("Test Firm");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<String> response = userServiceImpl.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Firm - deleted successfully", response.getBody());
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_NonExistingUser() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = userServiceImpl.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("no user found", response.getBody());
    }
}
