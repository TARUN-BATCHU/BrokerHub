-- =====================================================
-- MULTI-TENANT BROKERAGE APPLICATION DATABASE SETUP
-- =====================================================
-- This script creates a new database for the multi-tenant version
-- and sets up all required tables with broker relationships.
-- =====================================================

-- Create the new database for multi-tenant version
CREATE DATABASE IF NOT EXISTS brokerHub_multiTenant;
USE brokerHub_multiTenant;

-- =====================================================
-- CORE TABLES WITH MULTI-TENANT SUPPORT
-- =====================================================

-- 1. BROKER TABLE (Main tenant table)
CREATE TABLE IF NOT EXISTS broker (
    broker_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    broker_name VARCHAR(255) NOT NULL,
    brokerage_firm_name VARCHAR(255) NOT NULL,
    address_id BIGINT,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    total_brokerage DECIMAL(19,2) DEFAULT 0.00,
    bank_details_bank_details_id BIGINT,
    otp INT,
    otp_generated_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. ADDRESS TABLE (with broker isolation)
CREATE TABLE IF NOT EXISTS address (
    address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    city VARCHAR(50) NOT NULL,
    area VARCHAR(255),
    pincode VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_address_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    INDEX idx_address_broker (broker_id),
    INDEX idx_address_broker_city (broker_id, city),
    INDEX idx_address_broker_pincode (broker_id, pincode)
);

-- 3. BANK_DETAILS TABLE (with broker isolation)
CREATE TABLE IF NOT EXISTS bank_details (
    bank_details_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    bank_name VARCHAR(255),
    account_number VARCHAR(255),
    ifsc_code VARCHAR(255),
    branch VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_bank_details_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    INDEX idx_bank_details_broker (broker_id),
    INDEX idx_bank_details_account (broker_id, account_number)
);

-- 4. FINANCIAL_YEAR TABLE (with broker isolation)
CREATE TABLE IF NOT EXISTS financial_year (
    year_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    start DATE NOT NULL,
    end DATE NOT NULL,
    financial_year_name VARCHAR(255),
    for_bills BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_financial_year_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    INDEX idx_financial_year_broker (broker_id),
    INDEX idx_financial_year_broker_name (broker_id, financial_year_name)
);

-- 5. PRODUCT TABLE (with broker isolation)
CREATE TABLE IF NOT EXISTS product (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_brokerage FLOAT NOT NULL DEFAULT 0.0,
    quantity INT DEFAULT 0,
    price INT DEFAULT 0,
    quality VARCHAR(255),
    img_link VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_product_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    INDEX idx_product_broker (broker_id),
    INDEX idx_product_broker_name (broker_id, product_name),
    INDEX idx_product_broker_name_quality (broker_id, product_name, quality)
);

-- 6. USER TABLE (with broker isolation and inheritance)
CREATE TABLE IF NOT EXISTS user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    user_type VARCHAR(31) NOT NULL,
    gst_number VARCHAR(255),
    firm_name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255),
    address_id BIGINT NOT NULL,
    email VARCHAR(255),
    bank_details_id BIGINT,
    phone_numbers JSON,
    brokerage_rate INT DEFAULT 0,
    total_bags_sold BIGINT DEFAULT 0,
    total_bags_bought BIGINT DEFAULT 0,
    payable_amount BIGINT DEFAULT 0,
    receivable_amount BIGINT DEFAULT 0,
    total_payable_brokerage DECIMAL(19,2) DEFAULT 0.00,
    shop_number VARCHAR(255),
    address_hint VARCHAR(255),
    collection_rote VARCHAR(255),
    by_product VARCHAR(255), -- For Miller subclass
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    CONSTRAINT fk_user_address FOREIGN KEY (address_id) REFERENCES address(address_id),
    CONSTRAINT fk_user_bank_details FOREIGN KEY (bank_details_id) REFERENCES bank_details(bank_details_id),
    
    INDEX idx_user_broker (broker_id),
    INDEX idx_user_broker_firm_name (broker_id, firm_name),
    INDEX idx_user_broker_gst (broker_id, gst_number),
    INDEX idx_user_broker_type (broker_id, user_type)
);

-- 7. DAILY_LEDGER TABLE (with broker isolation)
CREATE TABLE IF NOT EXISTS daily_ledger (
    daily_ledger_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    date DATE NOT NULL,
    financial_year_year_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_daily_ledger_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    CONSTRAINT fk_daily_ledger_financial_year FOREIGN KEY (financial_year_year_id) REFERENCES financial_year(year_id),
    
    INDEX idx_daily_ledger_broker (broker_id),
    INDEX idx_daily_ledger_broker_date (broker_id, date),
    UNIQUE KEY uk_daily_ledger_broker_date (broker_id, date)
);

-- 8. LEDGER_DETAILS TABLE (with broker isolation)
CREATE TABLE IF NOT EXISTS ledger_details (
    ledger_details_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    daily_ledger_daily_ledger_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ledger_details_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    CONSTRAINT fk_ledger_details_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_ledger_details_daily_ledger FOREIGN KEY (daily_ledger_daily_ledger_id) REFERENCES daily_ledger(daily_ledger_id),
    
    INDEX idx_ledger_details_broker (broker_id),
    INDEX idx_ledger_details_broker_user (broker_id, user_id)
);

-- 9. LEDGER_RECORD TABLE (with broker isolation)
CREATE TABLE IF NOT EXISTS ledger_record (
    ledger_record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    ledger_details_ledger_details_id BIGINT,
    to_buyer_user_id BIGINT NOT NULL,
    product_product_id BIGINT,
    quantity BIGINT DEFAULT 0,
    brokerage BIGINT NOT NULL DEFAULT 0,
    product_cost BIGINT DEFAULT 0,
    total_products_cost BIGINT DEFAULT 0,
    total_brokerage BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ledger_record_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    CONSTRAINT fk_ledger_record_ledger_details FOREIGN KEY (ledger_details_ledger_details_id) REFERENCES ledger_details(ledger_details_id),
    CONSTRAINT fk_ledger_record_buyer FOREIGN KEY (to_buyer_user_id) REFERENCES user(user_id),
    CONSTRAINT fk_ledger_record_product FOREIGN KEY (product_product_id) REFERENCES product(product_id),
    
    INDEX idx_ledger_record_broker (broker_id)
);

-- =====================================================
-- PAYMENT SYSTEM TABLES (Already have broker_id)
-- =====================================================

-- Include the existing payment tables from database_tables_creation.sql
-- These already have broker_id fields

-- 10. BROKERAGE PAYMENT TABLE
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
    
    CONSTRAINT fk_brokerage_payment_merchant FOREIGN KEY (merchant_id) REFERENCES user(user_id),
    CONSTRAINT fk_brokerage_payment_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id),
    CONSTRAINT fk_brokerage_payment_financial_year FOREIGN KEY (financial_year_id) REFERENCES financial_year(year_id),
    
    INDEX idx_brokerage_payment_broker (broker_id),
    INDEX idx_brokerage_payment_merchant (merchant_id),
    INDEX idx_brokerage_payment_status (status),
    INDEX idx_brokerage_payment_due_date (due_date)
);

-- =====================================================
-- SAMPLE DATA FOR TESTING
-- =====================================================

-- Insert a default broker for testing
INSERT INTO broker (user_name, password, broker_name, brokerage_firm_name, email, phone_number, total_brokerage) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYrONgrkHuq6Zu', 'Admin Broker', 'Default Brokerage Firm', 'admin@brokerhub.com', '1234567890', 0.00);

-- Insert a sample address for the broker
INSERT INTO address (broker_id, city, area, pincode) 
VALUES (1, 'Mumbai', 'Andheri', '400001');

-- Insert a sample financial year for the broker
INSERT INTO financial_year (broker_id, start, end, financial_year_name, for_bills) 
VALUES (1, '2024-04-01', '2025-03-31', '2024-25', TRUE);

-- Insert a sample product for the broker
INSERT INTO product (broker_id, product_name, product_brokerage, quantity, price, quality) 
VALUES (1, 'Rice', 10.0, 100, 50, 'Grade A');

-- =====================================================
-- PERFORMANCE INDEXES
-- =====================================================

-- Additional composite indexes for better query performance
CREATE INDEX idx_user_broker_firm_gst ON user(broker_id, firm_name, gst_number);
CREATE INDEX idx_product_broker_name_quality_qty ON product(broker_id, product_name, quality, quantity);
CREATE INDEX idx_address_broker_city_area_pin ON address(broker_id, city, area, pincode);

-- =====================================================
-- END OF SETUP SCRIPT
-- =====================================================
