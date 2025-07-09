package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.*;
import com.brokerhub.brokerageapp.entity.Broker;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public interface BrokerService {
    ResponseEntity createBroker(@Valid BrokerDTO broker) throws IOException, InterruptedException;

    ResponseEntity updateBroker(UpdateBrokerDTO UpdateBrokerDTO);

    ResponseEntity deleteBroker(Long brokerId);

    Optional<Broker> findBrokerById(Long brokerId);

    BigDecimal calculateTotalBrokerage(Long brokerId);

    public BigDecimal getTotalBrokerage(Long brokerId);

    BigDecimal getTotalBrokerageFromCity(Long brokerId, String city);

    BigDecimal getTotalBrokerageOfUser(Long brokerId, Long userId);

    BigDecimal findBrokerageFromProduct(Long brokerId, Long productId);

    ResponseEntity<String> forgetPassword(String userName);

    String verifyAccount(String userName, Integer otp);

    ResponseEntity<String> regenerateOTP(String email);

    ResponseEntity<String> changePassword(ResetPasswordDTO resetPasswordDTO);

    ResponseEntity<String> createPassword(CreatePasswordDTO createPasswordDTO);

    ResponseEntity<?> login(BrokerLoginDTO brokerLoginDTO);

    String generatePasswordHash(String password);

    ResponseEntity<String> resetAdminPassword(String newPassword);

    Boolean findBrokerUserNameAvailability(String userName);

    Boolean findBrokerFirmNameAvailability(String firmName);
}
