-- Migration script to add broker_transaction_number column to ledger_details table
-- This enables broker-specific transaction numbering for multi-tenant support

-- Add the new column
ALTER TABLE ledger_details 
ADD COLUMN broker_transaction_number BIGINT;

-- Update existing records with sequential numbers per broker
-- This will assign transaction numbers starting from 1 for each broker based on ledger_details_id order
WITH numbered_transactions AS (
    SELECT 
        ledger_details_id,
        broker_id,
        ROW_NUMBER() OVER (PARTITION BY broker_id ORDER BY ledger_details_id) as transaction_number
    FROM ledger_details
)
UPDATE ledger_details 
SET broker_transaction_number = numbered_transactions.transaction_number
FROM numbered_transactions 
WHERE ledger_details.ledger_details_id = numbered_transactions.ledger_details_id;

-- Make the column NOT NULL after populating existing data
ALTER TABLE ledger_details 
ALTER COLUMN broker_transaction_number SET NOT NULL;

-- Create a unique index to ensure no duplicate transaction numbers per broker
CREATE UNIQUE INDEX idx_broker_transaction_number 
ON ledger_details (broker_id, broker_transaction_number);

-- Create an index for faster lookups by transaction number
CREATE INDEX idx_ledger_details_broker_transaction 
ON ledger_details (broker_id, broker_transaction_number);

-- Verify the migration
SELECT 
    broker_id,
    COUNT(*) as total_transactions,
    MIN(broker_transaction_number) as min_transaction_number,
    MAX(broker_transaction_number) as max_transaction_number
FROM ledger_details 
GROUP BY broker_id 
ORDER BY broker_id;