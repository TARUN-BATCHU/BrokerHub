# Brokerage Dashboard Testing Guide

## Prerequisites
1. Ensure the database migration script has been executed
2. Have at least one broker and some merchants in the system
3. Have some ledger records for calculating brokerage

## Testing Steps

### 1. Calculate Brokerage (First Step)
Before testing the dashboard, calculate brokerage for a broker:

```bash
POST /api/brokerage-dashboard/{brokerId}/calculate-brokerage
```

This will:
- Calculate bags sold/bought from ledger records
- Create BrokeragePayment records for each merchant
- Set initial payment status as PENDING

### 2. Test Dashboard Overview
```bash
GET /api/brokerage-dashboard/{brokerId}
```

Expected response:
- Summary with total amounts and merchant counts
- List of merchants with their brokerage details
- Chart data for visualization

### 3. Test Merchants List
```bash
GET /api/brokerage-dashboard/{brokerId}/merchants
```

Should return detailed list of all merchants with:
- Bag counts (sold/bought/total)
- Brokerage calculations
- Payment status
- Payment history

### 4. Test Payment Status Update

#### Partial Payment
```bash
PUT /api/brokerage-dashboard/{brokerId}/payment-status
Content-Type: application/json

{
  "merchantId": 1,
  "status": "PARTIAL_PAID",
  "paidAmount": 100.00,
  "paymentDate": "2024-01-15",
  "paymentMethod": "UPI",
  "transactionReference": "TXN123456",
  "notes": "Partial payment received"
}
```

#### Full Payment
```bash
PUT /api/brokerage-dashboard/{brokerId}/payment-status
Content-Type: application/json

{
  "merchantId": 1,
  "status": "PAID",
  "paymentDate": "2024-01-15",
  "paymentMethod": "CASH",
  "notes": "Full payment received"
}
```

### 5. Test Brokerage Amount Update
```bash
PUT /api/brokerage-dashboard/{brokerId}/brokerage-amount
Content-Type: application/json

{
  "merchantId": 1,
  "newBrokerageAmount": 100.00,
  "reason": "Rounded to nearest hundred for convenience"
}
```

### 6. Test Payment History
```bash
GET /api/brokerage-dashboard/{brokerId}/merchant/{merchantId}/history
```

Should return all payment transactions for the merchant.

### 7. Test Payment Methods
```bash
GET /api/brokerage-dashboard/payment-methods
```

Should return list of available payment methods.

## Sample Test Data

### Sample Broker
```json
{
  "brokerId": 1,
  "brokerName": "Test Broker",
  "userName": "testbroker"
}
```

### Sample Merchants
```json
[
  {
    "userId": 1,
    "firmName": "ABC Traders",
    "ownerName": "John Doe",
    "brokerageRate": 2,
    "brokerId": 1
  },
  {
    "userId": 2,
    "firmName": "XYZ Merchants",
    "ownerName": "Jane Smith",
    "brokerageRate": 3,
    "brokerId": 1
  }
]
```

## Expected Calculations

### Brokerage Formula
```
Total Bags = Sold Bags + Bought Bags
Gross Brokerage = Total Bags × Brokerage Rate
Discount = Gross Brokerage × 0.10 (10%)
TDS = Gross Brokerage × 0.05 (5%)
Net Brokerage = Gross Brokerage - Discount - TDS
Pending Amount = Net Brokerage - Paid Amount
```

### Example Calculation
```
Sold Bags: 100
Bought Bags: 50
Total Bags: 150
Brokerage Rate: ₹2 per bag

Gross Brokerage: 150 × 2 = ₹300
Discount: 300 × 0.10 = ₹30
TDS: 300 × 0.05 = ₹15
Net Brokerage: 300 - 30 - 15 = ₹255
```

## Verification Points

1. **Dashboard Summary**: Verify totals match individual merchant amounts
2. **Payment Status**: Ensure status updates correctly based on payments
3. **Payment History**: Check all partial payments are recorded
4. **Brokerage Adjustment**: Verify manual adjustments are applied correctly
5. **Chart Data**: Ensure chart values match summary totals

## Common Issues and Solutions

### Issue: No merchants showing in dashboard
**Solution**: Run the calculate-brokerage endpoint first

### Issue: Incorrect brokerage calculations
**Solution**: Check if merchants have brokerage rates set and ledger records exist

### Issue: Payment status not updating
**Solution**: Verify the merchant belongs to the specified broker

### Issue: Payment history empty
**Solution**: Make some partial payments first using the payment-status endpoint

## Database Verification Queries

```sql
-- Check brokerage payments
SELECT bp.*, u.firm_name 
FROM brokerage_payment bp 
JOIN user u ON bp.merchant_id = u.user_id 
WHERE bp.broker_id = 1;

-- Check part payments
SELECT pp.*, bp.merchant_id 
FROM part_payment pp 
JOIN brokerage_payment bp ON pp.brokerage_payment_id = bp.id 
WHERE bp.broker_id = 1;

-- Check summary
SELECT 
    COUNT(*) as total_merchants,
    SUM(net_brokerage) as total_receivable,
    SUM(paid_amount) as total_received,
    SUM(pending_amount) as total_pending
FROM brokerage_payment 
WHERE broker_id = 1;
```

## Performance Testing

Test with larger datasets:
- 100+ merchants
- Multiple financial years
- Hundreds of payment transactions

Monitor response times and optimize queries if needed.