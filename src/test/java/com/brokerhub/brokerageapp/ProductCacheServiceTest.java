package com.brokerhub.brokerageapp;

import com.brokerhub.brokerageapp.dto.ProductBasicInfoDTO;
import com.brokerhub.brokerageapp.repository.ProductRepository;
import com.brokerhub.brokerageapp.service.ProductCacheService;
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
class ProductCacheServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductCacheService productCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProductNames() {
        // Mock data
        List<String> mockProductNames = Arrays.asList("Wheat", "Rice", "Corn");
        when(productRepository.findAllProductNames()).thenReturn(mockProductNames);

        // Test
        List<String> result = productCacheService.getAllProductNames();

        // Verify
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Wheat", result.get(0));
        verify(productRepository, times(1)).findAllProductNames();
    }

    @Test
    void testGetDistinctProductNames() {
        // Mock data
        List<String> mockDistinctNames = Arrays.asList("Wheat", "Rice");
        when(productRepository.findDistinctProductNames()).thenReturn(mockDistinctNames);

        // Test
        List<String> result = productCacheService.getDistinctProductNames();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Wheat"));
        verify(productRepository, times(1)).findDistinctProductNames();
    }

    @Test
    void testGetProductNamesAndIds() {
        // Mock data
        Object[] product1 = {1L, "Wheat"};
        Object[] product2 = {2L, "Rice"};
        List<Object[]> mockData = Arrays.asList(product1, product2);
        when(productRepository.findProductIdsAndNames()).thenReturn(mockData);

        // Test
        List<HashMap<String, Long>> result = productCacheService.getProductNamesAndIds();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).containsKey("Wheat"));
        assertEquals(Long.valueOf(1L), result.get(0).get("Wheat"));
        verify(productRepository, times(1)).findProductIdsAndNames();
    }

    @Test
    void testGetAllBasicProductInfo() {
        // Mock data
        Object[] product1 = {1L, "Wheat", 5.0f, "Premium", 2500};
        Object[] product2 = {2L, "Rice", 3.5f, "Standard", 3000};
        List<Object[]> mockData = Arrays.asList(product1, product2);
        when(productRepository.findBasicProductInfo()).thenReturn(mockData);

        // Test
        List<ProductBasicInfoDTO> result = productCacheService.getAllBasicProductInfo();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Wheat", result.get(0).getProductName());
        assertEquals(Float.valueOf(5.0f), result.get(0).getProductBrokerage());
        assertEquals("Premium", result.get(0).getQuality());
        verify(productRepository, times(1)).findBasicProductInfo();
    }

    @Test
    void testGetProductNamesAndQualities() {
        // Mock data
        Object[] product1 = {"Wheat", "Premium"};
        Object[] product2 = {"Rice", "Standard"};
        List<Object[]> mockData = Arrays.asList(product1, product2);
        when(productRepository.findProductNamesAndQualities()).thenReturn(mockData);

        // Test
        List<ProductBasicInfoDTO> result = productCacheService.getProductNamesAndQualities();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Wheat", result.get(0).getProductName());
        assertEquals("Premium", result.get(0).getQuality());
        assertEquals("Rice", result.get(1).getProductName());
        assertEquals("Standard", result.get(1).getQuality());
        verify(productRepository, times(1)).findProductNamesAndQualities();
    }
}
