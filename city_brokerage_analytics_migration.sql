-- City Brokerage Analytics Database Migration Script

-- Create brokerage_history table for year-over-year comparison
CREATE TABLE IF NOT EXISTS brokerage_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    broker_id BIGINT NOT NULL,
    financial_year_id BIGINT NOT NULL,
    sold_bags BIGINT DEFAULT 0,
    bought_bags BIGINT DEFAULT 0,
    total_bags BIGINT DEFAULT 0,
    total_brokerage DECIMAL(10,2) DEFAULT 0.00,
    paid_brokerage DECIMAL(10,2) DEFAULT 0.00,
    
    FOREIGN KEY (merchant_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (broker_id) REFERENCES broker(broker_id) ON DELETE CASCADE,
    FOREIGN KEY (financial_year_id) REFERENCES financial_year(year_id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_merchant_broker_year (merchant_id, broker_id, financial_year_id),
    INDEX idx_broker_year (broker_id, financial_year_id),
    INDEX idx_merchant_year (merchant_id, financial_year_id)
);

-- Add city index to address table for better performance
CREATE INDEX IF NOT EXISTS idx_address_city ON address(city);

-- Create view for city-wise analytics (optional, for better performance)
CREATE OR REPLACE VIEW city_brokerage_analytics AS
SELECT 
    a.city,
    bp.broker_id,
    COUNT(DISTINCT bp.merchant_id) as total_merchants,
    SUM(bp.sold_bags) as total_bags_sold,
    SUM(bp.bought_bags) as total_bags_bought,
    SUM(bp.total_bags) as total_bags,
    SUM(bp.net_brokerage) as total_actual_brokerage,
    SUM(bp.pending_amount) as total_brokerage_pending,
    SUM(bp.paid_amount) as total_brokerage_received,
    SUM(CASE WHEN bp.net_brokerage > 0 THEN 1 ELSE 0 END) as total_payments,
    SUM(CASE WHEN bp.status = 'PARTIAL_PAID' THEN 1 ELSE 0 END) as total_partial_payments,
    SUM(CASE WHEN bp.status = 'PAID' THEN 1 ELSE 0 END) as total_success_payments
FROM brokerage_payment bp
JOIN user u ON bp.merchant_id = u.user_id
JOIN address a ON u.address_id = a.address_id
GROUP BY a.city, bp.broker_id;

COMMIT;