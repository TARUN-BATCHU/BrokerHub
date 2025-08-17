# BrokerHub API - cURL Commands

## Financial Year Controller APIs

### 1. Create Financial Year
```bash
curl -X POST "http://localhost:8080/BrokerHub/FinancialYear/create" \
  -H "Content-Type: application/json" \
  -d '{
    "financialYearName": "FY 2024-25",
    "start": "2024-04-01",
    "end": "2025-03-31"
  }'
```

### 2. Get All Financial Year IDs
```bash
curl -X GET "http://localhost:8080/BrokerHub/FinancialYear/getAllFinancialYearIds" \
  -H "Accept: application/json"
```

### 3. Get All Financial Years
```bash
curl -X GET "http://localhost:8080/BrokerHub/FinancialYear/getAllFinancialYears" \
  -H "Accept: application/json"
```

## Daily Ledger Controller APIs

### 1. Create Daily Ledger
```bash
curl -X POST "http://localhost:8080/BrokerHub/DailyLedger/create?financialYearId=1&date=2024-06-15" \
  -H "Content-Type: application/json"
```

### 2. Get Daily Ledger
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getDailyLedger?date=2024-06-15" \
  -H "Accept: application/json"
```

### 3. Get Daily Ledger On Date
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getDailyLedgerOnDate?date=2024-06-15" \
  -H "Accept: application/json"
```

### 4. Get Optimized Daily Ledger
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getOptimizedDailyLedger?date=2024-06-15" \
  -H "Accept: application/json"
```

### 5. Get Daily Ledger With Pagination
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getDailyLedgerWithPagination?date=2024-06-15&page=0&size=10&sortBy=ledgerDetailsId&sortDir=asc" \
  -H "Accept: application/json"
```

### 6. Get Optimized Daily Ledger With Pagination
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getOptimizedDailyLedgerWithPagination?date=2024-06-15&page=0&size=10&sortBy=ledgerDetailsId&sortDir=asc" \
  -H "Accept: application/json"
```

## Ledger Details Controller APIs

### 1. Create Ledger Details
```bash
curl -X POST "http://localhost:8080/BrokerHub/LedgerDetails/createLedgerDetails" \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2024-06-15",
    "fromSeller": 1,
    "brokerage": 50,
    "brokerId": 10,
    "ledgerRecordDTOList": [
      {
        "productId": 1,
        "buyerName": "ABC Traders",
        "quantity": 100,
        "brokerage": 5,
        "productCost": 1000
      },
      {
        "productId": 2,
        "buyerName": "XYZ Corporation",
        "quantity": 50,
        "brokerage": 8,
        "productCost": 1500
      }
    ]
  }'
```

### 2. Get All Ledger Details
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getAllLedgerDetails" \
  -H "Content-Type: application/json" \
  -d '10'
```

### 3. Get Ledger Details By ID
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsById?ledgerDetailId=1&brokerId=10" \
  -H "Accept: application/json"
```

### 4. Get Ledger Details By Transaction Number
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber?transactionNumber=1001&brokerId=10" \
  -H "Accept: application/json"
```

### 5. Get Optimized Ledger Details By ID
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsById?ledgerDetailId=1&brokerId=10" \
  -H "Accept: application/json"
```

### 6. Get Optimized Ledger Details By Transaction Number
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber?transactionNumber=1001&brokerId=10" \
  -H "Accept: application/json"
```

### 7. Get Ledger Details By Date
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsByDate?date=2024-06-15&brokerId=10" \
  -H "Accept: application/json"
```

### 8. Get Ledger Details By Seller
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsBySeller?sellerId=1&brokerId=10" \
  -H "Accept: application/json"
```

## Sample Data Values

### Common Parameters:
- **brokerId**: 10
- **financialYearId**: 1 (FY 2024-25)
- **date**: 2024-06-15, 2024-08-20, 2024-12-10, 2025-01-15 (within FY 2024-25 range)
- **ledgerDetailId**: 1, 2, 3, 4, 5
- **transactionNumber**: 1001, 1002, 1003
- **sellerId**: 1, 2, 3
- **productId**: 1, 2, 3, 4
- **buyerName**: "ABC Traders", "XYZ Corporation", "PQR Industries"

### Pagination Parameters:
- **page**: 0, 1, 2 (0-based indexing)
- **size**: 5, 10, 20, 50
- **sortBy**: "ledgerDetailsId", "date", "brokerTransactionNumber"
- **sortDir**: "asc", "desc"

## Notes:
1. Replace `http://localhost:8080` with your actual server URL
2. All APIs support multi-tenant architecture with broker-based isolation
3. Date format should be: YYYY-MM-DD
4. For pagination APIs, default values are: page=0, size=10, sortBy=ledgerDetailsId, sortDir=asc
5. The `getAllLedgerDetails` API expects brokerId in request body (unusual pattern in the controller)