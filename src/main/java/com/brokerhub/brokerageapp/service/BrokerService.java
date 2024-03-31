package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerDTO;
import com.brokerhub.brokerageapp.dto.UpdateBrokerDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

public interface BrokerService {
    ResponseEntity createBroker(@Valid BrokerDTO broker);

    ResponseEntity updateBroker(UpdateBrokerDTO UpdateBrokerDTO);

    ResponseEntity deleteBroker(Long brokerId);

    Optional<Broker> findBrokerById(Long brokerId);

    BigDecimal calculateTotalBrokerage(Long brokerId);

    public BigDecimal getTotalBrokerage(Long brokerId);

    BigDecimal getTotalBrokerageFromCity(Long brokerId, String city);

    BigDecimal getTotalBrokerageOfUser(Long brokerId, Long userId);

    BigDecimal findBrokerageFromProduct(Long brokerId, Long productId);
}
