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

**Response (200):**
```json
true
```

#### 4. Check Firm Name Availability
```http
GET /BrokerHub/Broker/BrokerFirmNameExists/{firmName}
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

---

## 🔒 Protected Endpoints (Authentication Required)

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

**Response (200):** Same as getDailyLedger

### 4. Get Optimized Daily Ledger
```http
GET /BrokerHub/DailyLedger/getOptimizedDailyLedger?date=2024-01-15
Authorization: Bearer <token>
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

**Response (200):** Same structure as individual ledger detail from getAllLedgerDetails

### 4. Get Ledger Details By Transaction Number
```http
GET /BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1
Authorization: Bearer <token>
```

**Response (200):** Same structure as getLedgerDetailsById

### 5. Get Optimized Ledger Details By ID
```http
GET /BrokerHub/LedgerDetails/getOptimizedLedgerDetailsById?ledgerDetailId=1&brokerId=1
Authorization: Bearer <token>
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

**Response (200):** Same structure as getOptimizedLedgerDetailsById

### 7. Get Ledger Details By Date
```http
GET /BrokerHub/LedgerDetails/getLedgerDetailsByDate?date=2024-01-15&brokerId=1
Authorization: Bearer <token>
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

### 3. Get Top 5 Buyers By Quantity
```http
GET /BrokerHub/Dashboard/{brokerId}/getTop5BuyersByQuantity/{financialYearId}
Authorization: Bearer <token>
```

### 4. Get Top 5 Sellers By Quantity
```http
GET /BrokerHub/Dashboard/{brokerId}/getTop5SellersByQuantity/{financialYearId}
Authorization: Bearer <token>
```

### 5. Get Top 5 Merchants By Brokerage
```http
GET /BrokerHub/Dashboard/{brokerId}/getTop5MerchantsByBrokerage/{financialYearId}
Authorization: Bearer <token>
```

### 6. Refresh Analytics Cache
```http
POST /BrokerHub/Dashboard/refreshCache/{financialYearId}
Authorization: Bearer <token>
```

### 7. Refresh All Analytics Cache
```http
POST /BrokerHub/Dashboard/refreshAllCache
Authorization: Bearer <token>
```

---

## Payment APIs

### 1. Get All Firm Names
```http
GET /BrokerHub/payments/firms
Authorization: Bearer <token>
```

### 2. Get All Brokerage Payments
```http
GET /BrokerHub/payments/{brokerId}/brokerage
Authorization: Bearer <token>
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

### 5. Get All Pending Payments
```http
GET /BrokerHub/payments/{brokerId}/pending
Authorization: Bearer <token>
```

### 6. Search Pending Payments
```http
GET /BrokerHub/payments/{brokerId}/pending/search?buyerFirm=ABC
Authorization: Bearer <token>
```

### 7. Get All Receivable Payments
```http
GET /BrokerHub/payments/{brokerId}/receivable
Authorization: Bearer <token>
```

### 8. Search Receivable Payments
```http
GET /BrokerHub/payments/{brokerId}/receivable/search?sellerFirm=XYZ
Authorization: Bearer <token>
```

### 9. Get Payment Dashboard Statistics
```http
GET /BrokerHub/payments/{brokerId}/dashboard
Authorization: Bearer <token>
```

### 10. Get Payment Summary
```http
GET /BrokerHub/payments/{brokerId}/summary
Authorization: Bearer <token>
```

### 11. Generate Payment Data from Ledger
```http
POST /BrokerHub/payments/{brokerId}/generate-from-ledger/{financialYearId}
Authorization: Bearer <token>
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

### 3. Delete Product
```http
DELETE /BrokerHub/Product/deleteProduct?productId=1
Authorization: Bearer <token>
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

### 6. Get Product Names
```http
GET /BrokerHub/Product/getProductNames
Authorization: Bearer <token>
```

### 7. Get Distinct Product Names
```http
GET /BrokerHub/Product/getDistinctProductNames
Authorization: Bearer <token>
```

### 8. Get Product Names and IDs
```http
GET /BrokerHub/Product/getProductNamesAndIds
Authorization: Bearer <token>
```

### 9. Get Basic Product Info
```http
GET /BrokerHub/Product/getBasicProductInfo
Authorization: Bearer <token>
```

### 10. Get Product Names and Qualities
```http
GET /BrokerHub/Product/getProductNamesAndQualities
Authorization: Bearer <token>
```

### 11. Get Product Names, Qualities and Quantities with ID
```http
GET /BrokerHub/Product/getProductNamesAndQualitiesAndQuantitesWithId
Authorization: Bearer <token>
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

### 2. Bulk Upload Users
```http
POST /BrokerHub/user/bulkUpload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <excel-file>
```

### 3. Download Template
```http
GET /BrokerHub/user/downloadTemplate
```

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

### 5. Delete User
```http
DELETE /BrokerHub/user/deleteUser/?Id=1
Authorization: Bearer <token>
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

### 8. Get User By ID
```http
GET /BrokerHub/user/{userId}
Authorization: Bearer <token>
```

### 9. Get Users Having Brokerage More Than
```http
GET /BrokerHub/user/brokerageMoreThan/?brokerage=100
Authorization: Bearer <token>
```

### 10. Get Users Having Brokerage In Range
```http
GET /BrokerHub/user/brokerageInRange/?min=50&max=200
Authorization: Bearer <token>
```

### 11. Get User By Property
```http
GET /BrokerHub/user/?property=firmName&value=ABC
Authorization: Bearer <token>
```

### 12. Get User Names and IDs
```http
GET /BrokerHub/user/getUserNamesAndIds
Authorization: Bearer <token>
```

### 13. Get User Names
```http
GET /BrokerHub/user/getUserNames
Authorization: Bearer <token>
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

### 2. Get All Financial Year IDs
```http
GET /BrokerHub/FinancialYear/getAllFinancialYearIds
Authorization: Bearer <token>
```

### 3. Get All Financial Years
```http
GET /BrokerHub/FinancialYear/getAllFinancialYears
Authorization: Bearer <token>
```

---

## Address APIs

### 1. Get All Addresses
```http
GET /BrokerHub/Address/getAllAddresses
Authorization: Bearer <token>
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

### 3. Test Basic Query
```http
GET /BrokerHub/Test/testBasicQuery/{financialYearId}
```

### 4. Test Overall Totals
```http
GET /BrokerHub/Test/testOverallTotals/{financialYearId}
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