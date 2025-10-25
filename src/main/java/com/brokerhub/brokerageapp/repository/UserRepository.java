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
    
    Optional<User> findByBrokerBrokerIdAndFirmNameIgnoreCase(Long brokerId, String firmName);

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

    @Query("SELECT u.userId, u.firmName, u.address.city, u.brokerageRate FROM User u WHERE u.broker.brokerId = :brokerId")
    List<Object[]> findUserIdsAndFirmNamesAndCitiesByBrokerId(@Param("brokerId") Long brokerId);
    
    @Query(value = "SELECT DISTINCT u.* FROM User u " +
           "WHERE u.broker_id = :brokerId AND (" +
           "EXISTS (SELECT 1 FROM Ledger_details ld " +
           "JOIN Daily_ledger dl ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id " +
           "WHERE ld.user_id = u.user_id AND ld.broker_id = :brokerId AND dl.financial_year_year_id = :financialYearId) OR " +
           "EXISTS (SELECT 1 FROM Ledger_record lr " +
           "JOIN Ledger_details ld ON lr.ledger_details_ledger_details_Id = ld.ledger_details_id " +
           "JOIN Daily_ledger dl ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id " +
           "WHERE lr.to_buyer_user_id = u.user_id AND ld.broker_id = :brokerId AND dl.financial_year_year_id = :financialYearId)) " +
           "ORDER BY u.firm_name ASC", 
           countQuery = "SELECT COUNT(DISTINCT u.user_id) FROM User u " +
           "WHERE u.broker_id = :brokerId AND (" +
           "EXISTS (SELECT 1 FROM Ledger_details ld " +
           "JOIN Daily_ledger dl ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id " +
           "WHERE ld.user_id = u.user_id AND ld.broker_id = :brokerId AND dl.financial_year_year_id = :financialYearId) OR " +
           "EXISTS (SELECT 1 FROM Ledger_record lr " +
           "JOIN Ledger_details ld ON lr.ledger_details_ledger_details_Id = ld.ledger_details_id " +
           "JOIN Daily_ledger dl ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id " +
           "WHERE lr.to_buyer_user_id = u.user_id AND ld.broker_id = :brokerId AND dl.financial_year_year_id = :financialYearId))", 
           nativeQuery = true)
    Page<User> findUsersByBrokerIdAndFinancialYearSorted(@Param("brokerId") Long brokerId, 
                                                        @Param("financialYearId") Long financialYearId, 
                                                        Pageable pageable);

    @Query(value = "SELECT u.user_id, " +
           "COALESCE(sold.bags_sold, 0) as bags_sold, " +
           "COALESCE(bought.bags_bought, 0) as bags_bought, " +
           "COALESCE(sold.seller_brokerage, 0) + COALESCE(bought.buyer_brokerage, 0) as total_brokerage " +
           "FROM User u " +
           "LEFT JOIN (" +
           "    SELECT ld.user_id, SUM(lr.quantity) as bags_sold, SUM(lr.total_brokerage) as seller_brokerage " +
           "    FROM Ledger_details ld " +
           "    JOIN Ledger_record lr ON lr.ledger_details_ledger_details_Id = ld.ledger_details_id " +
           "    JOIN Daily_ledger dl ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id " +
           "    WHERE ld.broker_id = :brokerId AND dl.financial_year_year_id = :financialYearId " +
           "    GROUP BY ld.user_id" +
           ") sold ON sold.user_id = u.user_id " +
           "LEFT JOIN (" +
           "    SELECT lr.to_buyer_user_id, SUM(lr.quantity) as bags_bought, SUM(lr.total_brokerage) as buyer_brokerage " +
           "    FROM Ledger_record lr " +
           "    JOIN Ledger_details ld ON lr.ledger_details_ledger_details_Id = ld.ledger_details_id " +
           "    JOIN Daily_ledger dl ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id " +
           "    WHERE ld.broker_id = :brokerId AND dl.financial_year_year_id = :financialYearId " +
           "    GROUP BY lr.to_buyer_user_id" +
           ") bought ON bought.to_buyer_user_id = u.user_id " +
           "WHERE u.broker_id = :brokerId AND (sold.user_id IS NOT NULL OR bought.to_buyer_user_id IS NOT NULL)", 
           nativeQuery = true)
    List<Object[]> findUserBagCountsAndBrokerageByFinancialYear(@Param("brokerId") Long brokerId, 
                                                               @Param("financialYearId") Long financialYearId);

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
    
    @Query("SELECT DISTINCT a.city FROM Address a WHERE a.broker.brokerId = :brokerId")
    List<String> findDistinctCitiesByBrokerId(@Param("brokerId") Long brokerId);
}
