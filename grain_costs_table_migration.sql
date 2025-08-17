-- Create grain_costs table for tracking grain price history
CREATE TABLE IF NOT EXISTS grain_costs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_grain_costs_broker 
        FOREIGN KEY (broker_id) REFERENCES broker(broker_id) 
        ON DELETE CASCADE,
    
    INDEX idx_grain_costs_broker_id (broker_id),
    INDEX idx_grain_costs_created_at (created_at),
    INDEX idx_grain_costs_product_name (product_name)
);