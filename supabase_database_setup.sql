-- =====================================================
-- BROKERAGE PAYMENT SYSTEM DATABASE TABLES - POSTGRESQL VERSION
-- =====================================================
-- Execute this in Supabase SQL Editor
-- =====================================================

-- First, let's create the core tables that your payment tables depend on

-- 1. BROKER TABLE (if not exists)
CREATE TABLE IF NOT EXISTS broker (
    broker_id BIGSERIAL PRIMARY KEY,
    broker_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    created_date DATE DEFAULT CURRENT_DATE,
    updated_date DATE DEFAULT CURRENT_DATE
);

-- 2. USER TABLE (if not exists) 
CREATE TABLE IF NOT EXISTS "user" (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    user_type VARCHAR(50),
    broker_id BIGINT REFERENCES broker(broker_id),
    created_date DATE DEFAULT CURRENT_DATE,
    updated_date DATE DEFAULT CURRENT_DATE
);

-- 3. PRODUCT TABLE (if not exists)
CREATE TABLE IF NOT EXISTS product (
    product_id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    unit VARCHAR(50),
    broker_id BIGINT REFERENCES broker(broker_id),
    created_date DATE DEFAULT CURRENT_DATE
);

-- 4. FINANCIAL YEAR TABLE (if not exists)
CREATE TABLE IF NOT EXISTS financial_year (
    year_id BIGSERIAL PRIMARY KEY,
    year_name VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    broker_id BIGINT REFERENCES broker(broker_id)
);

-- 5. BROKERAGE PAYMENT TABLE
CREATE TABLE IF NOT EXISTS brokerage_payment (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT NOT NULL REFERENCES "user"(user_id),
    broker_id BIGINT NOT NULL REFERENCES broker(broker_id),
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
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'DUE_SOON')),
    financial_year_id BIGINT REFERENCES financial_year(year_id),
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_date DATE NOT NULL DEFAULT CURRENT_DATE,
    notes TEXT
);

-- 6. PART PAYMENT TABLE
CREATE TABLE IF NOT EXISTS part_payment (
    id BIGSERIAL PRIMARY KEY,
    payment_reference VARCHAR(50) UNIQUE,
    brokerage_payment_id BIGINT NOT NULL REFERENCES brokerage_payment(id) ON DELETE CASCADE,
    amount DECIMAL(15,2) NOT NULL,
    payment_date DATE NOT NULL,
    method VARCHAR(20) NOT NULL CHECK (method IN ('CASH', 'BANK_TRANSFER', 'CHEQUE', 'UPI', 'NEFT', 'RTGS', 'ONLINE', 'OTHER')),
    notes TEXT,
    transaction_reference VARCHAR(100),
    bank_details VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recorded_by VARCHAR(100),
    verified BOOLEAN DEFAULT FALSE,
    verified_date DATE,
    verified_by VARCHAR(100)
);

-- 7. PENDING PAYMENT TABLE
CREATE TABLE IF NOT EXISTS pending_payment (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL REFERENCES "user"(user_id),
    broker_id BIGINT NOT NULL REFERENCES broker(broker_id),
    total_pending_amount DECIMAL(15,2) DEFAULT 0.00,
    transaction_count INTEGER DEFAULT 0,
    oldest_transaction_date DATE,
    due_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'DUE_SOON')),
    financial_year_id BIGINT REFERENCES financial_year(year_id),
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_date DATE NOT NULL DEFAULT CURRENT_DATE,
    notes TEXT
);

-- 8. RECEIVABLE PAYMENT TABLE
CREATE TABLE IF NOT EXISTS receivable_payment (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES "user"(user_id),
    broker_id BIGINT NOT NULL REFERENCES broker(broker_id),
    total_receivable_amount DECIMAL(15,2) DEFAULT 0.00,
    transaction_count INTEGER DEFAULT 0,
    oldest_transaction_date DATE,
    due_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'DUE_SOON')),
    financial_year_id BIGINT REFERENCES financial_year(year_id),
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_date DATE NOT NULL DEFAULT CURRENT_DATE,
    notes TEXT
);

-- 9. RECEIVABLE TRANSACTION TABLE
CREATE TABLE IF NOT EXISTS receivable_transaction (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL REFERENCES "user"(user_id),
    total_owed DECIMAL(15,2) DEFAULT 0.00,
    receivable_payment_id BIGINT NOT NULL REFERENCES receivable_payment(id) ON DELETE CASCADE,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_date DATE NOT NULL DEFAULT CURRENT_DATE
);

-- 10. PAYMENT TRANSACTION TABLE
CREATE TABLE IF NOT EXISTS payment_transaction (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE,
    transaction_date DATE NOT NULL,
    seller_id BIGINT NOT NULL REFERENCES "user"(user_id),
    buyer_id BIGINT NOT NULL REFERENCES "user"(user_id),
    product_id BIGINT NOT NULL REFERENCES product(product_id),
    quality VARCHAR(100),
    bags BIGINT DEFAULT 0,
    rate_per_bag DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(15,2) DEFAULT 0.00,
    paid_amount DECIMAL(15,2) DEFAULT 0.00,
    pending_amount DECIMAL(15,2) DEFAULT 0.00,
    due_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'DUE_SOON')),
    pending_payment_id BIGINT REFERENCES pending_payment(id),
    receivable_transaction_id BIGINT REFERENCES receivable_transaction(id),
    financial_year_id BIGINT REFERENCES financial_year(year_id),
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_date DATE NOT NULL DEFAULT CURRENT_DATE,
    notes TEXT
);

-- CREATE INDEXES FOR PERFORMANCE
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_broker ON brokerage_payment(broker_id);
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_merchant ON brokerage_payment(merchant_id);
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_status ON brokerage_payment(status);
CREATE INDEX IF NOT EXISTS idx_brokerage_payment_due_date ON brokerage_payment(due_date);

CREATE INDEX IF NOT EXISTS idx_part_payment_brokerage ON part_payment(brokerage_payment_id);
CREATE INDEX IF NOT EXISTS idx_part_payment_date ON part_payment(payment_date);

CREATE INDEX IF NOT EXISTS idx_pending_payment_buyer ON pending_payment(buyer_id);
CREATE INDEX IF NOT EXISTS idx_pending_payment_broker ON pending_payment(broker_id);
CREATE INDEX IF NOT EXISTS idx_pending_payment_status ON pending_payment(status);

CREATE INDEX IF NOT EXISTS idx_receivable_payment_seller ON receivable_payment(seller_id);
CREATE INDEX IF NOT EXISTS idx_receivable_payment_broker ON receivable_payment(broker_id);

CREATE INDEX IF NOT EXISTS idx_payment_transaction_seller ON payment_transaction(seller_id);
CREATE INDEX IF NOT EXISTS idx_payment_transaction_buyer ON payment_transaction(buyer_id);
CREATE INDEX IF NOT EXISTS idx_payment_transaction_date ON payment_transaction(transaction_date);

-- INSERT SAMPLE DATA (OPTIONAL)
-- Sample broker
INSERT INTO broker (broker_name, email, phone) VALUES 
('Demo Broker', 'demo@brokerhub.com', '9999999999') 
ON CONFLICT (email) DO NOTHING;

-- Sample financial year
INSERT INTO financial_year (year_name, start_date, end_date, is_active, broker_id) VALUES 
('2024-25', '2024-04-01', '2025-03-31', true, 1)
ON CONFLICT DO NOTHING;

-- Sample users
INSERT INTO "user" (user_name, email, phone, user_type, broker_id) VALUES 
('Demo Seller', 'seller@demo.com', '8888888888', 'SELLER', 1),
('Demo Buyer', 'buyer@demo.com', '7777777777', 'BUYER', 1)
ON CONFLICT (email) DO NOTHING;

-- Sample product
INSERT INTO product (product_name, category, unit, broker_id) VALUES 
('Rice', 'Grains', 'Bags', 1)
ON CONFLICT DO NOTHING;

-- Success message
SELECT 'Database setup completed successfully!' as message;