# API Fix Results for getUserSummary

## Issues Fixed:

### Issue 1: Missing Merchants in Response ✅ FIXED
**Problem**: The original query only returned users who were sellers, missing buyers.

**Root Cause**: 
```sql
-- OLD QUERY (only sellers)
SELECT DISTINCT u FROM User u 
JOIN LedgerDetails ld ON u.userId = ld.fromSeller.userId 
```

**Solution**: 
```sql
-- NEW QUERY (both sellers and buyers)
SELECT DISTINCT u.* FROM User u 
WHERE u.broker_id = :brokerId AND (
  EXISTS (SELECT 1 FROM Ledger_details ld 
          JOIN Daily_ledger dl ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id 
          WHERE ld.user_id = u.user_id AND dl.financial_year_year_id = :financialYearId) OR 
  EXISTS (SELECT 1 FROM Ledger_record lr 
          JOIN Ledger_details ld ON lr.ledger_details_ledger_details_Id = ld.ledger_details_id 
          JOIN Daily_ledger dl ON ld.daily_ledger_daily_ledger_Id = dl.daily_ledger_id 
          WHERE lr.to_buyer_user_id = u.user_id AND dl.financial_year_year_id = :financialYearId)
)
```

### Issue 2: Wrong Brokerage Calculation ✅ FIXED
**Problem**: Using lifetime totals instead of financial year specific data.

**Root Cause**:
```java
// OLD CODE (lifetime totals)
Long totalBags = user.getTotalBagsSold() + user.getTotalBagsBought();
BigDecimal calculatedBrokerage = brokerageRate.multiply(BigDecimal.valueOf(totalBags));
```

**Solution**: 
- Added new query `findUserBagCountsAndBrokerageByFinancialYear()` to get actual financial year data
- Updated service to use actual transaction data instead of lifetime totals
- Proper calculation of brokerage from actual LedgerRecord.totalBrokerage values

## Expected Results:

### Before Fix:
```json
{
  "content": [
    {
      "userId": 1,
      "firmName": "Merchant A",
      "totalBagsSold": 10,
      "totalBagsBought": 0,
      "totalPayableBrokerage": "wrong_amount"
    }
  ]
}
```

### After Fix:
```json
{
  "content": [
    {
      "userId": 1,
      "firmName": "Merchant A", 
      "totalBagsSold": 10,
      "totalBagsBought": 0,
      "totalPayableBrokerage": "correct_amount"
    },
    {
      "userId": 2,
      "firmName": "Merchant B",
      "totalBagsSold": 0,
      "totalBagsBought": 5,
      "totalPayableBrokerage": "correct_amount"
    },
    {
      "userId": 3,
      "firmName": "Merchant C",
      "totalBagsSold": 0,
      "totalBagsBought": 5,
      "totalPayableBrokerage": "correct_amount"
    }
  ]
}
```

## Test the API:
```bash
curl 'http://localhost:8080/BrokerHub/user/getUserSummary?financialYearId=8&page=0&size=1000&sort=firmName,asc' \
  -H 'Authorization: Bearer YOUR_TOKEN'
```

## ✅ CONFIRMED: Broker & Financial Year Specific Filtering

### Multi-Tenant Isolation:
- **User Level**: `u.broker_id = :brokerId` 
- **Transaction Level**: `ld.broker_id = :brokerId` in all EXISTS clauses
- **Financial Year**: `dl.financial_year_year_id = :financialYearId`

### Query Logic:
```sql
-- Only returns users belonging to specific broker
WHERE u.broker_id = :brokerId AND (
  -- Seller transactions for this broker + financial year
  EXISTS (... WHERE ld.user_id = u.user_id AND ld.broker_id = :brokerId AND dl.financial_year_year_id = :financialYearId) OR 
  -- Buyer transactions for this broker + financial year  
  EXISTS (... WHERE lr.to_buyer_user_id = u.user_id AND ld.broker_id = :brokerId AND dl.financial_year_year_id = :financialYearId)
)
```

### Brokerage Calculation:
- **Seller Brokerage**: `SUM(lr.total_brokerage)` WHERE `ld.broker_id = :brokerId`
- **Buyer Brokerage**: `SUM(lr.total_brokerage)` WHERE `ld.broker_id = :brokerId`
- **Financial Year**: Both filtered by `dl.financial_year_year_id = :financialYearId`

## Key Changes Made:

1. **UserRepository.java**:
   - Fixed `findUsersByBrokerIdAndFinancialYear()` to include both sellers and buyers
   - Added `findUserBagCountsAndBrokerageByFinancialYear()` for accurate calculations
   - **CRITICAL**: Added `ld.broker_id = :brokerId` in all EXISTS clauses for proper multi-tenant isolation

2. **UserServiceImpl.java**:
   - Updated `getUserSummaryByFinancialYear()` to use financial year specific data
   - Added proper mapping of actual brokerage amounts from transactions