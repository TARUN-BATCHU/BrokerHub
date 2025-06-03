package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.*;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.service.BrokerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/BrokerHub/Broker")
public class BrokerController {

    @Autowired
    BrokerService brokerService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody BrokerLoginDTO brokerLoginDTO){
        if(null == brokerLoginDTO.getUserName() || null == brokerLoginDTO.getPassword()){
            return new ResponseEntity("Username or password is missing",HttpStatus.BAD_REQUEST);
        }
        return brokerService.login(brokerLoginDTO);
    }

    @GetMapping("/")
    public String home(){
        return "Welcome to home public home page - this page does not have security";
    }

    @GetMapping("/brokerDashboard")
    public String brokerDashboard(){
        return "Login successful now you are in dashboard";
    }

    @PostMapping("/createBroker")
    @ResponseStatus(HttpStatus.CREATED)
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

    @GetMapping("/forgotPassword")
    public ResponseEntity<String> forgetPassword(@RequestParam("userName") String userName){
        return brokerService.forgetPassword(userName);
    }

    @PostMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String userName, @RequestParam Integer otp){
        return new ResponseEntity<>(brokerService.verifyAccount(userName,otp), HttpStatus.OK);
    }

    @PutMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOTP(@RequestParam String email){
        return brokerService.regenerateOTP(email);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
        return brokerService.changePassword(resetPasswordDTO);
    }

    @PutMapping("/createPassword")
    public ResponseEntity<String> createPassword(@RequestBody CreatePasswordDTO createPasswordDTO){
        return brokerService.createPassword(createPasswordDTO);
    }

    @GetMapping("/generateHash/{password}")
    public String generatePasswordHash(@PathVariable String password){
        return brokerService.generatePasswordHash(password);
    }

    @PostMapping("/resetAdminPassword/{newPassword}")
    public ResponseEntity<String> resetAdminPassword(@PathVariable String newPassword){
        return brokerService.resetAdminPassword(newPassword);
    }


}
