-- Subscription Module Database Migration Script
-- Execute this script to create subscription-related tables

-- Create subscription_plans table
CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_code VARCHAR(50) NOT NULL UNIQUE,
    plan_name VARCHAR(255) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    features_json JSON,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create user_subscriptions table
CREATE TABLE IF NOT EXISTS user_subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT
);

-- Create subscription_history table
CREATE TABLE IF NOT EXISTS subscription_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    old_plan_id BIGINT,
    new_plan_id BIGINT NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(50) NOT NULL,
    reason TEXT,
    FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id) ON DELETE CASCADE,
    FOREIGN KEY (old_plan_id) REFERENCES subscription_plans(id) ON DELETE SET NULL,
    FOREIGN KEY (new_plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT
);

-- Create subscription_charges table
CREATE TABLE IF NOT EXISTS subscription_charges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    charge_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id) ON DELETE CASCADE
);

-- Insert default subscription plans
INSERT INTO subscription_plans (plan_code, plan_name, base_price, billing_cycle, currency, features_json, is_active) VALUES
('FREE', 'Free Plan', 0.00, 'MONTHLY', 'INR', '{"projects": 1, "users": 2, "storage_gb": 5}', TRUE),
('BASIC', 'Basic Plan', 499.00, 'MONTHLY', 'INR', '{"projects": 5, "users": 3, "storage_gb": 20}', TRUE),
('PRO', 'Pro Plan', 999.00, 'MONTHLY', 'INR', '{"projects": 10, "users": 5, "storage_gb": 50}', TRUE),
('PAY_AS_YOU_GO', 'Pay As You Go', 0.00, 'MONTHLY', 'INR', '{"projects": -1, "users": -1, "storage_gb": -1}', TRUE)
ON DUPLICATE KEY UPDATE 
    plan_name = VALUES(plan_name),
    base_price = VALUES(base_price),
    features_json = VALUES(features_json);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_user_status ON user_subscriptions(user_id, status);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_end_date ON user_subscriptions(end_date);
CREATE INDEX IF NOT EXISTS idx_subscription_history_subscription ON subscription_history(subscription_id);
CREATE INDEX IF NOT EXISTS idx_subscription_charges_subscription ON subscription_charges(subscription_id);

COMMIT;