-- =====================================================
-- MULTI-TENANT MIGRATION SCRIPT
-- =====================================================
-- This script converts the single-tenant brokerage application
-- to a multi-tenant system where each broker has isolated data.
-- 
-- IMPORTANT: Run this script AFTER backing up your database!
-- =====================================================

-- =====================================================
-- PHASE 1: ADD BROKER_ID TO CORE ENTITIES
-- =====================================================

-- 1. Add broker_id to USER table
ALTER TABLE user 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_user_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_user_broker ON user(broker_id);

-- 2. Add broker_id to PRODUCT table
ALTER TABLE product 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_product_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_product_broker ON product(broker_id);

-- 3. Add broker_id to ADDRESS table
ALTER TABLE address 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_address_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_address_broker ON address(broker_id);

-- 4. Add broker_id to BANK_DETAILS table
ALTER TABLE bank_details 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_bank_details_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_bank_details_broker ON bank_details(broker_id);

-- 5. Add broker_id to FINANCIAL_YEAR table
ALTER TABLE financial_year 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_financial_year_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_financial_year_broker ON financial_year(broker_id);

-- 6. Add broker_id to LEDGER_DETAILS table
ALTER TABLE ledger_details 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_ledger_details_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_ledger_details_broker ON ledger_details(broker_id);

-- 7. Add broker_id to DAILY_LEDGER table
ALTER TABLE daily_ledger 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_daily_ledger_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_daily_ledger_broker ON daily_ledger(broker_id);

-- 8. Add broker_id to LEDGER_RECORD table
ALTER TABLE ledger_record 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_ledger_record_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_ledger_record_broker ON ledger_record(broker_id);

-- =====================================================
-- PHASE 2: DATA MIGRATION FOR EXISTING RECORDS
-- =====================================================

-- Get the first broker ID (assuming there's at least one broker)
SET @default_broker_id = (SELECT MIN(broker_id) FROM broker LIMIT 1);

-- If no broker exists, create a default one
INSERT INTO broker (user_name, password, broker_name, brokerage_firm_name, email, phone_number, total_brokerage)
SELECT 'admin', '$2a$10$defaultpasswordhash', 'Default Broker', 'Default Firm', 'admin@brokerhub.com', '1234567890', 0.00
WHERE NOT EXISTS (SELECT 1 FROM broker);

-- Update the default_broker_id if we just created one
SET @default_broker_id = COALESCE(@default_broker_id, (SELECT MAX(broker_id) FROM broker));

-- Migrate existing data to default broker
UPDATE user SET broker_id = @default_broker_id WHERE broker_id IS NULL;
UPDATE product SET broker_id = @default_broker_id WHERE broker_id IS NULL;
UPDATE address SET broker_id = @default_broker_id WHERE broker_id IS NULL;
UPDATE bank_details SET broker_id = @default_broker_id WHERE broker_id IS NULL;
UPDATE financial_year SET broker_id = @default_broker_id WHERE broker_id IS NULL;
UPDATE ledger_details SET broker_id = @default_broker_id WHERE broker_id IS NULL;
UPDATE daily_ledger SET broker_id = @default_broker_id WHERE broker_id IS NULL;
UPDATE ledger_record SET broker_id = @default_broker_id WHERE broker_id IS NULL;

-- =====================================================
-- PHASE 3: MAKE BROKER_ID NOT NULL
-- =====================================================

-- Make broker_id NOT NULL for all tables
ALTER TABLE user MODIFY COLUMN broker_id BIGINT NOT NULL;
ALTER TABLE product MODIFY COLUMN broker_id BIGINT NOT NULL;
ALTER TABLE address MODIFY COLUMN broker_id BIGINT NOT NULL;
ALTER TABLE bank_details MODIFY COLUMN broker_id BIGINT NOT NULL;
ALTER TABLE financial_year MODIFY COLUMN broker_id BIGINT NOT NULL;
ALTER TABLE ledger_details MODIFY COLUMN broker_id BIGINT NOT NULL;
ALTER TABLE daily_ledger MODIFY COLUMN broker_id BIGINT NOT NULL;
ALTER TABLE ledger_record MODIFY COLUMN broker_id BIGINT NOT NULL;

-- =====================================================
-- PHASE 4: ADD COMPOSITE INDEXES FOR MULTI-TENANT QUERIES
-- =====================================================

-- User table indexes
CREATE INDEX idx_user_broker_firm_name ON user(broker_id, firm_name);
CREATE INDEX idx_user_broker_gst ON user(broker_id, gst_number);
CREATE INDEX idx_user_broker_type ON user(broker_id, user_type);

-- Product table indexes
CREATE INDEX idx_product_broker_name ON product(broker_id, product_name);
CREATE INDEX idx_product_broker_name_quality ON product(broker_id, product_name, quality);

-- Address table indexes
CREATE INDEX idx_address_broker_city ON address(broker_id, city);
CREATE INDEX idx_address_broker_city_area ON address(broker_id, city, area);

-- Financial year table indexes
CREATE INDEX idx_financial_year_broker_name ON financial_year(broker_id, financial_year_name);

-- Ledger table indexes
CREATE INDEX idx_ledger_details_broker_user ON ledger_details(broker_id, user_id);
CREATE INDEX idx_daily_ledger_broker_date ON daily_ledger(broker_id, ledger_date);

-- =====================================================
-- PHASE 5: UPDATE PAYMENT TRANSACTION TABLE
-- =====================================================

-- Add broker_id to payment_transaction if not exists
ALTER TABLE payment_transaction 
ADD COLUMN broker_id BIGINT,
ADD CONSTRAINT fk_payment_transaction_broker FOREIGN KEY (broker_id) REFERENCES broker(broker_id);

-- Create index for performance
CREATE INDEX idx_payment_transaction_broker ON payment_transaction(broker_id);

-- Migrate existing payment transactions
UPDATE payment_transaction pt
JOIN user u ON pt.seller_id = u.user_id
SET pt.broker_id = u.broker_id
WHERE pt.broker_id IS NULL;

-- Make broker_id NOT NULL
ALTER TABLE payment_transaction MODIFY COLUMN broker_id BIGINT NOT NULL;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Check that all records have broker_id assigned
-- SELECT 'user' as table_name, COUNT(*) as total_records, COUNT(broker_id) as with_broker_id FROM user
-- UNION ALL
-- SELECT 'product', COUNT(*), COUNT(broker_id) FROM product
-- UNION ALL
-- SELECT 'address', COUNT(*), COUNT(broker_id) FROM address
-- UNION ALL
-- SELECT 'bank_details', COUNT(*), COUNT(broker_id) FROM bank_details
-- UNION ALL
-- SELECT 'financial_year', COUNT(*), COUNT(broker_id) FROM financial_year
-- UNION ALL
-- SELECT 'ledger_details', COUNT(*), COUNT(broker_id) FROM ledger_details
-- UNION ALL
-- SELECT 'daily_ledger', COUNT(*), COUNT(broker_id) FROM daily_ledger
-- UNION ALL
-- SELECT 'payment_transaction', COUNT(*), COUNT(broker_id) FROM payment_transaction;

-- =====================================================
-- ROLLBACK SCRIPT (EMERGENCY USE ONLY)
-- =====================================================

-- UNCOMMENT ONLY IF YOU NEED TO ROLLBACK THE MIGRATION
-- WARNING: This will remove all broker_id columns and constraints

/*
-- Remove foreign key constraints first
ALTER TABLE user DROP FOREIGN KEY fk_user_broker;
ALTER TABLE product DROP FOREIGN KEY fk_product_broker;
ALTER TABLE address DROP FOREIGN KEY fk_address_broker;
ALTER TABLE bank_details DROP FOREIGN KEY fk_bank_details_broker;
ALTER TABLE financial_year DROP FOREIGN KEY fk_financial_year_broker;
ALTER TABLE ledger_details DROP FOREIGN KEY fk_ledger_details_broker;
ALTER TABLE daily_ledger DROP FOREIGN KEY fk_daily_ledger_broker;
ALTER TABLE ledger_record DROP FOREIGN KEY fk_ledger_record_broker;
ALTER TABLE payment_transaction DROP FOREIGN KEY fk_payment_transaction_broker;

-- Drop indexes
DROP INDEX idx_user_broker ON user;
DROP INDEX idx_product_broker ON product;
DROP INDEX idx_address_broker ON address;
DROP INDEX idx_bank_details_broker ON bank_details;
DROP INDEX idx_financial_year_broker ON financial_year;
DROP INDEX idx_ledger_details_broker ON ledger_details;
DROP INDEX idx_daily_ledger_broker ON daily_ledger;
DROP INDEX idx_ledger_record_broker ON ledger_record;
DROP INDEX idx_payment_transaction_broker ON payment_transaction;

-- Remove broker_id columns
ALTER TABLE user DROP COLUMN broker_id;
ALTER TABLE product DROP COLUMN broker_id;
ALTER TABLE address DROP COLUMN broker_id;
ALTER TABLE bank_details DROP COLUMN broker_id;
ALTER TABLE financial_year DROP COLUMN broker_id;
ALTER TABLE ledger_details DROP COLUMN broker_id;
ALTER TABLE daily_ledger DROP COLUMN broker_id;
ALTER TABLE ledger_record DROP COLUMN broker_id;
ALTER TABLE payment_transaction DROP COLUMN broker_id;
*/

-- =====================================================
-- END OF MIGRATION SCRIPT
-- =====================================================
