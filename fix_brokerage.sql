-- Fix total_payable_brokerage for all users
-- Formula: (total_bags_bought + total_bags_sold) * brokerage_rate

UPDATE user 
SET total_payable_brokerage = (
    COALESCE(total_bags_bought, 0) + COALESCE(total_bags_sold, 0)
) * COALESCE(brokerage_rate, 0)
WHERE brokerage_rate IS NOT NULL 
AND brokerage_rate > 0;

-- Optional: Set to 0 for users with no brokerage rate
UPDATE user 
SET total_payable_brokerage = 0 
WHERE brokerage_rate IS NULL 
OR brokerage_rate = 0;

-- Verify the results (optional query to check)
SELECT 
    user_id,
    firm_name,
    total_bags_bought,
    total_bags_sold,
    brokerage_rate,
    (COALESCE(total_bags_bought, 0) + COALESCE(total_bags_sold, 0)) * COALESCE(brokerage_rate, 0) as calculated_brokerage,
    total_payable_brokerage as current_brokerage
FROM user 
WHERE broker_id = 1  -- Replace with your broker ID
ORDER BY firm_name;