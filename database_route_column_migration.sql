-- Add route column to address table
ALTER TABLE address ADD COLUMN route VARCHAR(255);

-- Create index on route column for faster lookups
CREATE INDEX idx_address_route ON address(route);