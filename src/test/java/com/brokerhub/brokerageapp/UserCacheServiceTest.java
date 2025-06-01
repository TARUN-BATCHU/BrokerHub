package com.brokerhub.brokerageapp;

import com.brokerhub.brokerageapp.dto.UserBasicInfoDTO;
import com.brokerhub.brokerageapp.repository.UserRepository;
import com.brokerhub.brokerageapp.service.UserCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserCacheServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCacheService userCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUserNames() {
        // Mock data
        List<String> mockFirmNames = Arrays.asList("Firm A", "Firm B", "Firm C");
        when(userRepository.findAllFirmNames()).thenReturn(mockFirmNames);

        // Test
        List<String> result = userCacheService.getAllUserNames();

        // Verify
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Firm A", result.get(0));
        verify(userRepository, times(1)).findAllFirmNames();
    }

    @Test
    void testGetUserNamesAndIds() {
        // Mock data
        Object[] user1 = {1L, "Firm A"};
        Object[] user2 = {2L, "Firm B"};
        List<Object[]> mockData = Arrays.asList(user1, user2);
        when(userRepository.findUserIdsAndFirmNames()).thenReturn(mockData);

        // Test
        List<HashMap<String, Long>> result = userCacheService.getUserNamesAndIds();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).containsKey("Firm A"));
        assertEquals(Long.valueOf(1L), result.get(0).get("Firm A"));
        verify(userRepository, times(1)).findUserIdsAndFirmNames();
    }

    @Test
    void testGetAllBasicUserInfo() {
        // Mock data
        Object[] user1 = {1L, "Firm A", "TRADER", "GST001", "Owner A"};
        Object[] user2 = {2L, "Firm B", "MILLER", "GST002", "Owner B"};
        List<Object[]> mockData = Arrays.asList(user1, user2);
        when(userRepository.findBasicUserInfo()).thenReturn(mockData);

        // Test
        List<UserBasicInfoDTO> result = userCacheService.getAllBasicUserInfo();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Firm A", result.get(0).getFirmName());
        assertEquals("TRADER", result.get(0).getUserType());
        verify(userRepository, times(1)).findBasicUserInfo();
    }
}
