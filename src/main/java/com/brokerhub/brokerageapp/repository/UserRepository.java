package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Multi-tenant aware queries - all include broker filtering
    Optional<User> findByBrokerBrokerIdAndFirmName(Long brokerId, String firmName);

    Optional<User> findByBrokerBrokerIdAndGstNumber(Long brokerId, String gstNumber);

    List<User> findByBrokerBrokerIdAndAddressCity(Long brokerId, String city);

    List<User> findByBrokerBrokerIdAndTotalPayableBrokerageGreaterThanEqual(Long brokerId, int brokerage);

    List<User> findByBrokerBrokerIdAndTotalPayableBrokerageBetween(Long brokerId, int min, int max);

    List<User> findByBrokerBrokerId(Long brokerId);

    Page<User> findByBrokerBrokerId(Long brokerId, Pageable pageable);

    // Optimized queries with broker filtering
    @Query("SELECT u.userId, u.firmName FROM User u WHERE u.broker.brokerId = :brokerId")
    List<Object[]> findUserIdsAndFirmNamesByBrokerId(@Param("brokerId") Long brokerId);

    @Query("SELECT u.firmName FROM User u WHERE u.broker.brokerId = :brokerId")
    List<String> findAllFirmNamesByBrokerId(@Param("brokerId") Long brokerId);

    @Query("SELECT u.userId, u.firmName, u.userType, u.gstNumber, u.ownerName FROM User u WHERE u.broker.brokerId = :brokerId")
    List<Object[]> findBasicUserInfoByBrokerId(@Param("brokerId") Long brokerId);

    @Query("SELECT u.userId, u.firmName, u.address.city FROM User u WHERE u.broker.brokerId = :brokerId")
    List<Object[]> findUserIdsAndFirmNamesAndCitiesByBrokerId(@Param("brokerId") Long brokerId);
    
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN LedgerDetails ld ON u.userId = ld.fromSeller.userId " +
           "JOIN DailyLedger dl ON ld.dailyLedger.dailyLedgerId = dl.dailyLedgerId " +
           "WHERE u.broker.brokerId = :brokerId AND dl.financialYear.yearId = :financialYearId")
    Page<User> findUsersByBrokerIdAndFinancialYear(@Param("brokerId") Long brokerId, 
                                                   @Param("financialYearId") Long financialYearId, 
                                                   Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.address LEFT JOIN FETCH u.bankDetails WHERE u.userId = :userId")
    Optional<User> findByIdWithDetails(@Param("userId") Long userId);

    // Legacy methods (deprecated - use broker-aware versions)
    @Deprecated
    Optional<User> findByFirmName(String firmName);

    @Deprecated
    Optional<User> findByGstNumber(String gstNumber);

    @Deprecated
    List<User> findByAddressCity(String city);

    @Deprecated
    List<User> findByTotalPayableBrokerageGreaterThanEqual(int brokerage);

    @Deprecated
    List<User> findByTotalPayableBrokerageBetween(int min, int max);

    @Deprecated
    @Query("SELECT u.userId, u.firmName FROM User u")
    List<Object[]> findUserIdsAndFirmNames();

    @Deprecated
    @Query("SELECT u.firmName FROM User u")
    List<String> findAllFirmNames();

    @Deprecated
    @Query("SELECT u.userId, u.firmName, u.userType, u.gstNumber, u.ownerName FROM User u")
    List<Object[]> findBasicUserInfo();
}
