package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface BrokerRepository extends JpaRepository<Broker, Long> {

    Optional<Broker> findByBrokerageFirmName(String brokerageFirmName);

    Optional<Broker> findByEmail(String email);

    Optional<Broker> findByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT SUM(u.total_payable_brokerage) FROM user u WHERE address_id IN (SELECT address_id FROM address WHERE city = ?1);", nativeQuery = true)
    BigDecimal findTotalBrokerageOfCity(String city);
}
