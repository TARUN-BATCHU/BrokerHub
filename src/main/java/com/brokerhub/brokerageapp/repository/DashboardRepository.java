package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<DailyLedger, Long> {

    // Monthly analytics query
    @Query(value = """
        SELECT
            YEAR(dl.date) as year,
            MONTH(dl.date) as month,
            SUM(lr.total_brokerage) as totalBrokerage,
            SUM(lr.quantity) as totalQuantity,
            SUM(lr.total_products_cost) as totalTransactionValue,
            COUNT(lr.ledger_record_id) as totalTransactions
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        WHERE dl.financial_year_year_id = :financialYearId
        GROUP BY YEAR(dl.date), MONTH(dl.date)
        ORDER BY YEAR(dl.date), MONTH(dl.date)
        """, nativeQuery = true)
    List<Object[]> getMonthlyAnalytics(@Param("financialYearId") Long financialYearId);

    // Product analytics by month
    @Query(value = """
        SELECT
            YEAR(dl.date) as year,
            MONTH(dl.date) as month,
            p.product_id as productId,
            p.product_name as productName,
            SUM(lr.quantity) as totalQuantity,
            SUM(lr.total_brokerage) as totalBrokerage,
            SUM(lr.total_products_cost) as totalTransactionValue,
            COUNT(lr.ledger_record_id) as totalTransactions,
            AVG(lr.product_cost) as averagePrice,
            AVG(lr.brokerage) as averageBrokeragePerUnit
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        JOIN product p ON lr.product_product_id = p.product_id
        WHERE dl.financial_year_year_id = :financialYearId
        GROUP BY YEAR(dl.date), MONTH(dl.date), p.product_id, p.product_name
        ORDER BY YEAR(dl.date), MONTH(dl.date), p.product_name
        """, nativeQuery = true)
    List<Object[]> getProductAnalyticsByMonth(@Param("financialYearId") Long financialYearId);

    // City analytics by month
    @Query(value = """
        SELECT
            YEAR(dl.date) as year,
            MONTH(dl.date) as month,
            a.city as cityName,
            SUM(lr.quantity) as totalQuantity,
            SUM(lr.total_brokerage) as totalBrokerage,
            SUM(lr.total_products_cost) as totalTransactionValue,
            COUNT(lr.ledger_record_id) as totalTransactions,
            COUNT(DISTINCT ld.user_id) as totalSellers,
            COUNT(DISTINCT lr.to_buyer_user_id) as totalBuyers
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        JOIN user u ON lr.to_buyer_user_id = u.user_id
        JOIN address a ON u.address_id = a.address_id
        WHERE dl.financial_year_year_id = :financialYearId
        GROUP BY YEAR(dl.date), MONTH(dl.date), a.city
        ORDER BY YEAR(dl.date), MONTH(dl.date), a.city
        """, nativeQuery = true)
    List<Object[]> getCityAnalyticsByMonth(@Param("financialYearId") Long financialYearId);

    // Merchant type analytics by month
    @Query(value = """
        SELECT
            YEAR(dl.date) as year,
            MONTH(dl.date) as month,
            seller.user_type as merchantType,
            SUM(lr.quantity) as totalQuantitySold,
            0 as totalQuantityBought,
            SUM(lr.total_brokerage) as totalBrokeragePaid,
            SUM(lr.total_products_cost) as totalTransactionValue,
            COUNT(lr.ledger_record_id) as totalTransactions,
            COUNT(DISTINCT ld.user_id) as totalMerchants
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        JOIN user seller ON ld.user_id = seller.user_id
        WHERE dl.financial_year_year_id = :financialYearId
        GROUP BY YEAR(dl.date), MONTH(dl.date), seller.user_type

        UNION ALL

        SELECT
            YEAR(dl.date) as year,
            MONTH(dl.date) as month,
            buyer.user_type as merchantType,
            0 as totalQuantitySold,
            SUM(lr.quantity) as totalQuantityBought,
            0 as totalBrokeragePaid,
            SUM(lr.total_products_cost) as totalTransactionValue,
            COUNT(lr.ledger_record_id) as totalTransactions,
            COUNT(DISTINCT lr.to_buyer_user_id) as totalMerchants
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        JOIN user buyer ON lr.to_buyer_user_id = buyer.user_id
        WHERE dl.financial_year_year_id = :financialYearId
        GROUP BY YEAR(dl.date), MONTH(dl.date), buyer.user_type
        ORDER BY year, month, merchantType
        """, nativeQuery = true)
    List<Object[]> getMerchantTypeAnalyticsByMonth(@Param("financialYearId") Long financialYearId);

    // Overall totals for financial year
    @Query(value = """
        SELECT
            SUM(lr.total_brokerage) as totalBrokerage,
            SUM(lr.quantity) as totalQuantity,
            SUM(lr.total_products_cost) as totalTransactionValue,
            COUNT(lr.ledger_record_id) as totalTransactions
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        WHERE dl.financial_year_year_id = :financialYearId
        """, nativeQuery = true)
    Object[] getOverallTotals(@Param("financialYearId") Long financialYearId);

    // Product breakdown for specific city and month
    @Query(value = """
        SELECT
            p.product_id as productId,
            p.product_name as productName,
            SUM(lr.quantity) as totalQuantity,
            SUM(lr.total_brokerage) as totalBrokerage,
            SUM(lr.total_products_cost) as totalTransactionValue,
            COUNT(lr.ledger_record_id) as totalTransactions,
            AVG(lr.product_cost) as averagePrice,
            AVG(lr.brokerage) as averageBrokeragePerUnit
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        JOIN product p ON lr.product_product_id = p.product_id
        JOIN user u ON lr.to_buyer_user_id = u.user_id
        JOIN address a ON u.address_id = a.address_id
        WHERE dl.financial_year_year_id = :financialYearId
        AND YEAR(dl.date) = :year
        AND MONTH(dl.date) = :month
        AND a.city = :cityName
        GROUP BY p.product_id, p.product_name
        ORDER BY p.product_name
        """, nativeQuery = true)
    List<Object[]> getProductBreakdownForCityAndMonth(
            @Param("financialYearId") Long financialYearId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("cityName") String cityName
    );

    // Top 5 buyers by quantity
    @Query(value = """
        SELECT
            buyer.user_id as buyerId,
            buyer.owner_name as buyerName,
            buyer.firm_name as firmName,
            a.city as city,
            buyer.user_type as userType,
            SUM(lr.quantity) as totalQuantityBought,
            SUM(lr.total_products_cost) as totalAmountSpent,
            SUM(lr.total_brokerage) as totalBrokeragePaid,
            COUNT(lr.ledger_record_id) as totalTransactions,
            AVG(lr.total_products_cost) as averageTransactionSize,
            '' as phoneNumber,
            buyer.email as email
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        JOIN user buyer ON lr.to_buyer_user_id = buyer.user_id
        JOIN address a ON buyer.address_id = a.address_id
        WHERE dl.financial_year_year_id = :financialYearId
        GROUP BY buyer.user_id, buyer.owner_name, buyer.firm_name, a.city, buyer.user_type, buyer.email
        ORDER BY SUM(lr.quantity) DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> getTop5BuyersByQuantity(@Param("financialYearId") Long financialYearId);

    // Top 5 sellers by quantity
    @Query(value = """
        SELECT
            seller.user_id as sellerId,
            seller.owner_name as sellerName,
            seller.firm_name as firmName,
            a.city as city,
            seller.user_type as userType,
            SUM(lr.quantity) as totalQuantitySold,
            SUM(lr.total_products_cost) as totalAmountReceived,
            SUM(lr.total_brokerage) as totalBrokerageGenerated,
            COUNT(DISTINCT ld.ledger_details_id) as totalTransactions,
            AVG(lr.total_products_cost) as averageTransactionSize,
            '' as phoneNumber,
            seller.email as email
        FROM daily_ledger dl
        JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
        JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
        JOIN user seller ON ld.user_id = seller.user_id
        JOIN address a ON seller.address_id = a.address_id
        WHERE dl.financial_year_year_id = :financialYearId
        GROUP BY seller.user_id, seller.owner_name, seller.firm_name, a.city, seller.user_type, seller.email
        ORDER BY SUM(lr.quantity) DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> getTop5SellersByQuantity(@Param("financialYearId") Long financialYearId);

    // Top 5 merchants by brokerage amount (combining both buying and selling activities)
    @Query(value = """
        SELECT
            merchant_data.merchantId,
            merchant_data.merchantName,
            merchant_data.firmName,
            merchant_data.city,
            merchant_data.userType,
            merchant_data.totalBrokeragePaid,
            merchant_data.totalQuantityTraded,
            merchant_data.totalQuantityBought,
            merchant_data.totalQuantitySold,
            merchant_data.totalAmountTraded,
            merchant_data.totalTransactions,
            merchant_data.phoneNumber,
            merchant_data.email
        FROM (
            SELECT
                u.user_id as merchantId,
                u.owner_name as merchantName,
                u.firm_name as firmName,
                a.city as city,
                u.user_type as userType,
                COALESCE(buyer_data.totalBrokeragePaid, 0) + COALESCE(seller_data.totalBrokerageGenerated, 0) as totalBrokeragePaid,
                COALESCE(buyer_data.totalQuantityBought, 0) + COALESCE(seller_data.totalQuantitySold, 0) as totalQuantityTraded,
                COALESCE(buyer_data.totalQuantityBought, 0) as totalQuantityBought,
                COALESCE(seller_data.totalQuantitySold, 0) as totalQuantitySold,
                COALESCE(buyer_data.totalAmountSpent, 0) + COALESCE(seller_data.totalAmountReceived, 0) as totalAmountTraded,
                COALESCE(buyer_data.totalTransactions, 0) + COALESCE(seller_data.totalTransactions, 0) as totalTransactions,
                '' as phoneNumber,
                u.email as email
            FROM user u
            JOIN address a ON u.address_id = a.address_id
            LEFT JOIN (
                SELECT
                    buyer.user_id,
                    SUM(lr.total_brokerage) as totalBrokeragePaid,
                    SUM(lr.quantity) as totalQuantityBought,
                    SUM(lr.total_products_cost) as totalAmountSpent,
                    COUNT(lr.ledger_record_id) as totalTransactions
                FROM daily_ledger dl
                JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
                JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
                JOIN user buyer ON lr.to_buyer_user_id = buyer.user_id
                WHERE dl.financial_year_year_id = :financialYearId
                GROUP BY buyer.user_id
            ) buyer_data ON u.user_id = buyer_data.user_id
            LEFT JOIN (
                SELECT
                    seller.user_id,
                    SUM(lr.total_brokerage) as totalBrokerageGenerated,
                    SUM(lr.quantity) as totalQuantitySold,
                    SUM(lr.total_products_cost) as totalAmountReceived,
                    COUNT(DISTINCT ld.ledger_details_id) as totalTransactions
                FROM daily_ledger dl
                JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
                JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
                JOIN user seller ON ld.user_id = seller.user_id
                WHERE dl.financial_year_year_id = :financialYearId
                GROUP BY seller.user_id
            ) seller_data ON u.user_id = seller_data.user_id
            WHERE (buyer_data.user_id IS NOT NULL OR seller_data.user_id IS NOT NULL)
        ) merchant_data
        ORDER BY merchant_data.totalBrokeragePaid DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> getTop5MerchantsByBrokerage(@Param("financialYearId") Long financialYearId);
}
