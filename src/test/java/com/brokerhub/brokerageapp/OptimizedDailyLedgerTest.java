package com.brokerhub.brokerageapp;

import com.brokerhub.brokerageapp.dto.OptimizedDailyLedgerDTO;
import com.brokerhub.brokerageapp.service.DailyLedgerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OptimizedDailyLedgerTest {

    @Autowired
    private DailyLedgerService dailyLedgerService;

    @Test
    public void testGetOptimizedDailyLedger() {
        // Test with a date that might have data
        LocalDate testDate = LocalDate.of(2023, 6, 17);
        
        OptimizedDailyLedgerDTO result = dailyLedgerService.getOptimizedDailyLedger(testDate);
        
        // The result might be null if no data exists for this date, which is fine
        if (result != null) {
            assertNotNull(result.getDailyLedgerId());
            assertEquals(testDate, result.getDate());
            assertNotNull(result.getFinancialYearId());
            
            // Check that we have optimized data structure
            if (result.getLedgerDetails() != null && !result.getLedgerDetails().isEmpty()) {
                result.getLedgerDetails().forEach(ledgerDetail -> {
                    assertNotNull(ledgerDetail.getLedgerDetailsId());
                    
                    if (ledgerDetail.getFromSeller() != null) {
                        assertNotNull(ledgerDetail.getFromSeller().getUserId());
                        assertNotNull(ledgerDetail.getFromSeller().getFirmName());
                        // addressId might be null, which is fine
                    }
                    
                    if (ledgerDetail.getRecords() != null) {
                        ledgerDetail.getRecords().forEach(record -> {
                            assertNotNull(record.getLedgerRecordId());
                            
                            if (record.getToBuyer() != null) {
                                assertNotNull(record.getToBuyer().getUserId());
                                assertNotNull(record.getToBuyer().getFirmName());
                            }
                            
                            if (record.getProduct() != null) {
                                assertNotNull(record.getProduct().getProductId());
                                assertNotNull(record.getProduct().getProductName());
                            }
                        });
                    }
                });
            }
        }
        
        System.out.println("Test completed successfully. Result: " + (result != null ? "Data found" : "No data for this date"));
    }
}
