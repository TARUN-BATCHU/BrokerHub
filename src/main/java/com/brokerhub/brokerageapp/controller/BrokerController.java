package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.service.BrokerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/BrokerHub/Broker")
public class BrokerController {

    @Autowired
    BrokerService brokerService;

    @PostMapping("/createBroker")
    public ResponseEntity createBroker(@RequestBody @Valid Broker broker){
        return brokerService.createBroker(broker);
    }

    @PutMapping("/updateBroker")
    public Broker updateBroker(@RequestBody @Valid Broker broker){
        return brokerService.updateBroker(broker);
    }

    @DeleteMapping("/deleteBroker")
    public ResponseEntity deleteBroker(@RequestParam Long brokerId){
        return brokerService.deleteBroker(brokerId);
    }

    @GetMapping("/getBroker/{brokerId}")
    public Optional<Broker> getBrokerById(@PathVariable Long brokerId){
        return brokerService.findBrokerById(brokerId);
    }

    @GetMapping("/{brokerId}/getTotalBrokerage")
    public BigDecimal getTotalBrokerage(@PathVariable Long brokerId){
        return brokerService.getTotalBrokerage(brokerId);
    }

    @GetMapping("/{brokerId}/getBrokerageOfCity/{city}")
    public BigDecimal getBrokerageFromCity(@PathVariable Long brokerId, String city){
        return brokerService.getTotalBrokerageFromCity(brokerId,city);
    }
}
