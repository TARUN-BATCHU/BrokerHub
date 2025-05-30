package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.PendingPayment;
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
 * Repository interface for PendingPayment entity.
 * Provides data access methods for pending payment operations.
 */
@Repository
public interface PendingPaymentRepository extends JpaRepository<PendingPayment, Long> {

    /**
     * Find all pending payments for a specific broker
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "LEFT JOIN FETCH pp.transactions t " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount > 0 " +
           "ORDER BY pp.dueDate ASC, pp.totalPendingAmount DESC")
    List<PendingPayment> findByBrokerIdWithDetails(@Param("brokerId") Long brokerId);

    /**
     * Search pending payments by buyer firm name (case-insensitive partial match)
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "LEFT JOIN FETCH pp.transactions t " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND LOWER(b.firmName) LIKE LOWER(CONCAT('%', :buyerFirm, '%')) " +
           "AND pp.totalPendingAmount > 0 " +
           "ORDER BY pp.dueDate ASC, pp.totalPendingAmount DESC")
    List<PendingPayment> findByBrokerIdAndBuyerFirmContaining(@Param("brokerId") Long brokerId,
                                                              @Param("buyerFirm") String buyerFirm);

    /**
     * Find pending payments by status
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.status = :status " +
           "ORDER BY pp.dueDate ASC, pp.totalPendingAmount DESC")
    List<PendingPayment> findByBrokerIdAndStatus(@Param("brokerId") Long brokerId,
                                                 @Param("status") PaymentStatus status);

    /**
     * Find overdue pending payments
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.dueDate < :currentDate " +
           "AND pp.totalPendingAmount > 0 " +
           "ORDER BY pp.dueDate ASC, pp.totalPendingAmount DESC")
    List<PendingPayment> findOverduePendingPayments(@Param("brokerId") Long brokerId,
                                                    @Param("currentDate") LocalDate currentDate);

    /**
     * Find pending payments due soon (within specified days)
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.dueDate BETWEEN :currentDate AND :dueSoonDate " +
           "AND pp.totalPendingAmount > 0 " +
           "ORDER BY pp.dueDate ASC, pp.totalPendingAmount DESC")
    List<PendingPayment> findPendingPaymentsDueSoon(@Param("brokerId") Long brokerId,
                                                    @Param("currentDate") LocalDate currentDate,
                                                    @Param("dueSoonDate") LocalDate dueSoonDate);

    /**
     * Find pending payment by buyer and broker
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "WHERE pp.buyer.userId = :buyerId " +
           "AND pp.broker.brokerId = :brokerId " +
           "AND pp.financialYear.yearId = :financialYearId")
    Optional<PendingPayment> findByBuyerAndBrokerAndFinancialYear(@Param("buyerId") Long buyerId,
                                                                  @Param("brokerId") Long brokerId,
                                                                  @Param("financialYearId") Long financialYearId);

    /**
     * Get total pending amount for a broker
     */
    @Query("SELECT COALESCE(SUM(pp.totalPendingAmount), 0) FROM PendingPayment pp " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount > 0")
    BigDecimal getTotalPendingAmount(@Param("brokerId") Long brokerId);

    /**
     * Get count of pending payments
     */
    @Query("SELECT COUNT(pp) FROM PendingPayment pp " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount > 0")
    Long getPendingPaymentsCount(@Param("brokerId") Long brokerId);

    /**
     * Get count of overdue pending payments
     */
    @Query("SELECT COUNT(pp) FROM PendingPayment pp " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.dueDate < :currentDate " +
           "AND pp.totalPendingAmount > 0")
    Long getOverduePendingPaymentsCount(@Param("brokerId") Long brokerId,
                                       @Param("currentDate") LocalDate currentDate);

    /**
     * Get all unique buyer firm names for search dropdown
     */
    @Query("SELECT DISTINCT b.firmName FROM PendingPayment pp " +
           "JOIN pp.buyer b " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount > 0 " +
           "ORDER BY b.firmName")
    List<String> findDistinctBuyerFirmNamesByBrokerId(@Param("brokerId") Long brokerId);

    /**
     * Find top buyers by pending amount
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount > 0 " +
           "ORDER BY pp.totalPendingAmount DESC")
    List<PendingPayment> findTopBuyersByPendingAmount(@Param("brokerId") Long brokerId);

    /**
     * Find pending payments by financial year
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.financialYear.yearId = :financialYearId " +
           "AND pp.totalPendingAmount > 0 " +
           "ORDER BY pp.dueDate ASC, pp.totalPendingAmount DESC")
    List<PendingPayment> findByBrokerIdAndFinancialYear(@Param("brokerId") Long brokerId,
                                                        @Param("financialYearId") Long financialYearId);

    /**
     * Find pending payments by buyer city
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND a.city = :city " +
           "AND pp.totalPendingAmount > 0 " +
           "ORDER BY pp.dueDate ASC, pp.totalPendingAmount DESC")
    List<PendingPayment> findByBrokerIdAndBuyerCity(@Param("brokerId") Long brokerId,
                                                    @Param("city") String city);

    /**
     * Get pending payment statistics for dashboard
     */
    @Query("SELECT " +
           "COUNT(pp) as totalPendingPayments, " +
           "COALESCE(SUM(pp.totalPendingAmount), 0) as totalPendingAmount, " +
           "COUNT(CASE WHEN pp.status = 'OVERDUE' THEN 1 END) as overdueCount, " +
           "COUNT(CASE WHEN pp.status = 'DUE_SOON' THEN 1 END) as dueSoonCount, " +
           "COALESCE(AVG(pp.totalPendingAmount), 0) as averagePendingAmount " +
           "FROM PendingPayment pp " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount > 0")
    Object[] getPendingPaymentStatistics(@Param("brokerId") Long brokerId);

    /**
     * Get pending payments summary by city
     */
    @Query("SELECT a.city, COUNT(pp), COALESCE(SUM(pp.totalPendingAmount), 0) " +
           "FROM PendingPayment pp " +
           "JOIN pp.buyer b " +
           "JOIN b.address a " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount > 0 " +
           "GROUP BY a.city " +
           "ORDER BY SUM(pp.totalPendingAmount) DESC")
    List<Object[]> getPendingPaymentsSummaryByCity(@Param("brokerId") Long brokerId);

    /**
     * Get pending payments summary by buyer type
     */
    @Query("SELECT b.userType, COUNT(pp), COALESCE(SUM(pp.totalPendingAmount), 0) " +
           "FROM PendingPayment pp " +
           "JOIN pp.buyer b " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount > 0 " +
           "GROUP BY b.userType " +
           "ORDER BY SUM(pp.totalPendingAmount) DESC")
    List<Object[]> getPendingPaymentsSummaryByBuyerType(@Param("brokerId") Long brokerId);

    /**
     * Update payment status for overdue payments
     */
    @Query("UPDATE PendingPayment pp SET pp.status = :status " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.dueDate < :currentDate " +
           "AND pp.totalPendingAmount > 0 " +
           "AND pp.status != :status")
    int updateOverduePaymentStatus(@Param("brokerId") Long brokerId,
                                   @Param("currentDate") LocalDate currentDate,
                                   @Param("status") PaymentStatus status);

    /**
     * Find critical pending payments (high amount and overdue)
     */
    @Query("SELECT pp FROM PendingPayment pp " +
           "JOIN FETCH pp.buyer b " +
           "JOIN FETCH b.address a " +
           "WHERE pp.broker.brokerId = :brokerId " +
           "AND pp.totalPendingAmount >= :criticalAmount " +
           "AND pp.dueDate < :currentDate " +
           "ORDER BY pp.totalPendingAmount DESC, pp.dueDate ASC")
    List<PendingPayment> findCriticalPendingPayments(@Param("brokerId") Long brokerId,
                                                     @Param("criticalAmount") BigDecimal criticalAmount,
                                                     @Param("currentDate") LocalDate currentDate);
}
