package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByFirmName(String firmName);

    Optional<User> findByGstNumber(String gstNumber);

    List<User> findByAddressCity(String city);

    List<User> findByTotalPayableBrokerageGreaterThanEqual(int brokerage);

    List<User> findByBrokerageBetween(int min, int max);
}
