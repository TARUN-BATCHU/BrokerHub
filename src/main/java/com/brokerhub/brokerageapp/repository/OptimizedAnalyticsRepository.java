package com.brokerhub.brokerageapp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Optimized repository for analytics queries with improved performance
 * through better indexing, query optimization, and reduced data transfer.
 */
@Repository
public interface OptimizedAnalyticsRepository {

    /**
     * Optimized single query to get all analytics data for a financial year
     * Reduces multiple database calls to a single comprehensive query
     */
    @Query(value = """
        WITH monthly_totals AS (
            SELECT
                YEAR(dl.date) as year,
                MONTH(dl.date) as month,
                SUM(lr.total_brokerage) as total_brokerage,
                SUM(lr.quantity) as total_quantity,
                SUM(lr.total_products_cost) as total_transaction_value,
                COUNT(lr.ledger_record_id) as total_transactions
            FROM daily_ledger dl
            INNER JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
            INNER JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
            WHERE dl.financial_year_year_id = :financialYearId
            GROUP BY YEAR(dl.date), MONTH(dl.date)
        ),
        product_totals AS (
            SELECT
                p.product_id,
                p.product_name,
                SUM(lr.quantity) as total_quantity,
                SUM(lr.total_brokerage) as total_brokerage,
                SUM(lr.total_products_cost) as total_transaction_value,
                COUNT(lr.ledger_record_id) as total_transactions,
                AVG(lr.product_cost) as average_price,
                AVG(lr.brokerage) as average_brokerage_per_unit
            FROM daily_ledger dl
            INNER JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
            INNER JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
            INNER JOIN product p ON lr.product_product_id = p.product_id
            WHERE dl.financial_year_year_id = :financialYearId
            GROUP BY p.product_id, p.product_name
        ),
        city_totals AS (
            SELECT
                a.city,
                SUM(lr.quantity) as total_quantity,
                SUM(lr.total_brokerage) as total_brokerage,
                SUM(lr.total_products_cost) as total_transaction_value,
                COUNT(lr.ledger_record_id) as total_transactions,
                COUNT(DISTINCT ld.user_id) as total_sellers,
                COUNT(DISTINCT lr.to_buyer_user_id) as total_buyers
            FROM daily_ledger dl
            INNER JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
            INNER JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
            INNER JOIN user u ON lr.to_buyer_user_id = u.user_id
            INNER JOIN address a ON u.address_id = a.address_id
            WHERE dl.financial_year_year_id = :financialYearId
            GROUP BY a.city
        )
        SELECT 'monthly' as data_type, year, month, NULL as name, total_brokerage, total_quantity, total_transaction_value, total_transactions, NULL as extra1, NULL as extra2
        FROM monthly_totals
        UNION ALL
        SELECT 'product' as data_type, NULL as year, NULL as month, product_name as name, total_brokerage, total_quantity, total_transaction_value, total_transactions, average_price as extra1, average_brokerage_per_unit as extra2
        FROM product_totals
        UNION ALL
        SELECT 'city' as data_type, NULL as year, NULL as month, city as name, total_brokerage, total_quantity, total_transaction_value, total_transactions, total_sellers as extra1, total_buyers as extra2
        FROM city_totals
        ORDER BY data_type, year, month, name
        """, nativeQuery = true)
    List<Object[]> getComprehensiveAnalytics(@Param("financialYearId") Long financialYearId);

    /**
     * Optimized query for top performers with single database call
     */
    @Query(value = """
        WITH buyer_stats AS (
            SELECT
                buyer.user_id,
                buyer.owner_name,
                buyer.firm_name,
                a.city,
                buyer.user_type,
                SUM(lr.quantity) as total_quantity_bought,
                SUM(lr.total_products_cost) as total_amount_spent,
                SUM(lr.total_brokerage) as total_brokerage_paid,
                COUNT(lr.ledger_record_id) as total_transactions,
                AVG(lr.total_products_cost) as average_transaction_size,
                buyer.email,
                ROW_NUMBER() OVER (ORDER BY SUM(lr.quantity) DESC) as buyer_rank
            FROM daily_ledger dl
            INNER JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
            INNER JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
            INNER JOIN user buyer ON lr.to_buyer_user_id = buyer.user_id
            INNER JOIN address a ON buyer.address_id = a.address_id
            WHERE dl.financial_year_year_id = :financialYearId
            GROUP BY buyer.user_id, buyer.owner_name, buyer.firm_name, a.city, buyer.user_type, buyer.email
        ),
        seller_stats AS (
            SELECT
                seller.user_id,
                seller.owner_name,
                seller.firm_name,
                a.city,
                seller.user_type,
                SUM(lr.quantity) as total_quantity_sold,
                SUM(lr.total_products_cost) as total_amount_received,
                SUM(lr.total_brokerage) as total_brokerage_generated,
                COUNT(DISTINCT ld.ledger_details_id) as total_transactions,
                AVG(lr.total_products_cost) as average_transaction_size,
                seller.email,
                ROW_NUMBER() OVER (ORDER BY SUM(lr.quantity) DESC) as seller_rank
            FROM daily_ledger dl
            INNER JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
            INNER JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
            INNER JOIN user seller ON ld.user_id = seller.user_id
            INNER JOIN address a ON seller.address_id = a.address_id
            WHERE dl.financial_year_year_id = :financialYearId
            GROUP BY seller.user_id, seller.owner_name, seller.firm_name, a.city, seller.user_type, seller.email
        )
        SELECT 'buyer' as type, user_id, owner_name, firm_name, city, user_type, 
               total_quantity_bought as quantity, total_amount_spent as amount, 
               total_brokerage_paid as brokerage, total_transactions, average_transaction_size, email
        FROM buyer_stats WHERE buyer_rank <= 5
        UNION ALL
        SELECT 'seller' as type, user_id, owner_name, firm_name, city, user_type,
               total_quantity_sold as quantity, total_amount_received as amount,
               total_brokerage_generated as brokerage, total_transactions, average_transaction_size, email
        FROM seller_stats WHERE seller_rank <= 5
        ORDER BY type, quantity DESC
        """, nativeQuery = true)
    List<Object[]> getTopPerformersOptimized(@Param("financialYearId") Long financialYearId);

    /**
     * Optimized query for payment dashboard statistics
     */
    @Query(value = """
        SELECT
            'brokerage' as payment_type,
            COUNT(*) as total_count,
            SUM(pending_amount) as total_pending,
            SUM(CASE WHEN status = 'OVERDUE' THEN 1 ELSE 0 END) as overdue_count,
            SUM(CASE WHEN status = 'OVERDUE' THEN pending_amount ELSE 0 END) as overdue_amount,
            SUM(CASE WHEN due_date <= CURDATE() + INTERVAL 7 DAY THEN 1 ELSE 0 END) as due_soon_count,
            SUM(CASE WHEN due_date <= CURDATE() + INTERVAL 7 DAY THEN pending_amount ELSE 0 END) as due_soon_amount
        FROM brokerage_payment
        WHERE broker_id = :brokerId AND pending_amount > 0
        UNION ALL
        SELECT
            'pending' as payment_type,
            COUNT(*) as total_count,
            SUM(total_pending_amount) as total_pending,
            SUM(CASE WHEN status = 'OVERDUE' THEN 1 ELSE 0 END) as overdue_count,
            SUM(CASE WHEN status = 'OVERDUE' THEN total_pending_amount ELSE 0 END) as overdue_amount,
            SUM(CASE WHEN due_date <= CURDATE() + INTERVAL 7 DAY THEN 1 ELSE 0 END) as due_soon_count,
            SUM(CASE WHEN due_date <= CURDATE() + INTERVAL 7 DAY THEN total_pending_amount ELSE 0 END) as due_soon_amount
        FROM pending_payment
        WHERE broker_id = :brokerId AND total_pending_amount > 0
        UNION ALL
        SELECT
            'receivable' as payment_type,
            COUNT(*) as total_count,
            SUM(total_receivable_amount) as total_pending,
            SUM(CASE WHEN status = 'OVERDUE' THEN 1 ELSE 0 END) as overdue_count,
            SUM(CASE WHEN status = 'OVERDUE' THEN total_receivable_amount ELSE 0 END) as overdue_amount,
            SUM(CASE WHEN due_date <= CURDATE() + INTERVAL 7 DAY THEN 1 ELSE 0 END) as due_soon_count,
            SUM(CASE WHEN due_date <= CURDATE() + INTERVAL 7 DAY THEN total_receivable_amount ELSE 0 END) as due_soon_amount
        FROM receivable_payment
        WHERE broker_id = :brokerId AND total_receivable_amount > 0
        """, nativeQuery = true)
    List<Object[]> getPaymentDashboardStats(@Param("brokerId") Long brokerId);

    /**
     * Optimized query for financial year summary with minimal data transfer
     */
    @Query(value = """
        SELECT
            SUM(lr.total_brokerage) as total_brokerage,
            SUM(lr.quantity) as total_quantity,
            SUM(lr.total_products_cost) as total_transaction_value,
            COUNT(lr.ledger_record_id) as total_transactions,
            COUNT(DISTINCT ld.user_id) as total_sellers,
            COUNT(DISTINCT lr.to_buyer_user_id) as total_buyers,
            COUNT(DISTINCT lr.product_product_id) as total_products,
            COUNT(DISTINCT DATE(dl.date)) as total_trading_days,
            MIN(dl.date) as first_transaction_date,
            MAX(dl.date) as last_transaction_date
        FROM daily_ledger dl
        INNER JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        INNER JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        WHERE dl.financial_year_year_id = :financialYearId
        """, nativeQuery = true)
    Object[] getFinancialYearSummary(@Param("financialYearId") Long financialYearId);

    /**
     * Optimized query for broker performance metrics
     */
    @Query(value = """
        SELECT
            b.broker_id,
            b.broker_name,
            b.brokerage_firm_name,
            COUNT(DISTINCT dl.daily_ledger_id) as active_days,
            SUM(lr.total_brokerage) as total_brokerage_earned,
            COUNT(DISTINCT ld.user_id) as total_sellers,
            COUNT(DISTINCT lr.to_buyer_user_id) as total_buyers,
            COUNT(lr.ledger_record_id) as total_transactions,
            SUM(lr.quantity) as total_quantity_facilitated,
            SUM(lr.total_products_cost) as total_transaction_value,
            AVG(lr.total_brokerage) as average_brokerage_per_transaction
        FROM broker b
        INNER JOIN daily_ledger dl ON dl.financial_year_year_id = :financialYearId
        INNER JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        INNER JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        WHERE b.broker_id = :brokerId
        GROUP BY b.broker_id, b.broker_name, b.brokerage_firm_name
        """, nativeQuery = true)
    Object[] getBrokerPerformanceMetrics(@Param("brokerId") Long brokerId, @Param("financialYearId") Long financialYearId);

    /**
     * Optimized query for trend analysis with date grouping
     */
    @Query(value = """
        SELECT
            DATE(dl.date) as transaction_date,
            SUM(lr.total_brokerage) as daily_brokerage,
            SUM(lr.quantity) as daily_quantity,
            SUM(lr.total_products_cost) as daily_transaction_value,
            COUNT(lr.ledger_record_id) as daily_transactions,
            COUNT(DISTINCT ld.user_id) as daily_sellers,
            COUNT(DISTINCT lr.to_buyer_user_id) as daily_buyers
        FROM daily_ledger dl
        INNER JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        INNER JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        WHERE dl.financial_year_year_id = :financialYearId
        AND dl.date BETWEEN :startDate AND :endDate
        GROUP BY DATE(dl.date)
        ORDER BY DATE(dl.date)
        """, nativeQuery = true)
    List<Object[]> getDailyTrends(@Param("financialYearId") Long financialYearId, 
                                  @Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);
}
