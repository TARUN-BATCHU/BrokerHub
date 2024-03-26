package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrokerRepository extends JpaRepository<Broker, Long> {

    Optional<Broker> findByBrokerageFirmName(String brokerageFirmName);

    Optional<Broker> findByEmail(String email);

    Optional<Broker> findByPhoneNumber(String phoneNumber);
}
