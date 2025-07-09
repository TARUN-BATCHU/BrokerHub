package com.brokerhub.brokerageapp;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import com.brokerhub.brokerageapp.service.FinancialYearServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FinancialYearBrokerTest {

    @Autowired
    private FinancialYearServiceImpl financialYearService;

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private FinancialYearRepository financialYearRepository;

    @Test
    public void testCreateFinancialYearWithBroker() {
        // Create a test broker with all required fields
        Broker testBroker = new Broker();
        testBroker.setBrokerageFirmName("Test Broker Firm");
        testBroker.setBrokerName("Test Broker");
        testBroker.setUserName("testbroker");
        testBroker.setPassword("testpassword");
        testBroker.setEmail("test@broker.com");
        testBroker.setPhoneNumber("1234567890");
        testBroker = brokerRepository.save(testBroker);

        // Create financial year
        FinancialYear financialYear = new FinancialYear();
        financialYear.setStart(LocalDate.of(2022, 4, 1));
        financialYear.setEnd(LocalDate.of(2023, 3, 31));
        financialYear.setFinancialYearName("Test Year 2022-23");
        financialYear.setForBills(false);

        // Test the service method
        ResponseEntity<String> response = financialYearService.createFinancialYear(financialYear, testBroker.getBrokerId());

        // Verify the response
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Financial year created"));

        System.out.println("✅ FinancialYear creation with broker test passed!");
        System.out.println("Response: " + response.getBody());
    }
}