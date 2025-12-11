-- Brokerage Dashboard Database Migration Script
-- This script ensures all required tables and relationships are properly set up

-- Ensure BrokeragePayment table exists with all required columns
CREATE TABLE IF NOT EXISTS brokerage_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    broker_id BIGINT NOT NULL,
    sold_bags BIGINT DEFAULT 0,
    bought_bags BIGINT DEFAULT 0,
    total_bags BIGINT DEFAULT 0,
    brokerage_rate DECIMAL(10,2) DEFAULT 0.00,
    gross_brokerage DECIMAL(10,2) DEFAULT 0.00,
    discount DECIMAL(10,2) DEFAULT 0.00,
    tds DECIMAL(10,2) DEFAULT 0.00,
    net_brokerage DECIMAL(10,2) DEFAULT 0.00,
    paid_amount DECIMAL(10,2) DEFAULT 0.00,
    pending_amount DECIMAL(10,2) DEFAULT 0.00,
    last_payment_date DATE,
    due_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING',
    financial_year_id BIGINT,
    created_date DATE DEFAULT (CURRENT_DATE),
    updated_date DATE DEFAULT (CURRENT_DATE),
    notes TEXT,
    
    FOREIGN KEY (merchant_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (broker_id) REFERENCES broker(broker_id) ON DELETE CASCADE,
    FOREIGN KEY (financial_year_id) REFERENCES financial_year(year_id) ON DELETE SET NULL,
    
    INDEX idx_broker_merchant (broker_id, merchant_id),
    INDEX idx_broker_status (broker_id, status),
    INDEX idx_due_date (due_date),
    INDEX idx_financial_year (financial_year_id)
);

-- Ensure PartPayment table exists with all required columns
CREATE TABLE IF NOT EXISTS part_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_reference VARCHAR(50) UNIQUE,
    brokerage_payment_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    method VARCHAR(20) NOT NULL,
    notes TEXT,
    transaction_reference VARCHAR(100),
    bank_details VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    recorded_by VARCHAR(100),
    verified BOOLEAN DEFAULT FALSE,
    verified_date DATE,
    verified_by VARCHAR(100),
    
    FOREIGN KEY (brokerage_payment_id) REFERENCES brokerage_payment(id) ON DELETE CASCADE,
    
    INDEX idx_brokerage_payment (brokerage_payment_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_method (method),
    INDEX idx_verified (verified)
);

-- Add missing columns to existing tables if they don't exist
ALTER TABLE brokerage_payment 
ADD COLUMN IF NOT EXISTS sold_bags BIGINT DEFAULT 0,
ADD COLUMN IF NOT EXISTS bought_bags BIGINT DEFAULT 0,
ADD COLUMN IF NOT EXISTS total_bags BIGINT DEFAULT 0,
ADD COLUMN IF NOT EXISTS brokerage_rate DECIMAL(10,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS gross_brokerage DECIMAL(10,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS discount DECIMAL(10,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS tds DECIMAL(10,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS created_date DATE DEFAULT (CURRENT_DATE),
ADD COLUMN IF NOT EXISTS updated_date DATE DEFAULT (CURRENT_DATE);

-- Add missing columns to part_payment table if they don't exist
ALTER TABLE part_payment 
ADD COLUMN IF NOT EXISTS payment_reference VARCHAR(50),
ADD COLUMN IF NOT EXISTS transaction_reference VARCHAR(100),
ADD COLUMN IF NOT EXISTS bank_details VARCHAR(200),
ADD COLUMN IF NOT EXISTS recorded_by VARCHAR(100),
ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS verified_date DATE,
ADD COLUMN IF NOT EXISTS verified_by VARCHAR(100);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_broker_merchant ON brokerage_payment(broker_id, merchant_id);
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_status ON brokerage_payment(broker_id, status);
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_due_date ON brokerage_payment(due_date);
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_pending ON brokerage_payment(broker_id, pending_amount);

CREATE INDEX IF NOT EXISTS idx_part_payment_brokerage ON part_payment(brokerage_payment_id);
CREATE INDEX IF NOT EXISTS idx_part_payment_date ON part_payment(payment_date);
CREATE INDEX IF NOT EXISTS idx_part_payment_method ON part_payment(method);

-- Update existing records to ensure data consistency
UPDATE brokerage_payment 
SET 
    created_date = COALESCE(created_date, CURRENT_DATE),
    updated_date = COALESCE(updated_date, CURRENT_DATE),
    paid_amount = COALESCE(paid_amount, 0.00),
    pending_amount = COALESCE(net_brokerage, 0.00) - COALESCE(paid_amount, 0.00)
WHERE created_date IS NULL OR updated_date IS NULL OR pending_amount IS NULL;

-- Ensure payment references are generated for existing part payments
UPDATE part_payment 
SET payment_reference = CONCAT('PP', DATE_FORMAT(created_at, '%Y%m%d%H%i%s'), LPAD(id, 6, '0'))
WHERE payment_reference IS NULL OR payment_reference = '';

-- Create a view for dashboard summary (optional, for better performance)
CREATE OR REPLACE VIEW brokerage_dashboard_summary AS
SELECT 
    bp.broker_id,
    COUNT(*) as total_merchants,
    SUM(bp.net_brokerage) as total_brokerage_receivable,
    SUM(bp.paid_amount) as total_brokerage_received,
    SUM(bp.pending_amount) as total_brokerage_pending,
    SUM(CASE WHEN bp.status = 'PAID' THEN 1 ELSE 0 END) as paid_merchants,
    SUM(CASE WHEN bp.status = 'PARTIAL_PAID' THEN 1 ELSE 0 END) as partial_paid_merchants,
    SUM(CASE WHEN bp.status IN ('PENDING', 'OVERDUE', 'DUE_SOON') THEN 1 ELSE 0 END) as pending_merchants
FROM brokerage_payment bp
GROUP BY bp.broker_id;

-- Insert sample data for testing (optional - remove in production)
-- This will only insert if no brokerage payments exist for the broker
/*
INSERT IGNORE INTO brokerage_payment (
    merchant_id, broker_id, sold_bags, bought_bags, total_bags, 
    brokerage_rate, gross_brokerage, discount, tds, net_brokerage, 
    paid_amount, pending_amount, status, due_date, financial_year_id
)
SELECT 
    u.user_id,
    u.broker_id,
    FLOOR(RAND() * 100) + 10 as sold_bags,
    FLOOR(RAND() * 50) + 5 as bought_bags,
    FLOOR(RAND() * 150) + 15 as total_bags,
    COALESCE(u.brokerage_rate, 2.00) as brokerage_rate,
    (FLOOR(RAND() * 150) + 15) * COALESCE(u.brokerage_rate, 2.00) as gross_brokerage,
    (FLOOR(RAND() * 150) + 15) * COALESCE(u.brokerage_rate, 2.00) * 0.10 as discount,
    (FLOOR(RAND() * 150) + 15) * COALESCE(u.brokerage_rate, 2.00) * 0.05 as tds,
    (FLOOR(RAND() * 150) + 15) * COALESCE(u.brokerage_rate, 2.00) * 0.85 as net_brokerage,
    0.00 as paid_amount,
    (FLOOR(RAND() * 150) + 15) * COALESCE(u.brokerage_rate, 2.00) * 0.85 as pending_amount,
    'PENDING' as status,
    DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY) as due_date,
    (SELECT year_id FROM financial_year WHERE is_current = 1 LIMIT 1) as financial_year_id
FROM user u 
WHERE u.broker_id IS NOT NULL 
AND NOT EXISTS (
    SELECT 1 FROM brokerage_payment bp 
    WHERE bp.merchant_id = u.user_id AND bp.broker_id = u.broker_id
)
LIMIT 10;
*/

COMMIT;