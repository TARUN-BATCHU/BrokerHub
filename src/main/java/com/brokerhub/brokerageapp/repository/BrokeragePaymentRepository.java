package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.BrokeragePayment;
import com.brokerhub.brokerageapp.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BrokeragePayment entity.
 * Provides data access methods for brokerage payment operations.
 */
@Repository
public interface BrokeragePaymentRepository extends JpaRepository<BrokeragePayment, Long> {

    /**
     * Find all brokerage payments for a specific broker
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "ORDER BY bp.dueDate ASC, bp.pendingAmount DESC")
    List<BrokeragePayment> findByBrokerIdWithDetails(@Param("brokerId") Long brokerId);

    /**
     * Search brokerage payments by firm name (case-insensitive partial match)
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND LOWER(m.firmName) LIKE LOWER(CONCAT('%', :firmName, '%')) " +
           "ORDER BY bp.dueDate ASC, bp.pendingAmount DESC")
    List<BrokeragePayment> findByBrokerIdAndFirmNameContaining(@Param("brokerId") Long brokerId, 
                                                               @Param("firmName") String firmName);

    /**
     * Find brokerage payments by status
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.status = :status " +
           "ORDER BY bp.dueDate ASC, bp.pendingAmount DESC")
    List<BrokeragePayment> findByBrokerIdAndStatus(@Param("brokerId") Long brokerId, 
                                                   @Param("status") PaymentStatus status);

    /**
     * Find overdue brokerage payments
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.dueDate < :currentDate " +
           "AND bp.pendingAmount > 0 " +
           "ORDER BY bp.dueDate ASC, bp.pendingAmount DESC")
    List<BrokeragePayment> findOverdueBrokeragePayments(@Param("brokerId") Long brokerId, 
                                                        @Param("currentDate") LocalDate currentDate);

    /**
     * Find brokerage payments due soon (within specified days)
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.dueDate BETWEEN :currentDate AND :dueSoonDate " +
           "AND bp.pendingAmount > 0 " +
           "ORDER BY bp.dueDate ASC, bp.pendingAmount DESC")
    List<BrokeragePayment> findBrokeragePaymentsDueSoon(@Param("brokerId") Long brokerId,
                                                        @Param("currentDate") LocalDate currentDate,
                                                        @Param("dueSoonDate") LocalDate dueSoonDate);

    /**
     * Find brokerage payment by merchant and broker
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "WHERE bp.merchant.userId = :merchantId " +
           "AND bp.broker.brokerId = :brokerId " +
           "AND bp.financialYear.yearId = :financialYearId")
    Optional<BrokeragePayment> findByMerchantAndBrokerAndFinancialYear(@Param("merchantId") Long merchantId,
                                                                       @Param("brokerId") Long brokerId,
                                                                       @Param("financialYearId") Long financialYearId);

    /**
     * Get total pending brokerage amount for a broker
     */
    @Query("SELECT COALESCE(SUM(bp.pendingAmount), 0) FROM BrokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.pendingAmount > 0")
    BigDecimal getTotalPendingBrokerageAmount(@Param("brokerId") Long brokerId);

    /**
     * Get total brokerage amount (paid + pending) for a broker
     */
    @Query("SELECT COALESCE(SUM(bp.netBrokerage), 0) FROM BrokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId")
    BigDecimal getTotalBrokerageAmount(@Param("brokerId") Long brokerId);

    /**
     * Get count of pending brokerage payments
     */
    @Query("SELECT COUNT(bp) FROM BrokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.pendingAmount > 0")
    Long getPendingBrokeragePaymentsCount(@Param("brokerId") Long brokerId);

    /**
     * Get count of overdue brokerage payments
     */
    @Query("SELECT COUNT(bp) FROM BrokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.dueDate < :currentDate " +
           "AND bp.pendingAmount > 0")
    Long getOverdueBrokeragePaymentsCount(@Param("brokerId") Long brokerId, 
                                         @Param("currentDate") LocalDate currentDate);

    /**
     * Get all unique firm names for search dropdown
     */
    @Query("SELECT DISTINCT m.firmName FROM BrokeragePayment bp " +
           "JOIN bp.merchant m " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "ORDER BY m.firmName")
    List<String> findDistinctFirmNamesByBrokerId(@Param("brokerId") Long brokerId);

    /**
     * Find top merchants by pending brokerage amount
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.pendingAmount > 0 " +
           "ORDER BY bp.pendingAmount DESC")
    List<BrokeragePayment> findTopMerchantsByPendingAmount(@Param("brokerId") Long brokerId);

    /**
     * Find brokerage payments by financial year
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.financialYear.yearId = :financialYearId " +
           "ORDER BY bp.dueDate ASC, bp.pendingAmount DESC")
    List<BrokeragePayment> findByBrokerIdAndFinancialYear(@Param("brokerId") Long brokerId,
                                                          @Param("financialYearId") Long financialYearId);

    /**
     * Update payment status for overdue payments
     */
    @Query("UPDATE BrokeragePayment bp SET bp.status = :status " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.dueDate < :currentDate " +
           "AND bp.pendingAmount > 0 " +
           "AND bp.status != :status")
    int updateOverduePaymentStatus(@Param("brokerId") Long brokerId,
                                   @Param("currentDate") LocalDate currentDate,
                                   @Param("status") PaymentStatus status);

    /**
     * Find brokerage payments with partial payments
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "LEFT JOIN FETCH bp.partPayments pp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.paidAmount > 0 " +
           "ORDER BY bp.lastPaymentDate DESC")
    List<BrokeragePayment> findBrokeragePaymentsWithPartPayments(@Param("brokerId") Long brokerId);

    /**
     * Get brokerage payment statistics for dashboard
     */
    @Query("SELECT " +
           "COUNT(bp) as totalPayments, " +
           "COALESCE(SUM(bp.netBrokerage), 0) as totalBrokerage, " +
           "COALESCE(SUM(bp.paidAmount), 0) as totalPaid, " +
           "COALESCE(SUM(bp.pendingAmount), 0) as totalPending, " +
           "COUNT(CASE WHEN bp.status = 'OVERDUE' THEN 1 END) as overdueCount " +
           "FROM BrokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId")
    Object[] getBrokeragePaymentStatistics(@Param("brokerId") Long brokerId);

    /**
     * Find all brokerage payments for a specific broker
     */
    List<BrokeragePayment> findByBrokerBrokerId(Long brokerId);

    /**
     * Find brokerage payment by broker and merchant
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "LEFT JOIN FETCH bp.partPayments " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND bp.merchant.userId = :merchantId")
    Optional<BrokeragePayment> findByBrokerIdAndMerchantUserId(@Param("brokerId") Long brokerId, 
                                                               @Param("merchantId") Long merchantId);

    /**
     * Find brokerage payments by broker and city
     */
    @Query("SELECT bp FROM BrokeragePayment bp " +
           "JOIN FETCH bp.merchant m " +
           "JOIN FETCH m.address a " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND a.city = :city")
    List<BrokeragePayment> findByBrokerBrokerIdAndMerchantAddressCity(@Param("brokerId") Long brokerId, 
                                                                     @Param("city") String city);
}
