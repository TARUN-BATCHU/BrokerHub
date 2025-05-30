package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.ReceivablePayment;
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
 * Repository interface for ReceivablePayment entity.
 * Provides data access methods for receivable payment operations.
 */
@Repository
public interface ReceivablePaymentRepository extends JpaRepository<ReceivablePayment, Long> {

    /**
     * Find all receivable payments for a specific broker
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "LEFT JOIN FETCH rp.owedBy ob " +
           "LEFT JOIN FETCH ob.buyer b " +
           "LEFT JOIN FETCH ob.transactions t " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0 " +
           "ORDER BY rp.dueDate ASC, rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findByBrokerIdWithDetails(@Param("brokerId") Long brokerId);

    /**
     * Search receivable payments by seller firm name (case-insensitive partial match)
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "LEFT JOIN FETCH rp.owedBy ob " +
           "LEFT JOIN FETCH ob.buyer b " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND LOWER(s.firmName) LIKE LOWER(CONCAT('%', :sellerFirm, '%')) " +
           "AND rp.totalReceivableAmount > 0 " +
           "ORDER BY rp.dueDate ASC, rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findByBrokerIdAndSellerFirmContaining(@Param("brokerId") Long brokerId,
                                                                  @Param("sellerFirm") String sellerFirm);

    /**
     * Find receivable payments by status
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.status = :status " +
           "ORDER BY rp.dueDate ASC, rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findByBrokerIdAndStatus(@Param("brokerId") Long brokerId,
                                                    @Param("status") PaymentStatus status);

    /**
     * Find overdue receivable payments
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.dueDate < :currentDate " +
           "AND rp.totalReceivableAmount > 0 " +
           "ORDER BY rp.dueDate ASC, rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findOverdueReceivablePayments(@Param("brokerId") Long brokerId,
                                                          @Param("currentDate") LocalDate currentDate);

    /**
     * Find receivable payments due soon (within specified days)
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.dueDate BETWEEN :currentDate AND :dueSoonDate " +
           "AND rp.totalReceivableAmount > 0 " +
           "ORDER BY rp.dueDate ASC, rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findReceivablePaymentsDueSoon(@Param("brokerId") Long brokerId,
                                                          @Param("currentDate") LocalDate currentDate,
                                                          @Param("dueSoonDate") LocalDate dueSoonDate);

    /**
     * Find receivable payment by seller and broker
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "WHERE rp.seller.userId = :sellerId " +
           "AND rp.broker.brokerId = :brokerId " +
           "AND rp.financialYear.yearId = :financialYearId")
    Optional<ReceivablePayment> findBySellerAndBrokerAndFinancialYear(@Param("sellerId") Long sellerId,
                                                                      @Param("brokerId") Long brokerId,
                                                                      @Param("financialYearId") Long financialYearId);

    /**
     * Get total receivable amount for a broker
     */
    @Query("SELECT COALESCE(SUM(rp.totalReceivableAmount), 0) FROM ReceivablePayment rp " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0")
    BigDecimal getTotalReceivableAmount(@Param("brokerId") Long brokerId);

    /**
     * Get count of receivable payments
     */
    @Query("SELECT COUNT(rp) FROM ReceivablePayment rp " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0")
    Long getReceivablePaymentsCount(@Param("brokerId") Long brokerId);

    /**
     * Get count of overdue receivable payments
     */
    @Query("SELECT COUNT(rp) FROM ReceivablePayment rp " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.dueDate < :currentDate " +
           "AND rp.totalReceivableAmount > 0")
    Long getOverdueReceivablePaymentsCount(@Param("brokerId") Long brokerId,
                                          @Param("currentDate") LocalDate currentDate);

    /**
     * Get all unique seller firm names for search dropdown
     */
    @Query("SELECT DISTINCT s.firmName FROM ReceivablePayment rp " +
           "JOIN rp.seller s " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0 " +
           "ORDER BY s.firmName")
    List<String> findDistinctSellerFirmNamesByBrokerId(@Param("brokerId") Long brokerId);

    /**
     * Find top sellers by receivable amount
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0 " +
           "ORDER BY rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findTopSellersByReceivableAmount(@Param("brokerId") Long brokerId);

    /**
     * Find receivable payments by financial year
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.financialYear.yearId = :financialYearId " +
           "AND rp.totalReceivableAmount > 0 " +
           "ORDER BY rp.dueDate ASC, rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findByBrokerIdAndFinancialYear(@Param("brokerId") Long brokerId,
                                                           @Param("financialYearId") Long financialYearId);

    /**
     * Find receivable payments by seller city
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND a.city = :city " +
           "AND rp.totalReceivableAmount > 0 " +
           "ORDER BY rp.dueDate ASC, rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findByBrokerIdAndSellerCity(@Param("brokerId") Long brokerId,
                                                        @Param("city") String city);

    /**
     * Get receivable payment statistics for dashboard
     */
    @Query("SELECT " +
           "COUNT(rp) as totalReceivablePayments, " +
           "COALESCE(SUM(rp.totalReceivableAmount), 0) as totalReceivableAmount, " +
           "COUNT(CASE WHEN rp.status = 'OVERDUE' THEN 1 END) as overdueCount, " +
           "COUNT(CASE WHEN rp.status = 'DUE_SOON' THEN 1 END) as dueSoonCount, " +
           "COALESCE(AVG(rp.totalReceivableAmount), 0) as averageReceivableAmount " +
           "FROM ReceivablePayment rp " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0")
    Object[] getReceivablePaymentStatistics(@Param("brokerId") Long brokerId);

    /**
     * Get receivable payments summary by city
     */
    @Query("SELECT a.city, COUNT(rp), COALESCE(SUM(rp.totalReceivableAmount), 0) " +
           "FROM ReceivablePayment rp " +
           "JOIN rp.seller s " +
           "JOIN s.address a " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0 " +
           "GROUP BY a.city " +
           "ORDER BY SUM(rp.totalReceivableAmount) DESC")
    List<Object[]> getReceivablePaymentsSummaryByCity(@Param("brokerId") Long brokerId);

    /**
     * Get receivable payments summary by seller type
     */
    @Query("SELECT s.userType, COUNT(rp), COALESCE(SUM(rp.totalReceivableAmount), 0) " +
           "FROM ReceivablePayment rp " +
           "JOIN rp.seller s " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0 " +
           "GROUP BY s.userType " +
           "ORDER BY SUM(rp.totalReceivableAmount) DESC")
    List<Object[]> getReceivablePaymentsSummaryBySellerType(@Param("brokerId") Long brokerId);

    /**
     * Update payment status for overdue payments
     */
    @Query("UPDATE ReceivablePayment rp SET rp.status = :status " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.dueDate < :currentDate " +
           "AND rp.totalReceivableAmount > 0 " +
           "AND rp.status != :status")
    int updateOverduePaymentStatus(@Param("brokerId") Long brokerId,
                                   @Param("currentDate") LocalDate currentDate,
                                   @Param("status") PaymentStatus status);

    /**
     * Find critical receivable payments (high amount and overdue)
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount >= :criticalAmount " +
           "AND rp.dueDate < :currentDate " +
           "ORDER BY rp.totalReceivableAmount DESC, rp.dueDate ASC")
    List<ReceivablePayment> findCriticalReceivablePayments(@Param("brokerId") Long brokerId,
                                                           @Param("criticalAmount") BigDecimal criticalAmount,
                                                           @Param("currentDate") LocalDate currentDate);

    /**
     * Find sellers with multiple buyers owing money
     */
    @Query("SELECT rp FROM ReceivablePayment rp " +
           "JOIN FETCH rp.seller s " +
           "JOIN FETCH s.address a " +
           "LEFT JOIN FETCH rp.owedBy ob " +
           "WHERE rp.broker.brokerId = :brokerId " +
           "AND rp.totalReceivableAmount > 0 " +
           "AND SIZE(rp.owedBy) > 1 " +
           "ORDER BY SIZE(rp.owedBy) DESC, rp.totalReceivableAmount DESC")
    List<ReceivablePayment> findSellersWithMultipleBuyers(@Param("brokerId") Long brokerId);
}
