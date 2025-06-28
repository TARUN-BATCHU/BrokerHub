-- Create market product table
CREATE TABLE market_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id VARCHAR(255) UNIQUE NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quality VARCHAR(50) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    available_until TIMESTAMP NOT NULL,
    broker_id BIGINT NOT NULL,
    firm_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (broker_id) REFERENCES brokers(id)
);

-- Create seller request table
CREATE TABLE seller_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(255) UNIQUE NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quality VARCHAR(50) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    available_until TIMESTAMP NOT NULL,
    seller_id BIGINT NOT NULL,
    broker_id BIGINT NOT NULL,
    firm_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    response_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES sellers(id),
    FOREIGN KEY (broker_id) REFERENCES brokers(id)
);

-- Create buyer request table
CREATE TABLE buyer_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(255) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    firm_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    response_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES market_product(id),
    FOREIGN KEY (buyer_id) REFERENCES buyers(id)
);

-- Create indexes for better performance
CREATE INDEX idx_market_product_broker ON market_product(broker_id);
CREATE INDEX idx_seller_request_broker ON seller_request(broker_id);
CREATE INDEX idx_seller_request_seller ON seller_request(seller_id);
CREATE INDEX idx_buyer_request_product ON buyer_request(product_id);
CREATE INDEX idx_buyer_request_buyer ON buyer_request(buyer_id);