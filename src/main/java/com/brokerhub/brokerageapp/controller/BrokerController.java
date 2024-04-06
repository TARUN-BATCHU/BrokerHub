package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.BrokerDTO;
import com.brokerhub.brokerageapp.dto.UpdateBrokerDTO;
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

    @GetMapping("/")
    public String home(){
        return "Welcome to home public home page - this page does not have security";
    }

    @GetMapping("/brokerDashboard")
    public String brokerDashboard(){
        return "Login successful now you are in dashboard";
    }

    @PostMapping("/createBroker")
    public ResponseEntity createBroker(@RequestBody @Valid BrokerDTO brokerDTO){
        return brokerService.createBroker(brokerDTO);
    }

    @PutMapping("/updateBroker")
    public ResponseEntity updateBroker(@RequestBody @Valid UpdateBrokerDTO updateBrokerDTO){
        return brokerService.updateBroker(updateBrokerDTO);
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
    public BigDecimal getBrokerageFromCity(@PathVariable Long brokerId, @PathVariable  String city){
        return brokerService.getTotalBrokerageFromCity(brokerId,city);
    }

    @GetMapping("/{brokerId}/getBrokerageOfUser/{userId}")
    public BigDecimal getBrokerageOfUser(@PathVariable Long brokerId, @PathVariable Long userId){
        return brokerService.getTotalBrokerageOfUser(brokerId,userId);
    }

    @GetMapping("/{brokerId}/getBrokerageOfProduct/{productId}")
    public BigDecimal getBrokerageFromProduct(@PathVariable Long brokerId, @PathVariable Long productId){
        return brokerService.findBrokerageFromProduct(brokerId,productId);
    }

}
