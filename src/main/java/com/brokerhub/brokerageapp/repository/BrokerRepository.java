package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokerRepository extends JpaRepository<Broker, Long> {
}
