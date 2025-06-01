-- =====================================================
-- DATABASE OPTIMIZATION INDEXES
-- =====================================================
-- This file contains optimized database indexes to improve
-- query performance for the brokerage application.
-- Execute these scripts after setting up the main database.
-- =====================================================

-- Note: Indexes will be created only if they don't exist
-- If you get "Duplicate key name" errors, the indexes already exist and can be ignored

-- =====================================================
-- CORE LEDGER SYSTEM INDEXES
-- =====================================================

-- Daily Ledger indexes for date-based queries
CREATE INDEX idx_daily_ledger_financial_year ON daily_ledger(financial_year_year_id);
CREATE INDEX idx_daily_ledger_date ON daily_ledger(date);
CREATE INDEX idx_daily_ledger_date_financial_year ON daily_ledger(date, financial_year_year_id);

-- Ledger Details indexes for seller-based queries
CREATE INDEX idx_ledger_details_user ON ledger_details(user_id);
CREATE INDEX idx_ledger_details_daily_ledger ON ledger_details(daily_ledger_daily_ledger_id);
CREATE INDEX idx_ledger_details_user_daily_ledger ON ledger_details(user_id, daily_ledger_daily_ledger_id);

-- Ledger Record indexes for transaction queries
CREATE INDEX idx_ledger_record_buyer ON ledger_record(to_buyer_user_id);
CREATE INDEX idx_ledger_record_product ON ledger_record(product_product_id);
CREATE INDEX idx_ledger_record_ledger_details ON ledger_record(ledger_details_ledger_details_id);
CREATE INDEX idx_ledger_record_buyer_product ON ledger_record(to_buyer_user_id, product_product_id);
CREATE INDEX idx_ledger_record_brokerage ON ledger_record(total_brokerage);
CREATE INDEX idx_ledger_record_quantity ON ledger_record(quantity);

-- =====================================================
-- USER AND ADDRESS INDEXES
-- =====================================================

-- User indexes for filtering and joining
CREATE INDEX idx_user_address ON user(address_id);
CREATE INDEX idx_user_type ON user(user_type);
CREATE INDEX idx_user_firm_name ON user(firm_name);
CREATE INDEX idx_user_type_address ON user(user_type, address_id);
CREATE INDEX idx_user_total_brokerage ON user(total_payable_brokerage);

-- Address indexes for city-based analytics
CREATE INDEX idx_address_city ON address(city);
CREATE INDEX idx_address_city_area ON address(city, area);

-- Product indexes for product analytics
CREATE INDEX idx_product_name ON product(product_name);
CREATE INDEX idx_product_brokerage ON product(product_brokerage);

-- =====================================================
-- COMPOSITE INDEXES FOR ANALYTICS QUERIES
-- =====================================================

-- Composite index for monthly analytics query
CREATE INDEX idx_analytics_monthly ON daily_ledger(financial_year_year_id, date);

-- Composite index for product analytics
CREATE INDEX idx_product_analytics ON ledger_record(product_product_id, total_brokerage, quantity, total_products_cost);

-- Composite index for city analytics (requires join optimization)
CREATE INDEX idx_city_analytics_user ON user(address_id, user_type);

-- Composite index for merchant type analytics
CREATE INDEX idx_merchant_analytics ON user(user_type, total_payable_brokerage);

-- =====================================================
-- PAYMENT SYSTEM INDEXES (if not already created)
-- =====================================================

-- Ensure payment system indexes exist
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_broker_status ON brokerage_payment(broker_id, status);
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_broker_due_date ON brokerage_payment(broker_id, due_date);
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_merchant_status ON brokerage_payment(merchant_id, status);
CREATE INDEX IF NOT EXISTS idx_pending_payment_broker_status ON pending_payment(broker_id, status);
CREATE INDEX IF NOT EXISTS idx_receivable_payment_broker_status ON receivable_payment(broker_id, status);
CREATE INDEX IF NOT EXISTS idx_receivable_payment_seller_status ON receivable_payment(seller_id, status);

-- =====================================================
-- FINANCIAL YEAR INDEXES
-- =====================================================

-- Financial year indexes for date range queries
CREATE INDEX IF NOT EXISTS idx_financial_year_dates ON financial_year(start, end);
CREATE INDEX IF NOT EXISTS idx_financial_year_name ON financial_year(financial_year_name);

-- =====================================================
-- BROKER SYSTEM INDEXES
-- =====================================================

-- Broker indexes for authentication and queries
CREATE INDEX IF NOT EXISTS idx_broker_username ON broker(user_name);
CREATE INDEX IF NOT EXISTS idx_broker_email ON broker(email);
CREATE INDEX IF NOT EXISTS idx_broker_phone ON broker(phone_number);
CREATE INDEX IF NOT EXISTS idx_broker_firm_name ON broker(brokerage_firm_name);
CREATE INDEX IF NOT EXISTS idx_broker_address ON broker(address_id);

-- =====================================================
-- BANK DETAILS INDEXES
-- =====================================================

-- Bank details indexes for financial operations
CREATE INDEX IF NOT EXISTS idx_bank_details_account ON bank_details(account_number);
CREATE INDEX IF NOT EXISTS idx_bank_details_ifsc ON bank_details(ifsc_code);

-- =====================================================
-- PERFORMANCE MONITORING QUERIES
-- =====================================================

-- Query to check index usage
-- SELECT 
--     TABLE_NAME,
--     INDEX_NAME,
--     COLUMN_NAME,
--     CARDINALITY,
--     INDEX_TYPE
-- FROM INFORMATION_SCHEMA.STATISTICS 
-- WHERE TABLE_SCHEMA = 'brokerHub'
-- ORDER BY TABLE_NAME, INDEX_NAME;

-- Query to check table sizes after indexing
-- SELECT 
--     table_name AS 'Table',
--     ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)',
--     ROUND((index_length / 1024 / 1024), 2) AS 'Index Size (MB)',
--     ROUND((data_length / 1024 / 1024), 2) AS 'Data Size (MB)'
-- FROM information_schema.tables 
-- WHERE table_schema = 'brokerHub'
-- ORDER BY (data_length + index_length) DESC;

-- =====================================================
-- MAINTENANCE RECOMMENDATIONS
-- =====================================================

-- 1. Run ANALYZE TABLE periodically to update statistics
-- ANALYZE TABLE daily_ledger, ledger_details, ledger_record, user, address, product;

-- 2. Monitor slow query log
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 2;

-- 3. Use EXPLAIN to analyze query execution plans
-- EXPLAIN SELECT ... FROM daily_ledger WHERE ...;

-- =====================================================
-- END OF OPTIMIZATION SCRIPT
-- =====================================================
