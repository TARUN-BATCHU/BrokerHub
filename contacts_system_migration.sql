-- Contacts Management System Database Migration
-- This script creates tables for the contacts management system

-- Create contact_sections table
CREATE TABLE IF NOT EXISTS contact_sections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    section_name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    broker_id BIGINT NOT NULL,
    parent_section_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (parent_section_id) REFERENCES contact_sections(id) ON DELETE CASCADE,
    UNIQUE KEY unique_section_per_parent (section_name, broker_id, parent_section_id),
    INDEX idx_broker_section (broker_id, section_name),
    INDEX idx_parent_section (parent_section_id)
);

-- Create contacts table
CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firm_name VARCHAR(200),
    user_name VARCHAR(100) NOT NULL,
    gst_number VARCHAR(50),
    additional_info TEXT,
    broker_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_broker_contact (broker_id, user_name),
    INDEX idx_search_fields (broker_id, user_name, firm_name, gst_number)
);

-- Create contact_section_mappings table (many-to-many)
CREATE TABLE IF NOT EXISTS contact_section_mappings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contact_id BIGINT NOT NULL,
    contact_section_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE,
    FOREIGN KEY (contact_section_id) REFERENCES contact_sections(id) ON DELETE CASCADE,
    UNIQUE KEY unique_contact_section (contact_id, contact_section_id),
    INDEX idx_contact_mapping (contact_id),
    INDEX idx_section_mapping (contact_section_id)
);

-- Create contact_phones table
CREATE TABLE IF NOT EXISTS contact_phones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(15) NOT NULL,
    phone_type VARCHAR(50),
    contact_id BIGINT NOT NULL,
    
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE,
    INDEX idx_contact_phone (contact_id)
);

-- Create contact_addresses table
CREATE TABLE IF NOT EXISTS contact_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    address_type VARCHAR(50),
    contact_id BIGINT NOT NULL,
    
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE,
    INDEX idx_contact_address (contact_id),
    INDEX idx_location (city, state)
);

-- Insert default contact sections for all existing brokers
INSERT INTO contact_sections (section_name, description, broker_id, parent_section_id)
SELECT 'Buyers', 'Grain buyers and purchasers', id, NULL FROM brokers
WHERE NOT EXISTS (
    SELECT 1 FROM contact_sections cs WHERE cs.section_name = 'Buyers' AND cs.broker_id = brokers.id AND cs.parent_section_id IS NULL
);

INSERT INTO contact_sections (section_name, description, broker_id, parent_section_id)
SELECT 'Sellers', 'Grain sellers and suppliers', id, NULL FROM brokers
WHERE NOT EXISTS (
    SELECT 1 FROM contact_sections cs WHERE cs.section_name = 'Sellers' AND cs.broker_id = brokers.id AND cs.parent_section_id IS NULL
);

INSERT INTO contact_sections (section_name, description, broker_id, parent_section_id)
SELECT 'Transport', 'Transportation services', id, NULL FROM brokers
WHERE NOT EXISTS (
    SELECT 1 FROM contact_sections cs WHERE cs.section_name = 'Transport' AND cs.broker_id = brokers.id AND cs.parent_section_id IS NULL
);

INSERT INTO contact_sections (section_name, description, broker_id, parent_section_id)
SELECT 'Workers', 'Labor and workers', id, NULL FROM brokers
WHERE NOT EXISTS (
    SELECT 1 FROM contact_sections cs WHERE cs.section_name = 'Workers' AND cs.broker_id = brokers.id AND cs.parent_section_id IS NULL
);

INSERT INTO contact_sections (section_name, description, broker_id, parent_section_id)
SELECT 'Local Authorities', 'Government and local officials', id, NULL FROM brokers
WHERE NOT EXISTS (
    SELECT 1 FROM contact_sections cs WHERE cs.section_name = 'Local Authorities' AND cs.broker_id = brokers.id AND cs.parent_section_id IS NULL
);

-- Create nested sections for Buyers (example)
INSERT INTO contact_sections (section_name, description, broker_id, parent_section_id)
SELECT 'Gram Dal Buyers', 'Buyers specializing in gram dal', b.id, cs.id 
FROM brokers b 
JOIN contact_sections cs ON cs.broker_id = b.id AND cs.section_name = 'Buyers' AND cs.parent_section_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM contact_sections cs2 WHERE cs2.section_name = 'Gram Dal Buyers' AND cs2.broker_id = b.id AND cs2.parent_section_id = cs.id
);

INSERT INTO contact_sections (section_name, description, broker_id, parent_section_id)
SELECT 'Moong Dal Buyers', 'Buyers specializing in moong dal', b.id, cs.id 
FROM brokers b 
JOIN contact_sections cs ON cs.broker_id = b.id AND cs.section_name = 'Buyers' AND cs.parent_section_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM contact_sections cs2 WHERE cs2.section_name = 'Moong Dal Buyers' AND cs2.broker_id = b.id AND cs2.parent_section_id = cs.id
);

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_contacts_full_text ON contacts (user_name, firm_name, gst_number);
CREATE INDEX IF NOT EXISTS idx_phone_search ON contact_phones (phone_number);
CREATE INDEX IF NOT EXISTS idx_address_search ON contact_addresses (city, state, pincode);
CREATE INDEX IF NOT EXISTS idx_section_hierarchy ON contact_sections (broker_id, parent_section_id, section_name);
CREATE INDEX IF NOT EXISTS idx_mapping_lookup ON contact_section_mappings (contact_section_id, contact_id);

COMMIT;