package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByFirmName(String firmName);

    Optional<User> findByGstNumber(String gstNumber);

    List<User> findByAddressCity(String city);

    List<User> findByTotalPayableBrokerageGreaterThanEqual(int brokerage);

    List<User> findByTotalPayableBrokerageBetween(int min, int max);

    // Optimized queries to fetch only required fields
    @Query("SELECT u.userId, u.firmName FROM User u")
    List<Object[]> findUserIdsAndFirmNames();

    @Query("SELECT u.firmName FROM User u")
    List<String> findAllFirmNames();

    @Query("SELECT u.userId, u.firmName, u.userType, u.gstNumber, u.ownerName FROM User u")
    List<Object[]> findBasicUserInfo();
}
