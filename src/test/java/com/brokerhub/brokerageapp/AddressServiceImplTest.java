package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsCityExists_CityExists() {
        String city = "TestCity";

        when(addressRepository.existsByCity(city)).thenReturn(true);

        boolean result = addressServiceImpl.isCityExists(city);

        assertTrue(result);
    }

    @Test
    void testIsCityExists_CityDoesNotExist() {
        String city = "NonExistentCity";

        when(addressRepository.existsByCity(city)).thenReturn(false);

        boolean result = addressServiceImpl.isCityExists(city);

        assertFalse(result);
    }

    @Test
    void testFindAddressByPincode_AddressExists() {
        String pincode = "123456";
        Address address = new Address();

        when(addressRepository.findByPincode(pincode)).thenReturn(address);

        Address result = addressServiceImpl.findAddressByPincode(pincode);

        assertNotNull(result);
    }

    @Test
    void testFindAddressByPincode_AddressDoesNotExist() {
        String pincode = "654321";

        when(addressRepository.findByPincode(pincode)).thenReturn(null);

        Address result = addressServiceImpl.findAddressByPincode(pincode);

        assertNull(result);
    }
}
