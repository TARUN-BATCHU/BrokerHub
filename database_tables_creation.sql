-- =====================================================
-- BROKERAGE PAYMENT SYSTEM DATABASE TABLES
-- =====================================================
-- This file contains SQL scripts to create all required tables
-- for the comprehensive payment management system.
-- 
-- Execute these scripts in your MySQL database to set up
-- the payment system tables.
-- =====================================================

-- 1. BROKERAGE PAYMENT TABLE
-- Tracks brokerage payments that merchants owe to brokers
CREATE TABLE IF NOT EXISTS brokerage_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    broker_id BIGINT NOT NULL,
    sold_bags BIGINT DEFAULT 0,
    bought_bags BIGINT DEFAULT 0,
    total_bags BIGINT DEFAULT 0,
    brokerage_rate DECIMAL(10,2) DEFAULT 0.00,
    gross_brokerage DECIMAL(15,2) DEFAULT 0.00,
    discount DECIMAL(15,2) DEFAULT 0.00,
    tds DECIMAL(15,2) DEFAULT 0.00,
    net_brokerage DECIMAL(15,2) DEFAULT 0.00,
    paid_amount DECIMAL(15,2) DEFAULT 0.00,
    pending_amount DECIMAL(15,2) DEFAULT 0.00,
    last_payment_date DATE,
    due_date DATE,
    status ENUM('PENDING', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'DUE_SOON') DEFAULT 'PENDING',
    financial_year_id BIGINT,
    created_date DATE NOT NULL,
    updated_date DATE NOT NULL,
    notes TEXT,
    
    -- Foreign key constraints
    CONSTRAINT fk_brokerage_payment_merchant FOREIGN KEY (merchant_id) REFERENCES user(user_id),
    CONSTRAINT fk_brokerage_payment_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    CONSTRAINT fk_brokerage_payment_financial_year FOREIGN KEY (financial_year_id) REFERENCES financial_year(year_id),
    
    -- Indexes for performance
    INDEX idx_brokerage_payment_broker (broker_id),
    INDEX idx_brokerage_payment_merchant (merchant_id),
    INDEX idx_brokerage_payment_status (status),
    INDEX idx_brokerage_payment_due_date (due_date),
    INDEX idx_brokerage_payment_pending_amount (pending_amount),
    INDEX idx_brokerage_payment_financial_year (financial_year_id)
);

-- 2. PART PAYMENT TABLE
-- Tracks partial payments made towards brokerage payments
CREATE TABLE IF NOT EXISTS part_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_reference VARCHAR(50) UNIQUE,
    brokerage_payment_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    payment_date DATE NOT NULL,
    method ENUM('CASH', 'BANK_TRANSFER', 'CHEQUE', 'UPI', 'NEFT', 'RTGS', 'ONLINE', 'OTHER') NOT NULL,
    notes TEXT,
    transaction_reference VARCHAR(100),
    bank_details VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    recorded_by VARCHAR(100),
    verified BOOLEAN DEFAULT FALSE,
    verified_date DATE,
    verified_by VARCHAR(100),
    
    -- Foreign key constraints
    CONSTRAINT fk_part_payment_brokerage FOREIGN KEY (brokerage_payment_id) REFERENCES brokerage_payment(id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_part_payment_brokerage (brokerage_payment_id),
    INDEX idx_part_payment_date (payment_date),
    INDEX idx_part_payment_method (method),
    INDEX idx_part_payment_verified (verified),
    INDEX idx_part_payment_reference (payment_reference)
);

-- 3. PENDING PAYMENT TABLE
-- Tracks payments that buyers owe to sellers
CREATE TABLE IF NOT EXISTS pending_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    broker_id BIGINT NOT NULL,
    total_pending_amount DECIMAL(15,2) DEFAULT 0.00,
    transaction_count INT DEFAULT 0,
    oldest_transaction_date DATE,
    due_date DATE,
    status ENUM('PENDING', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'DUE_SOON') DEFAULT 'PENDING',
    financial_year_id BIGINT,
    created_date DATE NOT NULL,
    updated_date DATE NOT NULL,
    notes TEXT,
    
    -- Foreign key constraints
    CONSTRAINT fk_pending_payment_buyer FOREIGN KEY (buyer_id) REFERENCES user(user_id),
    CONSTRAINT fk_pending_payment_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    CONSTRAINT fk_pending_payment_financial_year FOREIGN KEY (financial_year_id) REFERENCES financial_year(year_id),
    
    -- Indexes for performance
    INDEX idx_pending_payment_buyer (buyer_id),
    INDEX idx_pending_payment_broker (broker_id),
    INDEX idx_pending_payment_status (status),
    INDEX idx_pending_payment_due_date (due_date),
    INDEX idx_pending_payment_amount (total_pending_amount),
    INDEX idx_pending_payment_financial_year (financial_year_id)
);

-- 4. RECEIVABLE PAYMENT TABLE
-- Tracks payments that sellers are owed by buyers
CREATE TABLE IF NOT EXISTS receivable_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    broker_id BIGINT NOT NULL,
    total_receivable_amount DECIMAL(15,2) DEFAULT 0.00,
    transaction_count INT DEFAULT 0,
    oldest_transaction_date DATE,
    due_date DATE,
    status ENUM('PENDING', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'DUE_SOON') DEFAULT 'PENDING',
    financial_year_id BIGINT,
    created_date DATE NOT NULL,
    updated_date DATE NOT NULL,
    notes TEXT,
    
    -- Foreign key constraints
    CONSTRAINT fk_receivable_payment_seller FOREIGN KEY (seller_id) REFERENCES user(user_id),
    CONSTRAINT fk_receivable_payment_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    CONSTRAINT fk_receivable_payment_financial_year FOREIGN KEY (financial_year_id) REFERENCES financial_year(year_id),
    
    -- Indexes for performance
    INDEX idx_receivable_payment_seller (seller_id),
    INDEX idx_receivable_payment_broker (broker_id),
    INDEX idx_receivable_payment_status (status),
    INDEX idx_receivable_payment_due_date (due_date),
    INDEX idx_receivable_payment_amount (total_receivable_amount),
    INDEX idx_receivable_payment_financial_year (financial_year_id)
);

-- 5. PAYMENT TRANSACTION TABLE
-- Tracks individual transactions between merchants
CREATE TABLE IF NOT EXISTS payment_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE,
    transaction_date DATE NOT NULL,
    seller_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quality VARCHAR(100),
    bags BIGINT DEFAULT 0,
    rate_per_bag DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(15,2) DEFAULT 0.00,
    paid_amount DECIMAL(15,2) DEFAULT 0.00,
    pending_amount DECIMAL(15,2) DEFAULT 0.00,
    due_date DATE,
    status ENUM('PENDING', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'DUE_SOON') DEFAULT 'PENDING',
    pending_payment_id BIGINT,
    receivable_transaction_id BIGINT,
    financial_year_id BIGINT,
    created_date DATE NOT NULL,
    updated_date DATE NOT NULL,
    notes TEXT,
    
    -- Foreign key constraints
    CONSTRAINT fk_payment_transaction_seller FOREIGN KEY (seller_id) REFERENCES user(user_id),
    CONSTRAINT fk_payment_transaction_buyer FOREIGN KEY (buyer_id) REFERENCES user(user_id),
    CONSTRAINT fk_payment_transaction_product FOREIGN KEY (product_id) REFERENCES product(product_id),
    CONSTRAINT fk_payment_transaction_pending FOREIGN KEY (pending_payment_id) REFERENCES pending_payment(id),
    CONSTRAINT fk_payment_transaction_receivable FOREIGN KEY (receivable_transaction_id) REFERENCES receivable_transaction(id),
    CONSTRAINT fk_payment_transaction_financial_year FOREIGN KEY (financial_year_id) REFERENCES financial_year(year_id),
    
    -- Indexes for performance
    INDEX idx_payment_transaction_seller (seller_id),
    INDEX idx_payment_transaction_buyer (buyer_id),
    INDEX idx_payment_transaction_product (product_id),
    INDEX idx_payment_transaction_date (transaction_date),
    INDEX idx_payment_transaction_status (status),
    INDEX idx_payment_transaction_due_date (due_date),
    INDEX idx_payment_transaction_pending_amount (pending_amount),
    INDEX idx_payment_transaction_id_unique (transaction_id)
);

-- 6. RECEIVABLE TRANSACTION TABLE
-- Groups transactions by buyer for receivable payments
CREATE TABLE IF NOT EXISTS receivable_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    total_owed DECIMAL(15,2) DEFAULT 0.00,
    receivable_payment_id BIGINT NOT NULL,
    created_date DATE NOT NULL,
    updated_date DATE NOT NULL,
    
    -- Foreign key constraints
    CONSTRAINT fk_receivable_transaction_buyer FOREIGN KEY (buyer_id) REFERENCES user(user_id),
    CONSTRAINT fk_receivable_transaction_receivable FOREIGN KEY (receivable_payment_id) REFERENCES receivable_payment(id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_receivable_transaction_buyer (buyer_id),
    INDEX idx_receivable_transaction_receivable (receivable_payment_id),
    INDEX idx_receivable_transaction_total_owed (total_owed)
);

-- =====================================================
-- SAMPLE DATA INSERTION (OPTIONAL)
-- =====================================================
-- Uncomment the following section if you want to insert sample data for testing

/*
-- Sample brokerage payment data
INSERT INTO brokerage_payment (
    merchant_id, broker_id, sold_bags, bought_bags, total_bags, 
    brokerage_rate, gross_brokerage, discount, tds, net_brokerage, 
    paid_amount, pending_amount, due_date, status, created_date, updated_date
) VALUES 
(1, 1, 80, 70, 150, 10.00, 1500.00, 150.00, 75.00, 1275.00, 500.00, 775.00, '2024-02-15', 'PARTIAL_PAID', CURDATE(), CURDATE()),
(2, 1, 100, 50, 150, 10.00, 1500.00, 150.00, 75.00, 1275.00, 0.00, 1275.00, '2024-02-20', 'PENDING', CURDATE(), CURDATE());

-- Sample part payment data
INSERT INTO part_payment (
    payment_reference, brokerage_payment_id, amount, payment_date, 
    method, notes, recorded_by, verified
) VALUES 
('PP001', 1, 500.00, '2024-01-15', 'CASH', 'Partial payment received', 'admin', TRUE);

-- Sample pending payment data
INSERT INTO pending_payment (
    buyer_id, broker_id, total_pending_amount, transaction_count, 
    oldest_transaction_date, due_date, status, created_date, updated_date
) VALUES 
(2, 1, 850000.00, 2, '2024-01-10', '2024-02-10', 'OVERDUE', CURDATE(), CURDATE());

-- Sample receivable payment data
INSERT INTO receivable_payment (
    seller_id, broker_id, total_receivable_amount, transaction_count, 
    oldest_transaction_date, due_date, status, created_date, updated_date
) VALUES 
(1, 1, 400000.00, 1, '2024-01-10', '2024-02-10', 'OVERDUE', CURDATE(), CURDATE());
*/

-- =====================================================
-- PERFORMANCE OPTIMIZATION QUERIES
-- =====================================================

-- Create composite indexes for better query performance
CREATE INDEX idx_brokerage_payment_broker_status ON brokerage_payment(broker_id, status);
CREATE INDEX idx_brokerage_payment_broker_due_date ON brokerage_payment(broker_id, due_date);
CREATE INDEX idx_pending_payment_broker_status ON pending_payment(broker_id, status);
CREATE INDEX idx_receivable_payment_broker_status ON receivable_payment(broker_id, status);

-- =====================================================
-- MAINTENANCE QUERIES
-- =====================================================

-- Query to check table sizes
-- SELECT 
--     table_name AS 'Table',
--     ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
-- FROM information_schema.tables 
-- WHERE table_schema = 'brokerHub' 
-- AND table_name IN ('brokerage_payment', 'part_payment', 'pending_payment', 'receivable_payment', 'payment_transaction', 'receivable_transaction')
-- ORDER BY (data_length + index_length) DESC;

-- Query to check foreign key constraints
-- SELECT 
--     TABLE_NAME,
--     COLUMN_NAME,
--     CONSTRAINT_NAME,
--     REFERENCED_TABLE_NAME,
--     REFERENCED_COLUMN_NAME
-- FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
-- WHERE REFERENCED_TABLE_SCHEMA = 'brokerHub'
-- AND TABLE_NAME IN ('brokerage_payment', 'part_payment', 'pending_payment', 'receivable_payment', 'payment_transaction', 'receivable_transaction');

-- =====================================================
-- END OF SCRIPT
-- =====================================================
