package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.PartPayment;
import com.brokerhub.brokerageapp.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PartPayment entity.
 * Provides data access methods for partial payment operations.
 */
@Repository
public interface PartPaymentRepository extends JpaRepository<PartPayment, Long> {

    /**
     * Find all part payments for a specific brokerage payment
     */
    @Query("SELECT pp FROM PartPayment pp " +
           "WHERE pp.brokeragePayment.id = :brokeragePaymentId " +
           "ORDER BY pp.paymentDate DESC, pp.createdAt DESC")
    List<PartPayment> findByBrokeragePaymentId(@Param("brokeragePaymentId") Long brokeragePaymentId);

    /**
     * Find part payment by payment reference
     */
    Optional<PartPayment> findByPaymentReference(String paymentReference);

    /**
     * Find recent part payments (within specified days)
     */
    @Query("SELECT pp FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND pp.paymentDate >= :fromDate " +
           "ORDER BY pp.paymentDate DESC, pp.createdAt DESC")
    List<PartPayment> findRecentPartPayments(@Param("brokerId") Long brokerId,
                                            @Param("fromDate") LocalDate fromDate);

    /**
     * Find part payments by payment method
     */
    @Query("SELECT pp FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND pp.method = :method " +
           "ORDER BY pp.paymentDate DESC")
    List<PartPayment> findByBrokerIdAndPaymentMethod(@Param("brokerId") Long brokerId,
                                                     @Param("method") PaymentMethod method);

    /**
     * Find unverified part payments
     */
    @Query("SELECT pp FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND (pp.verified = false OR pp.verified IS NULL) " +
           "ORDER BY pp.createdAt DESC")
    List<PartPayment> findUnverifiedPartPayments(@Param("brokerId") Long brokerId);

    /**
     * Find part payments by date range
     */
    @Query("SELECT pp FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND pp.paymentDate BETWEEN :fromDate AND :toDate " +
           "ORDER BY pp.paymentDate DESC, pp.createdAt DESC")
    List<PartPayment> findByBrokerIdAndDateRange(@Param("brokerId") Long brokerId,
                                                 @Param("fromDate") LocalDate fromDate,
                                                 @Param("toDate") LocalDate toDate);

    /**
     * Find part payments by merchant firm name
     */
    @Query("SELECT pp FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "JOIN bp.merchant m " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND LOWER(m.firmName) LIKE LOWER(CONCAT('%', :firmName, '%')) " +
           "ORDER BY pp.paymentDate DESC")
    List<PartPayment> findByBrokerIdAndMerchantFirmName(@Param("brokerId") Long brokerId,
                                                        @Param("firmName") String firmName);

    /**
     * Get total part payments amount for a brokerage payment
     */
    @Query("SELECT COALESCE(SUM(pp.amount), 0) FROM PartPayment pp " +
           "WHERE pp.brokeragePayment.id = :brokeragePaymentId")
    BigDecimal getTotalPartPaymentsAmount(@Param("brokeragePaymentId") Long brokeragePaymentId);

    /**
     * Get total part payments amount for a broker
     */
    @Query("SELECT COALESCE(SUM(pp.amount), 0) FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId")
    BigDecimal getTotalPartPaymentsAmountByBroker(@Param("brokerId") Long brokerId);

    /**
     * Get total part payments amount by date range
     */
    @Query("SELECT COALESCE(SUM(pp.amount), 0) FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND pp.paymentDate BETWEEN :fromDate AND :toDate")
    BigDecimal getTotalPartPaymentsAmountByDateRange(@Param("brokerId") Long brokerId,
                                                     @Param("fromDate") LocalDate fromDate,
                                                     @Param("toDate") LocalDate toDate);

    /**
     * Get count of part payments for a broker
     */
    @Query("SELECT COUNT(pp) FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId")
    Long getPartPaymentsCountByBroker(@Param("brokerId") Long brokerId);

    /**
     * Get count of unverified part payments
     */
    @Query("SELECT COUNT(pp) FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND (pp.verified = false OR pp.verified IS NULL)")
    Long getUnverifiedPartPaymentsCount(@Param("brokerId") Long brokerId);

    /**
     * Find large part payments (above specified amount)
     */
    @Query("SELECT pp FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND pp.amount >= :minAmount " +
           "ORDER BY pp.amount DESC, pp.paymentDate DESC")
    List<PartPayment> findLargePartPayments(@Param("brokerId") Long brokerId,
                                           @Param("minAmount") BigDecimal minAmount);

    /**
     * Get part payment statistics by payment method
     */
    @Query("SELECT pp.method, COUNT(pp), COALESCE(SUM(pp.amount), 0) " +
           "FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "GROUP BY pp.method " +
           "ORDER BY SUM(pp.amount) DESC")
    List<Object[]> getPartPaymentStatisticsByMethod(@Param("brokerId") Long brokerId);

    /**
     * Get daily part payment summary for a date range
     */
    @Query("SELECT pp.paymentDate, COUNT(pp), COALESCE(SUM(pp.amount), 0) " +
           "FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND pp.paymentDate BETWEEN :fromDate AND :toDate " +
           "GROUP BY pp.paymentDate " +
           "ORDER BY pp.paymentDate DESC")
    List<Object[]> getDailyPartPaymentSummary(@Param("brokerId") Long brokerId,
                                              @Param("fromDate") LocalDate fromDate,
                                              @Param("toDate") LocalDate toDate);

    /**
     * Find part payments requiring verification (large amounts)
     */
    @Query("SELECT pp FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId " +
           "AND pp.amount >= :verificationThreshold " +
           "AND (pp.verified = false OR pp.verified IS NULL) " +
           "ORDER BY pp.amount DESC, pp.createdAt DESC")
    List<PartPayment> findPartPaymentsRequiringVerification(@Param("brokerId") Long brokerId,
                                                            @Param("verificationThreshold") BigDecimal verificationThreshold);

    /**
     * Get average part payment amount for a broker
     */
    @Query("SELECT COALESCE(AVG(pp.amount), 0) FROM PartPayment pp " +
           "JOIN pp.brokeragePayment bp " +
           "WHERE bp.broker.brokerId = :brokerId")
    BigDecimal getAveragePartPaymentAmount(@Param("brokerId") Long brokerId);

    /**
     * Find duplicate payment references (for validation)
     */
    @Query("SELECT pp.paymentReference, COUNT(pp) FROM PartPayment pp " +
           "WHERE pp.paymentReference IS NOT NULL " +
           "GROUP BY pp.paymentReference " +
           "HAVING COUNT(pp) > 1")
    List<Object[]> findDuplicatePaymentReferences();
}
