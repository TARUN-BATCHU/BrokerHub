package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import com.brokerhub.brokerageapp.service.LedgerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/BrokerHub/LedgerDetails")
public class LedgerDetailsController {

    @Autowired
    LedgerDetailsService ledgerDetailsService;

    @PostMapping("/createLedgerDetails")
    public ResponseEntity<String> createLedgerDetails(@RequestBody LedgerDetailsDTO ledgerDetailsDTO){
        return ledgerDetailsService.createLedgerDetails(ledgerDetailsDTO);
    }

    @GetMapping("/getAllLedgerDetails")
    public List<LedgerDetailsDTO> getAllLedgerDetails(@RequestBody Long brokerId){
        return ledgerDetailsService.getAllLedgerDetails();
    }

    @GetMapping("/getLedgerDetailsById/")
    public LedgerDetails getLedgerDetailById(@RequestParam Long ledgerDetailId, @RequestParam Long brokerId){
        return ledgerDetailsService.getLedgerDetailById(ledgerDetailId,brokerId);
    }

    @GetMapping("/getLedgerDetailsByDate")
    public List<LedgerDetailsDTO> getAllLedgerDetailsOnDate(@RequestParam LocalDate date, @RequestParam Long brokerId){
        return ledgerDetailsService.getAllLedgerDetailsOnDate(date,brokerId);
    }

    @GetMapping("/getLedgerDetailsBySeller")
    public List<LedgerDetailsDTO> getAllLedgerDetailsBySeller(@RequestParam Long sellerId, @RequestParam Long brokerId){
        return ledgerDetailsService.getAllLedgerDetailsBySeller(sellerId,brokerId);
    }

    @GetMapping("/getAllLedgerDetailsOfAllUsersFromCity")
    public List<LedgerDetailsDTO> getAllLedgerDetailsOfAllUsersFromCity(@RequestParam String city,@RequestParam Long brokerId,){
        return ledgerDetailsService.getAllLedgerDetailsOfAllUsersFromCity(city,brokerId);
    }

    @DeleteMapping("/deleteLedgerDetail")
    public ResponseEntity<String> deleteLedgerDetailById(@RequestParam Long ledgerDetailId,@RequestParam Long brokerId){
        return ledgerDetailsService.deleteLedgerDetailById(ledgerDetailId,brokerId);
    }

    @PutMapping("/updateLedgerDetailById")
    public LedgerDetails updateLedgerDetailById(@RequestParam Long ledgerDetailId,@RequestBody LedgerDetailsDTO ledgerDetailsDTO,@RequestParam Long brokerId){
        return ledgerDetailsService.updateLedgerDetailById(ledgerDetailId,ledgerDetailsDTO,brokerId);
    }
}
