package com.brokerhub.brokerageapp;

import com.brokerhub.brokerageapp.dto.analytics.FinancialYearAnalyticsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JacksonLocalDateTest {

    @Test
    public void testLocalDateSerialization() throws Exception {
        // Create ObjectMapper with JSR310 module
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Create test DTO with LocalDate fields
        FinancialYearAnalyticsDTO dto = FinancialYearAnalyticsDTO.builder()
                .financialYearId(1L)
                .financialYearName("2024-25")
                .startDate(LocalDate.of(2024, 4, 1))
                .endDate(LocalDate.of(2025, 3, 31))
                .totalBrokerage(BigDecimal.valueOf(1000.50))
                .totalQuantity(100L)
                .totalTransactionValue(BigDecimal.valueOf(50000.75))
                .totalTransactions(25)
                .monthlyAnalytics(new ArrayList<>())
                .overallProductTotals(new ArrayList<>())
                .overallCityTotals(new ArrayList<>())
                .overallMerchantTypeTotals(new ArrayList<>())
                .build();

        // Test serialization
        String json = objectMapper.writeValueAsString(dto);
        assertNotNull(json);
        assertTrue(json.contains("2024-04-01"));
        assertTrue(json.contains("2025-03-31"));
        
        // Test deserialization
        FinancialYearAnalyticsDTO deserializedDto = objectMapper.readValue(json, FinancialYearAnalyticsDTO.class);
        assertNotNull(deserializedDto);
        assertEquals(LocalDate.of(2024, 4, 1), deserializedDto.getStartDate());
        assertEquals(LocalDate.of(2025, 3, 31), deserializedDto.getEndDate());
        
        System.out.println("✅ LocalDate serialization test passed!");
        System.out.println("JSON: " + json);
    }
}