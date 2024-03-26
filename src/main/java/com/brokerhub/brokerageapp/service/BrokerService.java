package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

public interface BrokerService {
    ResponseEntity createBroker(Broker broker);

    Broker updateBroker(Broker broker);

    ResponseEntity deleteBroker(Long brokerId);

    Optional<Broker> findBrokerById(Long brokerId);

    BigDecimal calculateTotalBrokerage(Long brokerId);

    public BigDecimal getTotalBrokerage(Long brokerId);

    BigDecimal getTotalBrokerageFromCity(Long brokerId, String city);
}
