package com.brokerhub.brokerageapp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public abstract class DashboardRepositoryImpl implements DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getMonthlyAnalytics(Long financialYearId) {
        String sql = """
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
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("financialYearId", financialYearId);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getProductAnalyticsByMonth(Long financialYearId) {
        String sql = """
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
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("financialYearId", financialYearId);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getCityAnalyticsByMonth(Long financialYearId) {
        String sql = """
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
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("financialYearId", financialYearId);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getMerchantTypeAnalyticsByMonth(Long financialYearId) {
        String sql = """
            SELECT
                YEAR(dl.date) as year,
                MONTH(dl.date) as month,
                u.user_type as merchantType,
                SUM(CASE WHEN u.user_type = 'MILLER' THEN lr.quantity ELSE 0 END) as totalQuantitySold,
                SUM(CASE WHEN u.user_type = 'TRADER' THEN lr.quantity ELSE 0 END) as totalQuantityBought,
                SUM(lr.total_brokerage) as totalBrokeragePaid,
                SUM(lr.total_products_cost) as totalTransactionValue,
                COUNT(lr.ledger_record_id) as totalTransactions,
                COUNT(DISTINCT u.user_id) as totalMerchants
            FROM daily_ledger dl
            JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
            JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
            JOIN user u ON ld.user_id = u.user_id
            WHERE dl.financial_year_year_id = :financialYearId
            GROUP BY YEAR(dl.date), MONTH(dl.date), u.user_type
            ORDER BY YEAR(dl.date), MONTH(dl.date), u.user_type
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("financialYearId", financialYearId);
        return query.getResultList();
    }

    @Override
    public Object[] getOverallTotals(Long financialYearId) {
        String sql = """
            SELECT
                SUM(lr.total_brokerage) as totalBrokerage,
                SUM(lr.quantity) as totalQuantity,
                SUM(lr.total_products_cost) as totalTransactionValue,
                COUNT(lr.ledger_record_id) as totalTransactions
            FROM daily_ledger dl
            JOIN ledger_details ld ON dl.daily_ledger_id = ld.daily_ledger_daily_ledger_id
            JOIN ledger_record lr ON ld.ledger_details_id = lr.ledger_details_ledger_details_id
            WHERE dl.financial_year_year_id = :financialYearId
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("financialYearId", financialYearId);
        List<Object[]> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Object[]> getProductBreakdownForCityAndMonth(
            Long financialYearId,
            Integer year,
            Integer month,
            String cityName) {
        String sql = """
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
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("financialYearId", financialYearId);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("cityName", cityName);
        return query.getResultList();
    }
}
