package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.DisplayLedgerDetailDTO;
import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.dto.OptimizedLedgerDetailsDTO;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import com.brokerhub.brokerageapp.service.LedgerDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/BrokerHub/LedgerDetails")
@Slf4j
public class LedgerDetailsController {

    @Autowired
    LedgerDetailsService ledgerDetailsService;

    @PostMapping("/createLedgerDetails")
    public ResponseEntity<String> createLedgerDetails(@RequestBody LedgerDetailsDTO ledgerDetailsDTO){
        return ledgerDetailsService.createLedgerDetails(ledgerDetailsDTO);
    }


    @GetMapping("/getAllLedgerDetails")
    public List<LedgerDetails> getAllLedgerDetails(@RequestBody Long brokerId){
        return ledgerDetailsService.getAllLedgerDetails();
    }

    @GetMapping("/getLedgerDetailsById")
    public LedgerDetails getLedgerDetailById(@RequestParam Long ledgerDetailId, @RequestParam Long brokerId){
        return ledgerDetailsService.getLedgerDetailById(ledgerDetailId,brokerId);
    }

    /**
     * Get optimized ledger details by ID - solves lazy loading issues
     *
     * @param ledgerDetailId The ID of the ledger detail to fetch
     * @param brokerId The broker ID for authorization
     * @return OptimizedLedgerDetailsDTO with all related data eagerly loaded
     */
    @GetMapping("/getOptimizedLedgerDetailsById")
    public ResponseEntity<OptimizedLedgerDetailsDTO> getOptimizedLedgerDetailsById(
            @RequestParam Long ledgerDetailId,
            @RequestParam Long brokerId) {

        log.info("Fetching optimized ledger details by ID: {} for broker: {}", ledgerDetailId, brokerId);

        try {
            OptimizedLedgerDetailsDTO optimizedLedgerDetails =
                    ledgerDetailsService.getOptimizedLedgerDetailById(ledgerDetailId, brokerId);

            if (optimizedLedgerDetails != null) {
                log.info("Successfully fetched optimized ledger details for ID: {}", ledgerDetailId);
                return ResponseEntity.ok(optimizedLedgerDetails);
            } else {
                log.warn("No ledger details found for ID: {}", ledgerDetailId);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid parameters for getOptimizedLedgerDetailsById: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching optimized ledger details for ID: {}", ledgerDetailId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getLedgerDetailsByDate")
    public List<DisplayLedgerDetailDTO> getAllLedgerDetailsOnDate(@RequestParam LocalDate date, @RequestParam Long brokerId){
        return ledgerDetailsService.getAllLedgerDetailsOnDate(date,brokerId);
    }

    @GetMapping("/getLedgerDetailsBySeller")
    public List<LedgerDetailsDTO> getAllLedgerDetailsBySeller(@RequestParam Long sellerId, @RequestParam Long brokerId){
        return ledgerDetailsService.getAllLedgerDetailsBySeller(sellerId,brokerId);
    }
//
//    @GetMapping("/getAllLedgerDetailsOfAllUsersFromCity")
//    public List<LedgerDetailsDTO> getAllLedgerDetailsOfAllUsersFromCity(@RequestParam String city,@RequestParam Long brokerId,){
//        return ledgerDetailsService.getAllLedgerDetailsOfAllUsersFromCity(city,brokerId);
//    }
//
//    @DeleteMapping("/deleteLedgerDetail")
//    public ResponseEntity<String> deleteLedgerDetailById(@RequestParam Long ledgerDetailId,@RequestParam Long brokerId){
//        return ledgerDetailsService.deleteLedgerDetailById(ledgerDetailId,brokerId);
//    }
//
//    @PutMapping("/updateLedgerDetailById")
//    public LedgerDetails updateLedgerDetailById(@RequestParam Long ledgerDetailId,@RequestBody LedgerDetailsDTO ledgerDetailsDTO,@RequestParam Long brokerId){
//        return ledgerDetailsService.updateLedgerDetailById(ledgerDetailId,ledgerDetailsDTO,brokerId);
//    }

    //update the brokerage for all the transactions for a particular product
//    @PutMapping("/updateBrokerageForAProduct")
//    public LedgerDetails updateBrokerageForAProduct(@RequestParam Long productId,@RequestParam Long brokerage,@RequestParam Long brokerId){
//        return ledgerDetailsService.updateBrokerageForAProduct(productId,brokerage,brokerId);
//    }
}
