# 🔗 BrokerHub Complete API Endpoints Reference

## 🔐 Authentication Headers

### For Protected Endpoints:
```
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

### Base URL:
```
Development: http://localhost:8080
Production: https://your-domain.com
```

---

## 🟢 Public Endpoints (No Authentication Required)

### Broker Management

#### 1. Broker Registration
```http
POST /BrokerHub/Broker/createBroker
Content-Type: application/json

{
  "userName": "broker123",
  "password": "securePassword",
  "brokerName": "John Doe",
  "brokerageFirmName": "ABC Trading",
  "email": "john@abctrading.com",
  "phoneNumber": "9876543210",
  "pincode": "123456",
  "accountNumber": "1234567890",
  "ifscCode": "HDFC0001234"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/BrokerHub/Broker/createBroker \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "broker123",
    "password": "securePassword",
    "brokerName": "John Doe",
    "brokerageFirmName": "ABC Trading",
    "email": "john@abctrading.com",
    "phoneNumber": "9876543210",
    "pincode": "123456",
    "accountNumber": "1234567890",
    "ifscCode": "HDFC0001234"
  }'
```

**Response (201):**
```json
"Broker account successfully created"
```

#### 2. User Login
```http
POST /BrokerHub/Broker/login
Content-Type: application/json

{
  "userName": "broker123",
  "password": "securePassword"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "broker123",
  "brokerId": 1,
  "brokerName": "John Doe",
  "message": "Login successful"
}
```

#### 3. Check Username Availability
```http
GET /BrokerHub/Broker/UserNameExists/{userName}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Broker/UserNameExists/broker123"
```

**Response (200):**
```json
true
```

#### 4. Check Firm Name Availability
```http
GET /BrokerHub/Broker/BrokerFirmNameExists/{firmName}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Broker/BrokerFirmNameExists/ABC%20Trading"
```

**Response (200):**
```json
false
```

### 5. Get User Firm Names, IDs and Cities
```http
GET /BrokerHub/user/getFirmNamesIdsAndCities
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/getFirmNamesIdsAndCities" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "id": 1,
    "firmName": "ABC Trading Co",
    "city": "Mumbai"
  },
  {
    "id": 2,
    "firmName": "XYZ Mills",
    "city": "Pune"
  }
]
```

### 6. Get User Summary (Paginated)
```http
GET /BrokerHub/user/getUserSummary?page=0&size=10&sort=firmName,asc
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/getUserSummary?page=0&size=10&sort=firmName,asc" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "content": [
    {
      "userId": 10,
      "firmName": "ABC Trading Co",
      "city": "Mumbai",
      "totalBagsSold": 150,
      "totalBagsBought": 200,
      "brokeragePerBag": 5.50,
      "totalPayableBrokerage": 1925.00
    },
    {
      "userId": 15,
      "firmName": "XYZ Mills",
      "city": "Pune",
      "totalBagsSold": 300,
      "totalBagsBought": 100,
      "brokeragePerBag": 4.00,
      "totalPayableBrokerage": 1600.00
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 25,
  "totalPages": 3,
  "last": false,
  "first": true,
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false
  },
  "empty": false
}
```

---

---

## 🔒 Protected Endpoints (Authentication Required)

## User Management APIs

## Product Management APIs

### 1. Product Bulk Upload
```http
POST /BrokerHub/Product/bulkUpload
Authorization: Bearer <token>
Content-Type: multipart/form-data

Form Data:
- file: Excel file (.xlsx)
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Product/bulkUpload" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F "file=@products.xlsx"
```

**Response (200):**
```json
{
  "totalRecords": 10,
  "successfulRecords": 8,
  "failedRecords": 2,
  "errorMessages": [
    "Row 3: Product name is required",
    "Row 7: Error processing product - Invalid data format"
  ],
  "message": "Partial success: 8 products uploaded, 2 failed"
}
```

### 2. Download Product Template
```http
GET /BrokerHub/Product/downloadTemplate
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Product/downloadTemplate" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -o product_template.xlsx
```

**Response:** Excel file download with sample data and instructions

**Excel Template Structure:**
| Column | Field Name | Required | Description |
|--------|------------|----------|-------------|
| A | productName | **Yes** | Name of the product |
| B | productBrokerage | No | Brokerage rate per unit (default: 0.0) |
| C | quantity | No | Available quantity (default: 0) |
| D | price | No | Price per unit (default: 0) |
| E | quality | No | Quality grade/description |
| F | imgLink | No | Image URL for the product |

**Validation Rules:**
- Only `productName` is required
- Numeric fields default to 0 if empty
- Duplicate product names are allowed
- All fields except productName can be left empty

---

## Daily Ledger APIs

### 1. Create Daily Ledger
```http
POST /BrokerHub/DailyLedger/create?financialYearId=1&date=2024-01-15
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/DailyLedger/create?financialYearId=1&date=2024-01-15" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (201):**
```json
1
```

### 2. Get Daily Ledger
```http
GET /BrokerHub/DailyLedger/getDailyLedger?date=2024-01-15
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getDailyLedger?date=2024-01-15" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "dailyLedgerId": 1,
  "date": "2024-01-15",
  "broker": {
    "brokerId": 1,
    "brokerName": "John Broker",
    "brokerageFirmName": "ABC Brokerage"
  },
  "financialYear": {
    "yearId": 1,
    "financialYearName": "FY 2024-25",
    "start": "2024-04-01",
    "end": "2025-03-31"
  },
  "ledgerDetails": [
    {
      "ledgerDetailsId": 1,
      "brokerTransactionNumber": 1,
      "broker": {
        "brokerId": 1,
        "brokerName": "John Broker"
      },
      "fromSeller": {
        "userId": 10,
        "userType": "MILLER",
        "firmName": "XYZ Mills",
        "ownerName": "Miller Owner",
        "address": {
          "addressId": 5,
          "city": "Pune",
          "area": "Hadapsar",
          "pincode": "411028"
        }
      },
      "records": [
        {
          "ledgerRecordId": 1,
          "toBuyer": {
            "userId": 20,
            "userType": "TRADER",
            "firmName": "ABC Traders",
            "ownerName": "Trader Owner"
          },
          "product": {
            "productId": 1,
            "productName": "Wheat",
            "quality": "Premium",
            "productBrokerage": 5.0
          },
          "quantity": 10,
          "brokerage": 50,
          "productCost": 25000,
          "totalProductsCost": 25000,
          "totalBrokerage": 50
        }
      ]
    }
  ]
}
```

### 3. Get Daily Ledger On Date
```http
GET /BrokerHub/DailyLedger/getDailyLedgerOnDate?date=2024-01-15
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getDailyLedgerOnDate?date=2024-01-15" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same as getDailyLedger

### 4. Get Optimized Daily Ledger
```http
GET /BrokerHub/DailyLedger/getOptimizedDailyLedger?date=2024-01-15
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getOptimizedDailyLedger?date=2024-01-15" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "dailyLedgerId": 1,
  "date": "2024-01-15",
  "financialYearId": 1,
  "ledgerDetails": [
    {
      "ledgerDetailsId": 1,
      "brokerTransactionNumber": 1,
      "transactionDate": "2024-01-15",
      "fromSeller": {
        "userId": 10,
        "firmName": "XYZ Mills",
        "addressId": 5
      },
      "records": [
        {
          "ledgerRecordId": 1,
          "toBuyer": {
            "userId": 20,
            "firmName": "ABC Traders",
            "addressId": 8
          },
          "product": {
            "productId": 1,
            "productName": "Wheat"
          },
          "quantity": 10,
          "brokerage": 50,
          "productCost": 25000,
          "totalProductsCost": 25000,
          "totalBrokerage": 50
        }
      ],
      "transactionSummary": {
        "totalBagsSoldInTransaction": 25,
        "totalBrokerageInTransaction": 125.00,
        "totalReceivableAmountInTransaction": 62500,
        "averageBrokeragePerBag": 5.00,
        "numberOfProducts": 1,
        "numberOfBuyers": 2
      }
    }
  ]
}
```

### 5. Get Daily Ledger With Pagination
```http
GET /BrokerHub/DailyLedger/getDailyLedgerWithPagination?date=2024-01-15&page=0&size=10&sortBy=ledgerDetailsId&sortDir=asc
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getDailyLedgerWithPagination?date=2024-01-15&page=0&size=10&sortBy=ledgerDetailsId&sortDir=asc" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as getDailyLedger but with pagination applied

### 6. Get Optimized Daily Ledger With Pagination
```http
GET /BrokerHub/DailyLedger/getOptimizedDailyLedgerWithPagination?date=2024-01-15&page=0&size=10&sortBy=ledgerDetailsId&sortDir=asc
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getOptimizedDailyLedgerWithPagination?date=2024-01-15&page=0&size=10&sortBy=ledgerDetailsId&sortDir=asc" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as getOptimizedDailyLedger but with pagination applied

---

## Ledger Details APIs

### 1. Create Ledger Details
```http
POST /BrokerHub/LedgerDetails/createLedgerDetails
Authorization: Bearer <token>
Content-Type: application/json

{
  "brokerId": 1,
  "brokerage": 500,
  "fromSeller": 10,
  "date": "2024-01-15",
  "ledgerRecordDTOList": [
    {
      "buyerName": "ABC Traders",
      "productId": 1,
      "quantity": 10,
      "brokerage": 50,
      "productCost": 25000
    },
    {
      "buyerName": "PQR Trading",
      "productId": 1,
      "quantity": 15,
      "brokerage": 75,
      "productCost": 37500
    }
  ]
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/BrokerHub/LedgerDetails/createLedgerDetails \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "brokerId": 1,
    "brokerage": 500,
    "fromSeller": 10,
    "date": "2024-01-15",
    "ledgerRecordDTOList": [
      {
        "buyerName": "ABC Traders",
        "productId": 1,
        "quantity": 10,
        "brokerage": 50,
        "productCost": 25000
      }
    ]
  }'
```

**Response (201):**
```json
"Ledger details created successfully"
```

### 2. Get All Ledger Details
```http
GET /BrokerHub/LedgerDetails/getAllLedgerDetails
Authorization: Bearer <token>
Content-Type: application/json

{
  "brokerId": 1
}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getAllLedgerDetails" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{"brokerId": 1}'
```

**Response (200):**
```json
[
  {
    "ledgerDetailsId": 1,
    "brokerTransactionNumber": 1,
    "broker": {
      "brokerId": 1,
      "brokerName": "John Broker"
    },
    "fromSeller": {
      "userId": 10,
      "firmName": "XYZ Mills",
      "ownerName": "Miller Owner"
    },
    "dailyLedger": {
      "dailyLedgerId": 1,
      "date": "2024-01-15"
    },
    "records": [
      {
        "ledgerRecordId": 1,
        "toBuyer": {
          "userId": 20,
          "firmName": "ABC Traders"
        },
        "product": {
          "productId": 1,
          "productName": "Wheat"
        },
        "quantity": 10,
        "brokerage": 50,
        "productCost": 25000,
        "totalProductsCost": 25000,
        "totalBrokerage": 50
      }
    ]
  }
]
```

### 3. Get Ledger Details By ID
```http
GET /BrokerHub/LedgerDetails/getLedgerDetailsById?ledgerDetailId=1&brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsById?ledgerDetailId=1&brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as individual ledger detail from getAllLedgerDetails

### 4. Get Ledger Details By Transaction Number
```http
GET /BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as getLedgerDetailsById

### 5. Get Optimized Ledger Details By ID
```http
GET /BrokerHub/LedgerDetails/getOptimizedLedgerDetailsById?ledgerDetailId=1&brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsById?ledgerDetailId=1&brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "ledgerDetailsId": 1,
  "brokerTransactionNumber": 1,
  "transactionDate": "2024-01-15",
  "fromSeller": {
    "userId": 10,
    "firmName": "XYZ Mills",
    "addressId": 5
  },
  "records": [
    {
      "ledgerRecordId": 1,
      "toBuyer": {
        "userId": 20,
        "firmName": "ABC Traders",
        "addressId": 8
      },
      "product": {
        "productId": 1,
        "productName": "Wheat"
      },
      "quantity": 10,
      "brokerage": 50,
      "productCost": 25000,
      "totalProductsCost": 25000,
      "totalBrokerage": 50
    }
  ],
  "transactionSummary": {
    "totalBagsSoldInTransaction": 25,
    "totalBrokerageInTransaction": 125.00,
    "totalReceivableAmountInTransaction": 62500,
    "averageBrokeragePerBag": 5.00,
    "numberOfProducts": 1,
    "numberOfBuyers": 2
  }
}
```

### 6. Get Optimized Ledger Details By Transaction Number
```http
GET /BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as getOptimizedLedgerDetailsById

### 7. Get Ledger Details By Date
```http
GET /BrokerHub/LedgerDetails/getLedgerDetailsByDate?date=2024-01-15&brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsByDate?date=2024-01-15&brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "brokerName": "John Broker",
    "brokerage": 500,
    "sellerName": "XYZ Mills",
    "date": "2024-01-15",
    "displayLedgerRecordDTOList": [
      {
        "buyerName": "ABC Traders",
        "location": "Mumbai, Andheri",
        "productName": "Wheat",
        "quantity": 10,
        "brokerage": 50,
        "productCost": 25000,
        "total": 25050
      }
    ]
  }
]
```

### 8. Get Ledger Details By Seller
```http
GET /BrokerHub/LedgerDetails/getLedgerDetailsBySeller?sellerId=10&brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsBySeller?sellerId=10&brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "brokerId": 1,
    "brokerage": 500,
    "fromSeller": 10,
    "date": "2024-01-15",
    "ledgerRecordDTOList": [
      {
        "buyerName": "ABC Traders",
        "productId": 1,
        "quantity": 10,
        "brokerage": 50,
        "productCost": 25000
      }
    ]
  }
]
```

### 9. Update Ledger Details By Transaction Number
```http
PUT /BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber?transactionNumber=1&brokerId=1
Authorization: Bearer <token>
Content-Type: application/json

{
  "brokerId": 1,
  "brokerage": 600,
  "fromSeller": 15,
  "date": "2024-01-16",
  "ledgerRecordDTOList": [
    {
      "buyerName": "XYZ Traders",
      "productId": 2,
      "quantity": 20,
      "brokerage": 60,
      "productCost": 30000
    },
    {
      "buyerName": "PQR Trading",
      "productId": 1,
      "quantity": 15,
      "brokerage": 75,
      "productCost": 37500
    }
  ]
}
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber?transactionNumber=1&brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "brokerId": 1,
    "brokerage": 600,
    "fromSeller": 15,
    "date": "2024-01-16",
    "ledgerRecordDTOList": [
      {
        "buyerName": "XYZ Traders",
        "productId": 2,
        "quantity": 20,
        "brokerage": 60,
        "productCost": 30000
      }
    ]
  }'
```

**Response (200):**
```json
"Ledger details updated successfully"
```

**Response (404) - Transaction Not Found:**
```json
"Ledger details not found with transaction number: 1"
```

**Response (400) - Invalid Parameters:**
```json
"Transaction number cannot be null"
```

**Response (500) - Server Error:**
```json
"Failed to update ledger details: <error message>"
```

---

## Dashboard APIs

### 1. Get Financial Year Analytics
```http
GET /BrokerHub/Dashboard/{brokerId}/getFinancialYearAnalytics/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getFinancialYearAnalytics/1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "financialYearId": 1,
  "financialYearName": "FY 2024-25",
  "startDate": "2024-04-01",
  "endDate": "2025-03-31",
  "totalBrokerage": 125000.50,
  "totalQuantity": 2500,
  "totalTransactionValue": 5000000.00,
  "totalTransactions": 150,
  "monthlyAnalytics": [
    {
      "month": "JANUARY",
      "totalBags": 200,
      "totalBrokerage": 10000.00,
      "totalValue": 400000.00,
      "transactionCount": 15,
      "productAnalytics": [
        {
          "productName": "Wheat",
          "totalBags": 120,
          "totalBrokerage": 6000.00,
          "totalValue": 240000.00
        }
      ],
      "cityAnalytics": [
        {
          "city": "Mumbai",
          "totalBags": 150,
          "totalBrokerage": 7500.00,
          "totalValue": 300000.00
        }
      ]
    }
  ],
  "overallProductTotals": [
    {
      "productName": "Wheat",
      "totalBags": 1200,
      "totalBrokerage": 60000.00,
      "totalValue": 2400000.00,
      "averagePrice": 2000.00
    }
  ],
  "overallCityTotals": [
    {
      "city": "Mumbai",
      "totalBags": 1500,
      "totalBrokerage": 75000.00,
      "totalValue": 3000000.00,
      "productBreakdown": [
        {
          "productName": "Wheat",
          "bags": 900,
          "brokerage": 45000.00
        }
      ]
    }
  ],
  "overallMerchantTypeTotals": [
    {
      "merchantType": "TRADER",
      "totalBags": 1800,
      "totalBrokerage": 90000.00,
      "totalValue": 3600000.00,
      "merchantCount": 25
    }
  ]
}
```

### 2. Get Top Performers
```http
GET /BrokerHub/Dashboard/{brokerId}/getTopPerformers/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getTopPerformers/1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "topBuyers": [
    {
      "userId": 20,
      "firmName": "ABC Traders",
      "totalBags": 500,
      "totalBrokerage": 25000.00,
      "totalValue": 1250000.00
    }
  ],
  "topSellers": [
    {
      "userId": 10,
      "firmName": "XYZ Mills",
      "totalBags": 800,
      "totalBrokerage": 40000.00,
      "totalValue": 2000000.00
    }
  ],
  "topProducts": [
    {
      "productName": "Wheat",
      "totalBags": 1200,
      "totalBrokerage": 60000.00,
      "averagePrice": 2500.00
    }
  ]
}
```

### 3. Get Top 5 Buyers By Quantity
```http
GET /BrokerHub/Dashboard/{brokerId}/getTop5BuyersByQuantity/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getTop5BuyersByQuantity/1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "userId": 20,
    "firmName": "ABC Traders",
    "ownerName": "John Doe",
    "city": "Mumbai",
    "totalQuantity": 500,
    "totalBrokerage": 25000.00,
    "averageBrokeragePerBag": 50.00
  }
]
```

### 4. Get Top 5 Sellers By Quantity
```http
GET /BrokerHub/Dashboard/{brokerId}/getTop5SellersByQuantity/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getTop5SellersByQuantity/1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "userId": 10,
    "firmName": "XYZ Mills",
    "ownerName": "Jane Smith",
    "city": "Pune",
    "totalQuantity": 800,
    "totalBrokerage": 40000.00,
    "averageBrokeragePerBag": 50.00
  }
]
```

### 5. Get Top 5 Merchants By Brokerage
```http
GET /BrokerHub/Dashboard/{brokerId}/getTop5MerchantsByBrokerage/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getTop5MerchantsByBrokerage/1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "userId": 15,
    "firmName": "Premium Mills",
    "ownerName": "Robert Johnson",
    "city": "Delhi",
    "userType": "MILLER",
    "totalBrokerage": 75000.00,
    "totalQuantity": 1200,
    "averageBrokeragePerBag": 62.50
  }
]
```

### 6. Refresh Analytics Cache
```http
POST /BrokerHub/Dashboard/refreshCache/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Dashboard/refreshCache/1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
"Analytics cache refreshed successfully for financial year: 1"
```

### 7. Refresh All Analytics Cache
```http
POST /BrokerHub/Dashboard/refreshAllCache
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Dashboard/refreshAllCache" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
"All analytics cache refreshed successfully"
```

---

## Payment APIs

### 1. Get All Firm Names
```http
GET /BrokerHub/payments/firms
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/firms" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "firmName": "ABC Traders",
    "city": "Mumbai"
  },
  {
    "firmName": "XYZ Mills",
    "city": "Pune"
  }
]
```

### 2. Get All Brokerage Payments
```http
GET /BrokerHub/payments/{brokerId}/brokerage
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "merchantId": "10",
      "firmName": "ABC Traders",
      "ownerName": "John Doe",
      "city": "Mumbai",
      "userType": "TRADER",
      "soldBags": 80,
      "boughtBags": 120,
      "totalBags": 200,
      "brokerageRate": 10.00,
      "grossBrokerage": 2000.00,
      "discount": 200.00,
      "tds": 100.00,
      "netBrokerage": 1700.00,
      "paidAmount": 800.00,
      "pendingAmount": 900.00,
      "lastPaymentDate": "2024-01-10",
      "dueDate": "2024-02-15",
      "status": "PARTIAL_PAID",
      "partPayments": [
        {
          "id": 1,
          "amount": 500.00,
          "paymentDate": "2024-01-05",
          "method": "CASH",
          "notes": "First installment"
        }
      ],
      "phoneNumber": "9876543210",
      "email": "john@abctraders.com",
      "gstNumber": "GST123456789",
      "daysUntilDue": 15,
      "daysOverdue": 0,
      "paymentCompletionPercentage": 47.06
    }
  ]
}
```

### 3. Search Brokerage Payments
```http
GET /BrokerHub/payments/{brokerId}/brokerage/search?firmName=ABC
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage/search?firmName=ABC" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as Get All Brokerage Payments but filtered by firm name

### 4. Add Part Payment
```http
POST /BrokerHub/payments/{brokerId}/brokerage/{paymentId}/part-payment
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 1000.00,
  "paymentMethod": "CASH",
  "notes": "Partial payment received",
  "paymentDate": "2024-01-15"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/brokerage/1/part-payment" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00,
    "paymentMethod": "CASH",
    "notes": "Partial payment received",
    "paymentDate": "2024-01-15"
  }'
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Part payment added successfully",
  "paymentId": 1,
  "newPendingAmount": 700.00
}
```

### 5. Get All Pending Payments
```http
GET /BrokerHub/payments/{brokerId}/pending
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/pending" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "data": [
    {
      "transactionId": 1,
      "buyerFirm": "ABC Traders",
      "sellerFirm": "XYZ Mills",
      "productName": "Wheat",
      "quantity": 100,
      "totalAmount": 250000.00,
      "pendingAmount": 250000.00,
      "transactionDate": "2024-01-15",
      "dueDate": "2024-02-15",
      "daysOverdue": 0,
      "status": "PENDING"
    }
  ]
}
```

### 6. Search Pending Payments
```http
GET /BrokerHub/payments/{brokerId}/pending/search?buyerFirm=ABC
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/pending/search?buyerFirm=ABC" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as Get All Pending Payments but filtered by buyer firm

### 7. Get All Receivable Payments
```http
GET /BrokerHub/payments/{brokerId}/receivable
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/receivable" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "data": [
    {
      "transactionId": 2,
      "sellerFirm": "Premium Mills",
      "buyerFirm": "Quality Traders",
      "productName": "Rice",
      "quantity": 150,
      "totalAmount": 450000.00,
      "receivableAmount": 450000.00,
      "transactionDate": "2024-01-20",
      "expectedDate": "2024-02-20",
      "status": "RECEIVABLE"
    }
  ]
}
```

### 8. Search Receivable Payments
```http
GET /BrokerHub/payments/{brokerId}/receivable/search?sellerFirm=XYZ
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/receivable/search?sellerFirm=XYZ" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as Get All Receivable Payments but filtered by seller firm

### 9. Get Payment Dashboard Statistics
```http
GET /BrokerHub/payments/{brokerId}/dashboard
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/dashboard" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "totalBrokerageEarned": 125000.00,
    "totalBrokeragePaid": 75000.00,
    "totalBrokeragePending": 50000.00,
    "totalPendingPayments": 250000.00,
    "totalReceivablePayments": 180000.00,
    "overduePayments": 25000.00,
    "paymentCompletionRate": 60.0,
    "averagePaymentDelay": 5.2
  }
}
```

### 10. Get Payment Summary
```http
GET /BrokerHub/payments/{brokerId}/summary
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/summary" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "data": {
    "monthlyBrokerageSummary": [
      {
        "month": "JANUARY",
        "totalBrokerage": 25000.00,
        "paidBrokerage": 15000.00,
        "pendingBrokerage": 10000.00
      }
    ],
    "topPayingClients": [
      {
        "firmName": "ABC Traders",
        "totalPaid": 45000.00,
        "paymentScore": 95.5
      }
    ],
    "overdueClients": [
      {
        "firmName": "Slow Payers Ltd",
        "overdueAmount": 15000.00,
        "daysPastDue": 30
      }
    ]
  }
}
```

### 11. Generate Payment Data from Ledger
```http
POST /BrokerHub/payments/{brokerId}/generate-from-ledger/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/generate-from-ledger/1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Payment data generated successfully from ledger",
  "recordsGenerated": 45,
  "totalBrokerageAmount": 125000.00
}
```

---

## Product APIs

### 1. Create Product
```http
POST /BrokerHub/Product/createProduct
Authorization: Bearer <token>
Content-Type: application/json

{
  "productName": "Wheat",
  "productBrokerage": 5.0,
  "quantity": 1000,
  "price": 2500,
  "quality": "Premium"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Product/createProduct" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Wheat",
    "productBrokerage": 5.0,
    "quantity": 1000,
    "price": 2500,
    "quality": "Premium"
  }'
```

**Response (201):**
```json
"Product created successfully"
```

### 2. Update Product
```http
PUT /BrokerHub/Product/updateProduct
Authorization: Bearer <token>
Content-Type: application/json

{
  "productId": 1,
  "productName": "Basmati Rice",
  "productBrokerage": 12.0,
  "quantity": 500,
  "price": 3000,
  "quality": "Premium"
}
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/BrokerHub/Product/updateProduct" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "productName": "Basmati Rice",
    "productBrokerage": 12.0,
    "quantity": 500,
    "price": 3000,
    "quality": "Premium"
  }'
```

**Response (200):**
```json
"Product updated successfully"
```

### 3. Delete Product
```http
DELETE /BrokerHub/Product/deleteProduct?productId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X DELETE "http://localhost:8080/BrokerHub/Product/deleteProduct?productId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
"Product deleted successfully"
```

### 4. Get All Products
```http
GET /BrokerHub/Product/allProducts
Authorization: Bearer <token>
```

**Response (200):**
```json
[
  {
    "productId": 1,
    "broker": {
      "brokerId": 1,
      "brokerName": "Main Broker",
      "brokerageFirmName": "ABC Brokerage"
    },
    "productName": "Wheat",
    "productBrokerage": 5.0,
    "quantity": 1000,
    "price": 2500,
    "quality": "Premium",
    "imgLink": "https://example.com/wheat.jpg"
  }
]
```

### 5. Get Products By Name
```http
GET /BrokerHub/Product/allProducts/?productName=Wheat
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Product/allProducts/?productName=Wheat" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as Get All Products but filtered by product name

### 6. Get Product Names
```http
GET /BrokerHub/Product/getProductNames
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Product/getProductNames" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  "Wheat",
  "Rice",
  "Barley",
  "Corn"
]
```

### 7. Get Distinct Product Names
```http
GET /BrokerHub/Product/getDistinctProductNames
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Product/getDistinctProductNames" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  "Wheat",
  "Rice",
  "Barley"
]
```

### 8. Get Product Names and IDs
```http
GET /BrokerHub/Product/getProductNamesAndIds
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Product/getProductNamesAndIds" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "productId": 1,
    "productName": "Wheat"
  },
  {
    "productId": 2,
    "productName": "Rice"
  }
]
```

### 9. Get Basic Product Info
```http
GET /BrokerHub/Product/getBasicProductInfo
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Product/getBasicProductInfo" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "productId": 1,
    "productName": "Wheat",
    "productBrokerage": 5.0
  },
  {
    "productId": 2,
    "productName": "Rice",
    "productBrokerage": 8.0
  }
]
```

### 10. Get Product Names and Qualities
```http
GET /BrokerHub/Product/getProductNamesAndQualities
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Product/getProductNamesAndQualities" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "productName": "Wheat",
    "quality": "Premium"
  },
  {
    "productName": "Rice",
    "quality": "Grade A"
  }
]
```

### 11. Get Product Names, Qualities and Quantities with ID
```http
GET /BrokerHub/Product/getProductNamesAndQualitiesAndQuantitesWithId
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Product/getProductNamesAndQualitiesAndQuantitesWithId" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "productId": 1,
    "productName": "Wheat",
    "quality": "Premium",
    "quantity": 1000
  },
  {
    "productId": 2,
    "productName": "Rice",
    "quality": "Grade A",
    "quantity": 500
  }
]
```

---

## User Management APIs

### 1. Create User
```http
POST /BrokerHub/user/createUser
Content-Type: application/json

{
  "userType": "TRADER",
  "gstNumber": "GST123456789",
  "firmName": "XYZ Traders",
  "ownerName": "Jane Smith",
  "city": "Mumbai",
  "area": "Andheri",
  "pincode": "400001",
  "email": "jane@xyztraders.com",
  "phoneNumbers": ["9876543210", "9876543211"],
  "brokerageRate": 10
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/user/createUser" \
  -H "Content-Type: application/json" \
  -d '{
    "userType": "TRADER",
    "gstNumber": "GST123456789",
    "firmName": "XYZ Traders",
    "ownerName": "Jane Smith",
    "city": "Mumbai",
    "area": "Andheri",
    "pincode": "400001",
    "email": "jane@xyztraders.com",
    "phoneNumbers": ["9876543210", "9876543211"],
    "brokerageRate": 10
  }'
```

**Response (201):**
```json
"User created successfully"
```

### 2. Bulk Upload Users
```http
POST /BrokerHub/user/bulkUpload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <excel-file>
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/user/bulkUpload" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F "file=@users.xlsx"
```

**Response (200):**
```json
{
  "totalRecords": 15,
  "successfulRecords": 12,
  "failedRecords": 3,
  "errorMessages": [
    "Row 5: Firm name is required",
    "Row 8: Invalid email format",
    "Row 12: Phone number is required"
  ],
  "message": "Partial success: 12 users uploaded, 3 failed"
}
```

### 3. Download Template
```http
GET /BrokerHub/user/downloadTemplate
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/downloadTemplate" \
  -o user_template.xlsx
```

**Response:** Excel file download with user template structure

### 4. Update User
```http
PUT /BrokerHub/user/updateUser
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "firmName": "Updated Traders",
  "ownerName": "John Updated",
  "email": "updated@traders.com"
}
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/BrokerHub/user/updateUser" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "firmName": "Updated Traders",
    "ownerName": "John Updated",
    "email": "updated@traders.com"
  }'
```

**Response (200):**
```json
"User updated successfully"
```

### 5. Delete User
```http
DELETE /BrokerHub/user/deleteUser/?Id=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X DELETE "http://localhost:8080/BrokerHub/user/deleteUser/?Id=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
"User deleted successfully"
```

### 6. Get All Users
```http
GET /BrokerHub/user/allUsers
Authorization: Bearer <token>
```

**Response (200):**
```json
[
  {
    "userId": 1,
    "userType": "TRADER",
    "gstNumber": "GST123456789",
    "firmName": "ABC Traders",
    "ownerName": "John Doe",
    "broker": {
      "brokerId": 1,
      "brokerName": "Main Broker"
    },
    "address": {
      "addressId": 5,
      "city": "Mumbai",
      "area": "Andheri",
      "pincode": "400001"
    },
    "email": "john@abctraders.com",
    "phoneNumbers": ["9876543210"],
    "brokerageRate": 10,
    "totalBagsSold": 150,
    "totalBagsBought": 200,
    "payableAmount": 75000,
    "receivableAmount": 125000,
    "totalPayableBrokerage": 3500.00
  }
]
```

### 7. Get Users By City
```http
GET /BrokerHub/user/allUsers/?city=Mumbai
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/allUsers/?city=Mumbai" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as Get All Users but filtered by city

### 8. Get User By ID
```http
GET /BrokerHub/user/{userId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/10" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "userId": 10,
  "userType": "TRADER",
  "gstNumber": "GST123456789",
  "firmName": "ABC Traders",
  "ownerName": "John Doe",
  "broker": {
    "brokerId": 1,
    "brokerName": "Main Broker"
  },
  "address": {
    "addressId": 5,
    "city": "Mumbai",
    "area": "Andheri",
    "pincode": "400001"
  },
  "email": "john@abctraders.com",
  "phoneNumbers": ["9876543210"],
  "brokerageRate": 10,
  "totalBagsSold": 150,
  "totalBagsBought": 200,
  "payableAmount": 75000,
  "receivableAmount": 125000,
  "totalPayableBrokerage": 3500.00
}
```

### 9. Get Users Having Brokerage More Than
```http
GET /BrokerHub/user/brokerageMoreThan/?brokerage=100
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/brokerageMoreThan/?brokerage=100" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "userId": 15,
    "userType": "MILLER",
    "gstNumber": "GST987654321",
    "firmName": "Premium Mills",
    "ownerName": "Jane Smith",
    "broker": {
      "brokerId": 1,
      "brokerName": "Main Broker"
    },
    "address": {
      "addressId": 8,
      "city": "Pune",
      "area": "Hadapsar",
      "pincode": "411028"
    },
    "email": "jane@premiummills.com",
    "phoneNumbers": ["9876543220"],
    "brokerageRate": 150,
    "totalBagsSold": 300,
    "totalBagsBought": 50,
    "payableAmount": 125000,
    "receivableAmount": 200000,
    "totalPayableBrokerage": 5250.00
  }
]
```

### 10. Get Users Having Brokerage In Range
```http
GET /BrokerHub/user/brokerageInRange/?min=50&max=200
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/brokerageInRange/?min=50&max=200" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "userId": 12,
    "userType": "TRADER",
    "gstNumber": "GST456789123",
    "firmName": "Mid Range Traders",
    "ownerName": "Mike Johnson",
    "broker": {
      "brokerId": 1,
      "brokerName": "Main Broker"
    },
    "address": {
      "addressId": 6,
      "city": "Delhi",
      "area": "Connaught Place",
      "pincode": "110001"
    },
    "email": "mike@midrangetraders.com",
    "phoneNumbers": ["9876543230"],
    "brokerageRate": 75,
    "totalBagsSold": 200,
    "totalBagsBought": 150,
    "payableAmount": 95000,
    "receivableAmount": 110000,
    "totalPayableBrokerage": 2625.00
  }
]
```

### 11. Get User By Property
```http
GET /BrokerHub/user/?property=firmName&value=ABC
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/?property=firmName&value=ABC" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):** Same structure as Get All Users but filtered by specified property and value

### 12. Get User Names and IDs
```http
GET /BrokerHub/user/getUserNamesAndIds
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/getUserNamesAndIds" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "userId": 1,
    "firmName": "ABC Traders"
  },
  {
    "userId": 2,
    "firmName": "XYZ Mills"
  }
]
```

### 13. Get User Names
```http
GET /BrokerHub/user/getUserNames
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/getUserNames" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  "ABC Traders",
  "XYZ Mills",
  "Premium Traders",
  "Quality Mills"
]
```

---

## Financial Year APIs

### 1. Create Financial Year
```http
POST /BrokerHub/FinancialYear/create
Authorization: Bearer <token>
Content-Type: application/json

{
  "start": "2024-04-01",
  "end": "2025-03-31",
  "financialYearName": "FY 2024-25",
  "forBills": false
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/FinancialYear/create" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "start": "2024-04-01",
    "end": "2025-03-31",
    "financialYearName": "FY 2024-25",
    "forBills": false
  }'
```

**Response (201):**
```json
1
```

### 2. Get All Financial Year IDs
```http
GET /BrokerHub/FinancialYear/getAllFinancialYearIds
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/FinancialYear/getAllFinancialYearIds" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[1, 2, 3, 4]
```

### 3. Get All Financial Years
```http
GET /BrokerHub/FinancialYear/getAllFinancialYears
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/FinancialYear/getAllFinancialYears" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "yearId": 1,
    "financialYearName": "FY 2024-25",
    "start": "2024-04-01",
    "end": "2025-03-31",
    "forBills": false
  },
  {
    "yearId": 2,
    "financialYearName": "FY 2023-24",
    "start": "2023-04-01",
    "end": "2024-03-31",
    "forBills": true
  }
]
```

---

## Address APIs

### 1. Get All Addresses
```http
GET /BrokerHub/Address/getAllAddresses
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Address/getAllAddresses" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "addressId": 1,
    "city": "Mumbai",
    "area": "Andheri",
    "pincode": "400001"
  },
  {
    "addressId": 2,
    "city": "Pune",
    "area": "Hadapsar",
    "pincode": "411028"
  }
]
```

### 2. Create Address
```http
POST /BrokerHub/Address/createAddress
Authorization: Bearer <token>
Content-Type: application/json

{
  "city": "Mumbai",
  "area": "Andheri",
  "pincode": "400001"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Address/createAddress" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "city": "Mumbai",
    "area": "Andheri",
    "pincode": "400001"
  }'
```

**Response (201):**
```json
"Address created successfully"
```

### 3. Update Address
```http
PUT /BrokerHub/Address/updateAddress
Authorization: Bearer <token>
Content-Type: application/json

{
  "addressId": 1,
  "city": "Mumbai",
  "area": "Bandra",
  "pincode": "400050"
}
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/BrokerHub/Address/updateAddress" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "addressId": 1,
    "city": "Mumbai",
    "area": "Bandra",
    "pincode": "400050"
  }'
```

**Response (200):**
```json
"Address updated successfully"
```

---

## Test APIs

### 1. Health Check
```http
GET /BrokerHub/Test/health
```

**Response (200):**
```json
"Dashboard service is running!"
```

### 2. Get Financial Years
```http
GET /BrokerHub/Test/financialYears
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Test/financialYears"
```

**Response (200):**
```json
[
  {
    "yearId": 1,
    "financialYearName": "FY 2024-25",
    "start": "2024-04-01",
    "end": "2025-03-31"
  }
]
```

### 3. Test Basic Query
```http
GET /BrokerHub/Test/testBasicQuery/{financialYearId}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Test/testBasicQuery/1"
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Basic query test completed",
  "financialYearId": 1,
  "recordCount": 150
}
```

### 4. Test Overall Totals
```http
GET /BrokerHub/Test/testOverallTotals/{financialYearId}
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Test/testOverallTotals/1"
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Overall totals test completed",
  "financialYearId": 1,
  "totalBrokerage": 125000.00,
  "totalQuantity": 2500,
  "totalTransactions": 150
}
```

---

## 📊 Response Status Codes

| Code | Status | Meaning |
|------|--------|---------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Authentication required |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 500 | Internal Server Error | Server error |

---

## 📝 Notes

1. **Date Format**: Use `YYYY-MM-DD` format for all date fields
2. **Pagination**: Default page=0, size=10, max size=100
3. **Sorting**: Use `asc` or `desc` for sortDir parameter
4. **Multi-tenant**: All APIs are broker-isolated automatically
5. **File Uploads**: Use `multipart/form-data` content type

---

This comprehensive reference covers ALL API endpoints from every controller with accurate request/response structures based on actual DTO implementations.