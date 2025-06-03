-- =====================================================
-- BROKER ACCOUNT CREATION SCRIPT
-- =====================================================
-- This script creates sample broker accounts for testing
-- the multi-tenant brokerage application.
-- =====================================================

USE brokerHub_multiTenant;

-- =====================================================
-- DEFAULT ADMIN BROKER (Already created in setup script)
-- =====================================================
-- Username: admin
-- Password: admin123
-- Encrypted: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYrONgrkHuq6Zu

-- =====================================================
-- ADDITIONAL SAMPLE BROKERS
-- =====================================================

-- Broker 2: Mumbai Broker
INSERT INTO broker (user_name, password, broker_name, brokerage_firm_name, email, phone_number, total_brokerage) 
VALUES ('mumbai_broker', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Mumbai Broker', 'Mumbai Trading Co.', 'mumbai@brokerhub.com', '9876543210', 0.00);
-- Password: hello123

-- Broker 3: Delhi Broker  
INSERT INTO broker (user_name, password, broker_name, brokerage_firm_name, email, phone_number, total_brokerage) 
VALUES ('delhi_broker', '$2a$10$DpwjKnOXfCQtiQmzQVjbVeoy4oWDEEXw8lY/sIxvMf.H0xa/qkaKK', 'Delhi Broker', 'Delhi Commodities Ltd.', 'delhi@brokerhub.com', '9876543211', 0.00);
-- Password: password123

-- Broker 4: Pune Broker
INSERT INTO broker (user_name, password, broker_name, brokerage_firm_name, email, phone_number, total_brokerage) 
VALUES ('pune_broker', '$2a$10$e0MWvwI5.Ue3G.q.1l3/V.1pME1YiP.gULe9SXQXN8Ws8.SdUyDpO', 'Pune Broker', 'Pune Agricultural Exchange', 'pune@brokerhub.com', '9876543212', 0.00);
-- Password: broker123

-- Broker 5: Chennai Broker
INSERT INTO broker (user_name, password, broker_name, brokerage_firm_name, email, phone_number, total_brokerage) 
VALUES ('chennai_broker', '$2a$10$fE.COEfVOwlaCoGWQqRnYeIhiTIxlmGjqX2qXdj1/JOmx8/mh/.WG', 'Chennai Broker', 'South India Trading Hub', 'chennai@brokerhub.com', '9876543213', 0.00);
-- Password: south123

-- =====================================================
-- BROKER CREDENTIALS SUMMARY
-- =====================================================

/*
BROKER LOGIN CREDENTIALS:

1. Admin Broker (Default)
   Username: admin
   Password: admin123
   Firm: Default Brokerage Firm

2. Mumbai Broker
   Username: mumbai_broker
   Password: hello123
   Firm: Mumbai Trading Co.

3. Delhi Broker
   Username: delhi_broker
   Password: password123
   Firm: Delhi Commodities Ltd.

4. Pune Broker
   Username: pune_broker
   Password: broker123
   Firm: Pune Agricultural Exchange

5. Chennai Broker
   Username: chennai_broker
   Password: south123
   Firm: South India Trading Hub

AUTHENTICATION:
- Use Basic Authentication
- Header: Authorization: Basic <base64(username:password)>
- Example for admin: Authorization: Basic YWRtaW46YWRtaW4xMjM=

API TESTING:
- Base URL: http://localhost:8080/api
- All endpoints require authentication
- Each broker will only see their own data
*/

-- =====================================================
-- CREATE SAMPLE DATA FOR EACH BROKER
-- =====================================================

-- Sample addresses for each broker
INSERT INTO address (broker_id, city, area, pincode) VALUES
(1, 'Mumbai', 'Andheri', '400001'),
(1, 'Mumbai', 'Bandra', '400002'),
(2, 'Mumbai', 'Kurla', '400003'),
(2, 'Mumbai', 'Powai', '400004'),
(3, 'Delhi', 'Connaught Place', '110001'),
(3, 'Delhi', 'Karol Bagh', '110002'),
(4, 'Pune', 'Shivaji Nagar', '411001'),
(4, 'Pune', 'Kothrud', '411002'),
(5, 'Chennai', 'T. Nagar', '600001'),
(5, 'Chennai', 'Anna Nagar', '600002');

-- Sample financial years for each broker
INSERT INTO financial_year (broker_id, start, end, financial_year_name, for_bills) VALUES
(1, '2024-04-01', '2025-03-31', '2024-25', TRUE),
(2, '2024-04-01', '2025-03-31', '2024-25', TRUE),
(3, '2024-04-01', '2025-03-31', '2024-25', TRUE),
(4, '2024-04-01', '2025-03-31', '2024-25', TRUE),
(5, '2024-04-01', '2025-03-31', '2024-25', TRUE);

-- Sample products for each broker
INSERT INTO product (broker_id, product_name, product_brokerage, quantity, price, quality) VALUES
-- Admin Broker products
(1, 'Rice', 10.0, 100, 50, 'Grade A'),
(1, 'Wheat', 8.0, 150, 40, 'Premium'),
(1, 'Sugar', 12.0, 80, 60, 'Refined'),

-- Mumbai Broker products
(2, 'Rice', 9.0, 120, 48, 'Grade A'),
(2, 'Pulses', 15.0, 90, 80, 'Premium'),
(2, 'Spices', 20.0, 50, 100, 'Export Quality'),

-- Delhi Broker products
(3, 'Wheat', 7.0, 200, 38, 'Standard'),
(3, 'Barley', 6.0, 100, 35, 'Feed Grade'),
(3, 'Mustard', 18.0, 60, 90, 'Oil Grade'),

-- Pune Broker products
(4, 'Onions', 5.0, 300, 25, 'Medium'),
(4, 'Potatoes', 4.0, 250, 20, 'Large'),
(4, 'Tomatoes', 8.0, 150, 30, 'Fresh'),

-- Chennai Broker products
(5, 'Rice', 11.0, 180, 52, 'Basmati'),
(5, 'Coconut', 25.0, 40, 120, 'Fresh'),
(5, 'Tamarind', 30.0, 30, 150, 'Premium');

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Check all brokers
-- SELECT broker_id, user_name, broker_name, brokerage_firm_name, email FROM broker;

-- Check products per broker
-- SELECT b.broker_name, p.product_name, p.quantity, p.price 
-- FROM broker b 
-- JOIN product p ON b.broker_id = p.broker_id 
-- ORDER BY b.broker_name, p.product_name;

-- Check addresses per broker
-- SELECT b.broker_name, a.city, a.area, a.pincode 
-- FROM broker b 
-- JOIN address a ON b.broker_id = a.broker_id 
-- ORDER BY b.broker_name, a.city;

-- =====================================================
-- PASSWORD GENERATION REFERENCE
-- =====================================================

/*
To generate BCrypt passwords in Java (for future reference):

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("hello123: " + encoder.encode("hello123"));
        System.out.println("password123: " + encoder.encode("password123"));
        System.out.println("broker123: " + encoder.encode("broker123"));
        System.out.println("south123: " + encoder.encode("south123"));
    }
}

Or use online BCrypt generators:
- https://bcrypt-generator.com/
- https://www.browserling.com/tools/bcrypt
*/

-- =====================================================
-- END OF BROKER CREATION SCRIPT
-- =====================================================
