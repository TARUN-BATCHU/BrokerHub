# Current Financial Year Workflow

## Problem Solved
UI was unable to send `financialYearId` field, causing database constraint violations.

## Solution Implemented
Created a **Current Financial Year** preference system that allows brokers to set their active financial year once, then use it automatically in all operations.

## Database Changes

### New Table: `current_financial_year`
```sql
CREATE TABLE current_financial_year (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL UNIQUE,
    financial_year_id BIGINT NOT NULL,
    UNIQUE KEY unique_broker (broker_id)
);
```

## New APIs Added

### 1. Set Current Financial Year
```bash
POST /BrokerHub/FinancialYear/setCurrentFinancialYear?financialYearId=2024
Authorization: Bearer <token>
```

### 2. Get Current Financial Year  
```bash
GET /BrokerHub/FinancialYear/getCurrentFinancialYear
Authorization: Bearer <token>
```

## Updated Behavior

### Before (Required financialYearId)
```json
{
  "brokerId": 1,
  "financialYearId": 2024,  // REQUIRED
  "fromSeller": 123,
  "ledgerRecordDTOList": [...]
}
```

### After (Optional financialYearId)
```json
{
  "brokerId": 1,
  // financialYearId omitted - uses current FY
  "fromSeller": 123, 
  "ledgerRecordDTOList": [...]
}
```

## Workflow

### Initial Setup (Done Once)
1. Broker logs in
2. UI calls: `POST /setCurrentFinancialYear?financialYearId=2024`
3. System stores preference in `current_financial_year` table

### Daily Operations (Simplified)
1. UI creates transactions without `financialYearId`
2. System automatically uses broker's current financial year
3. Transaction numbers work correctly (1, 2, 3... per broker per FY)

### Override When Needed
1. UI can still specify `financialYearId` to override current FY
2. Useful for viewing/editing old financial year data

## Implementation Details

### Service Layer Changes
- `CurrentFinancialYearService` - manages FY preferences
- `LedgerDetailsServiceImpl` - auto-resolves FY when null
- All transaction number methods support optional FY parameter

### Controller Changes  
- `FinancialYearController` - new FY preference endpoints
- `LedgerDetailsController` - made `financialYearId` optional in transaction APIs

### Error Handling
- Clear error message when no current FY is set
- Graceful fallback to explicit FY parameter
- Maintains backward compatibility

## Benefits
1. **UI Simplified** - No need to track/send financialYearId
2. **User Friendly** - Set once, use everywhere
3. **Flexible** - Can override when needed
4. **Backward Compatible** - Old API calls still work
5. **Error Prevention** - No more null constraint violations