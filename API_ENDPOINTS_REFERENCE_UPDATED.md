# 🔗 BrokerHub API Endpoints Reference

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

### 1. Broker Registration
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

**Response (208):**
```json
"Broker already exists"
```

### 2. User Login
```http
POST /BrokerHub/Broker/login
Content-Type: application/json

{
  "userName": "broker123",
  "password": "securePassword"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/BrokerHub/Broker/login \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "broker123",
    "password": "securePassword"
  }'
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

**Response (401):**
```json
"Invalid username or password"
```

### 3. Create Password
```http
POST /BrokerHub/Broker/createPassword
Content-Type: application/json

{
  "email": "john@abctrading.com",
  "password": "newSecurePassword"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/BrokerHub/Broker/createPassword \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@abctrading.com",
    "password": "newSecurePassword"
  }'
```

**Response (201):**
```json
"Password created"
```

### 4. Verify Account
```http
POST /BrokerHub/Broker/verify-account?userName=broker123&otp=123456
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Broker/verify-account?userName=broker123&otp=123456"
```

**Response (200):**
```json
"OTP verified you can change password now"
```

### 5. Forgot Password
```http
GET /BrokerHub/Broker/forgotPassword?userName=broker123
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Broker/forgotPassword?userName=broker123"
```

**Response (200):**
```json
"OTP send successfully"
```

**Response (400):**
```json
"No user found with provided user name"
```

### 6. Check Username Availability
```http
GET /BrokerHub/Broker/UserNameExists/broker123
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/Broker/UserNameExists/broker123
```

**Response (200):**
```json
true
```

### 7. Check Firm Name Availability
```http
GET /BrokerHub/Broker/BrokerFirmNameExists/ABC%20Trading
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Broker/BrokerFirmNameExists/ABC%20Trading"
```

**Response (200):**
```json
false
```

### 8. Create User
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
curl -X POST http://localhost:8080/BrokerHub/user/createUser \
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

**Response (400):**
```json
"Firm name is required"
```

---

## 🔒 Protected Endpoints (Authentication Required)

### Dashboard APIs

#### 1. Get Financial Year Analytics
```http
GET /BrokerHub/Dashboard/{brokerId}/getFinancialYearAnalytics/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/Dashboard/1/getFinancialYearAnalytics/1 \
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
    },
    {
      "productName": "Rice",
      "totalBags": 800,
      "totalBrokerage": 40000.00,
      "totalValue": 1600000.00,
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
    },
    {
      "merchantType": "MILLER",
      "totalBags": 700,
      "totalBrokerage": 35000.00,
      "totalValue": 1400000.00,
      "merchantCount": 8
    }
  ]
}
```

#### 2. Get Top Performers
```http
GET /BrokerHub/Dashboard/{brokerId}/getTopPerformers/{financialYearId}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/Dashboard/1/getTopPerformers/1 \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "topBuyers": [
    {
      "firmName": "ABC Traders",
      "totalBags": 500,
      "totalBrokerage": 25000.00
    }
  ],
  "topSellers": [
    {
      "firmName": "XYZ Mills",
      "totalBags": 800,
      "totalBrokerage": 40000.00
    }
  ]
}
```

### Product APIs

#### 1. Get All Products
```http
GET /BrokerHub/Product/allProducts
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/Product/allProducts \
  -H "Authorization: Bearer <your-jwt-token>"
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
  },
  {
    "productId": 2,
    "broker": {
      "brokerId": 1,
      "brokerName": "Main Broker",
      "brokerageFirmName": "ABC Brokerage"
    },
    "productName": "Rice",
    "productBrokerage": 8.0,
    "quantity": 800,
    "price": 3000,
    "quality": "Basmati",
    "imgLink": "https://example.com/rice.jpg"
  },
  {
    "productId": 3,
    "broker": {
      "brokerId": 1,
      "brokerName": "Main Broker",
      "brokerageFirmName": "ABC Brokerage"
    },
    "productName": "Cotton",
    "productBrokerage": 12.0,
    "quantity": 500,
    "price": 4500,
    "quality": "Long Staple",
    "imgLink": null
  }
]
```

#### 2. Create Product
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
curl -X POST http://localhost:8080/BrokerHub/Product/createProduct \
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
{
  "status": "success",
  "message": "Product created successfully",
  "productId": 1
}
```

#### 3. Get Product Names
```http
GET /BrokerHub/Product/getProductNames
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/Product/getProductNames \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
["Wheat", "Rice", "Cotton", "Sugar"]
```

### Ledger APIs

#### 1. Create Ledger Details
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

**Response (200):**
```json
{
  "status": "success",
  "message": "Ledger details created successfully",
  "ledgerDetailsId": 123
}
```

#### 2. Get Daily Ledger
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
            "ownerName": "Trader Owner",
            "address": {
              "city": "Mumbai",
              "area": "Andheri",
              "pincode": "400001"
            }
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

#### 3. Get Optimized Daily Ledger
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
        },
        {
          "ledgerRecordId": 2,
          "toBuyer": {
            "userId": 21,
            "firmName": "PQR Trading",
            "addressId": 9
          },
          "product": {
            "productId": 1,
            "productName": "Wheat"
          },
          "quantity": 15,
          "brokerage": 75,
          "productCost": 37500,
          "totalProductsCost": 37500,
          "totalBrokerage": 75
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

### Payment APIs

#### 1. Get All Brokerage Payments
```http
GET /BrokerHub/payments/{brokerId}/brokerage
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/payments/1/brokerage \
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
        },
        {
          "id": 2,
          "amount": 300.00,
          "paymentDate": "2024-01-10",
          "method": "UPI",
          "notes": "Second installment"
        }
      ],
      "notes": "Regular customer",
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

#### 2. Search Brokerage Payments
```http
GET /BrokerHub/payments/{brokerId}/brokerage/search?firmName=ABC
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage/search?firmName=ABC" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "merchantFirmName": "ABC Traders",
      "pendingAmount": 2250.00,
      "status": "PARTIAL_PAID"
    }
  ]
}
```

#### 3. Add Part Payment
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
curl -X POST http://localhost:8080/BrokerHub/payments/1/brokerage/1/part-payment \
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
  "data": {
    "paymentId": 1,
    "partPaymentId": 123,
    "amount": 1000.00,
    "remainingAmount": 1250.00,
    "paymentStatus": "PARTIAL_PAID"
  }
}
```

### User Management APIs

#### 1. Get All Users
```http
GET /BrokerHub/user/allUsers
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/user/allUsers \
  -H "Authorization: Bearer <your-jwt-token>"
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
    "bankDetails": {
      "merchantBankDetailsId": 10,
      "accountNumber": "1234567890",
      "ifscCode": "HDFC0001234",
      "bankName": "HDFC Bank",
      "branch": "Andheri Branch"
    },
    "phoneNumbers": ["9876543210", "9876543211"],
    "brokerageRate": 10,
    "totalBagsSold": 150,
    "totalBagsBought": 200,
    "payableAmount": 75000,
    "receivableAmount": 125000,
    "totalPayableBrokerage": 3500.00,
    "shopNumber": "Shop-101",
    "addressHint": "Near Metro Station",
    "collectionRote": "Route-A"
  },
  {
    "userId": 2,
    "userType": "MILLER",
    "gstNumber": "GST987654321",
    "firmName": "XYZ Mills",
    "ownerName": "Jane Smith",
    "broker": {
      "brokerId": 1,
      "brokerName": "Main Broker"
    },
    "address": {
      "addressId": 6,
      "city": "Pune",
      "area": "Hadapsar",
      "pincode": "411028"
    },
    "email": "jane@xyzmills.com",
    "phoneNumbers": ["9876543220"],
    "brokerageRate": 8,
    "totalBagsSold": 300,
    "totalBagsBought": 50,
    "payableAmount": 25000,
    "receivableAmount": 200000,
    "totalPayableBrokerage": 2800.00,
    "byProduct": "Wheat Flour"
  }
]
```

#### 2. Get User Names and IDs
```http
GET /BrokerHub/user/getUserNamesAndIds
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/user/getUserNamesAndIds \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {"ABC Traders": 1},
  {"XYZ Mills": 2},
  {"PQR Trading": 3}
]
```

#### 3. Bulk Upload Users
```http
POST /BrokerHub/user/bulkUpload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <excel-file>
```

**cURL:**
```bash
curl -X POST http://localhost:8080/BrokerHub/user/bulkUpload \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F "file=@users.xlsx"
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Bulk upload completed",
  "totalProcessed": 50,
  "successful": 48,
  "failed": 2,
  "errors": [
    {
      "row": 5,
      "error": "Firm name already exists"
    }
  ]
}
```

### Financial Year APIs

#### 1. Create Financial Year
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
curl -X POST http://localhost:8080/BrokerHub/FinancialYear/create \
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
{
  "status": "success",
  "message": "Financial year created successfully",
  "financialYearId": 1
}
```

#### 2. Get All Financial Years
```http
GET /BrokerHub/FinancialYear/getAllFinancialYears
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/FinancialYear/getAllFinancialYears \
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
  }
]
```

### Address APIs

#### 1. Get All Addresses
```http
GET /BrokerHub/Address/getAllAddresses
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/Address/getAllAddresses \
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
  }
]
```

#### 2. Create Address
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
curl -X POST http://localhost:8080/BrokerHub/Address/createAddress \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "city": "Mumbai",
    "area": "Andheri",
    "pincode": "400001"
  }'
```

**Response (200):**
```json
"Address created successfully with id: 123"
```

**Response (409):**
```json
"Address already exists for this broker"
```

---

## 🆕 Additional Missing Endpoints

### Bank Details APIs

#### 1. Create Broker Bank Details
```http
POST /BrokerHub/BankDetails/createBrokerBankDetails
Authorization: Bearer <token>
Content-Type: application/json

{
  "accountNumber": "1234567890",
  "ifscCode": "HDFC0001234",
  "bankName": "HDFC Bank",
  "branch": "Andheri Branch"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/BrokerHub/BankDetails/createBrokerBankDetails \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "1234567890",
    "ifscCode": "HDFC0001234",
    "bankName": "HDFC Bank",
    "branch": "Andheri Branch"
  }'
```

**Response (201):**
```json
{
  "status": "success",
  "message": "Broker bank details created successfully",
  "bankDetailsId": 123
}
```

#### 2. Get Bank Details by IFSC
```http
GET /BrokerHub/BankDetails/getByIfsc/{ifscCode}
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET http://localhost:8080/BrokerHub/BankDetails/getByIfsc/HDFC0001234 \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "ifscCode": "HDFC0001234",
  "bankName": "HDFC Bank",
  "branch": "Andheri Branch",
  "bankAddress": "Mumbai, Maharashtra",
  "rtgs": true,
  "neft": true,
  "imps": true,
  "upi": true
}
```

### Ledger Record APIs

#### 1. Get Ledger Records by Date Range
```http
GET /BrokerHub/LedgerRecord/getByDateRange?startDate=2024-01-01&endDate=2024-01-31&brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerRecord/getByDateRange?startDate=2024-01-01&endDate=2024-01-31&brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
[
  {
    "ledgerRecordId": 1,
    "buyerName": "ABC Traders",
    "productName": "Rice",
    "quantity": 10,
    "brokerage": 100,
    "productCost": 25000,
    "totalBrokerage": 100,
    "transactionDate": "2024-01-15"
  }
]
```

#### 2. Update Ledger Record
```http
PUT /BrokerHub/LedgerRecord/update/{recordId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "quantity": 15,
  "brokerage": 150,
  "productCost": 37500
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/BrokerHub/LedgerRecord/update/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 15,
    "brokerage": 150,
    "productCost": 37500
  }'
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Ledger record updated successfully",
  "recordId": 1
}
```

### Product Management APIs

#### 1. Update Product
```http
PUT /BrokerHub/Product/updateProduct/{productId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "productName": "Basmati Rice",
  "productBrokerage": 12.0,
  "quantity": 500,
  "price": 3000,
  "quality": "Premium"
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/BrokerHub/Product/updateProduct/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Basmati Rice",
    "productBrokerage": 12.0,
    "quantity": 500,
    "price": 3000,
    "quality": "Premium"
  }'
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Product updated successfully",
  "productId": 1
}
```

#### 2. Delete Product
```http
DELETE /BrokerHub/Product/deleteProduct/{productId}?brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X DELETE "http://localhost:8080/BrokerHub/Product/deleteProduct/1?brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "message": "Product deleted successfully"
}
```

### User Management APIs

#### 1. Update User
```http
PUT /BrokerHub/user/updateUser/{userId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firmName": "Updated Traders",
  "ownerName": "John Updated",
  "email": "updated@traders.com",
  "phoneNumbers": ["9876543210"],
  "brokerageRate": 15
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/BrokerHub/user/updateUser/1 \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "firmName": "Updated Traders",
    "ownerName": "John Updated",
    "email": "updated@traders.com",
    "phoneNumbers": ["9876543210"],
    "brokerageRate": 15
  }'
```

**Response (200):**
```json
{
  "status": "success",
  "message": "User updated successfully",
  "userId": 1
}
```

#### 2. Delete User
```http
DELETE /BrokerHub/user/deleteUser/{userId}?brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X DELETE "http://localhost:8080/BrokerHub/user/deleteUser/1?brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "status": "success",
  "message": "User deleted successfully"
}
```

#### 3. Get User by ID
```http
GET /BrokerHub/user/getUser/{userId}?brokerId=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/user/getUser/1?brokerId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "userId": 1,
  "userType": "TRADER",
  "firmName": "ABC Traders",
  "ownerName": "John Doe",
  "gstNumber": "GST123456789",
  "email": "john@abctraders.com",
  "phoneNumbers": ["9876543210"],
  "address": {
    "city": "Mumbai",
    "area": "Andheri",
    "pincode": "400001"
  },
  "brokerageRate": 10,
  "totalBagsSold": 100,
  "totalBagsBought": 150
}
```

### Analytics & Reports APIs

#### 1. Get Monthly Analytics
```http
GET /BrokerHub/Dashboard/{brokerId}/getMonthlyAnalytics/{financialYearId}?month=1
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getMonthlyAnalytics/1?month=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200):**
```json
{
  "month": "JANUARY",
  "totalBags": 500,
  "totalBrokerage": 25000.00,
  "totalValue": 1000000.00,
  "transactionCount": 25,
  "topProducts": [
    {
      "productName": "Rice",
      "bags": 200,
      "brokerage": 10000.00
    }
  ]
}
```

#### 2. Export Data
```http
GET /BrokerHub/Dashboard/{brokerId}/exportData/{financialYearId}?format=excel&startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <token>
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/exportData/1?format=excel&startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer <your-jwt-token>" \
  --output "export_data.xlsx"
```

**Response (200):**
```
Binary Excel file download
```

---

## 📊 Response Status Codes

| Code | Status | Meaning |
|------|--------|---------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 202 | Accepted | Request accepted |
| 208 | Already Reported | Resource already exists |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Authentication required |
| 403 | Forbidden | Access denied |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 500 | Internal Server Error | Server error |

---

## 🔧 Common Request Headers

### For All Requests:
```
Content-Type: application/json
Accept: application/json
```

### For Protected Endpoints:
```
Authorization: Bearer <your-jwt-token>
```

### For File Uploads:
```
Content-Type: multipart/form-data
Authorization: Bearer <your-jwt-token>
```

---

## 🌐 Base URLs

### Development:
```
http://localhost:8080
```

### Production:
```
https://your-production-domain.com
```

---

## 🔧 Common cURL Examples

### Authentication Flow
```bash
# 1. Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/BrokerHub/Broker/login \
  -H "Content-Type: application/json" \
  -d '{"userName":"admin","password":"password"}' | jq -r '.token')

# 2. Use token in subsequent requests
curl -X GET http://localhost:8080/BrokerHub/user/allUsers \
  -H "Authorization: Bearer $TOKEN"
```

### Bulk Operations
```bash
# Upload Excel file
curl -X POST http://localhost:8080/BrokerHub/user/bulkUpload \
  -H "Authorization: Bearer <token>" \
  -F "file=@users.xlsx"

# Download template
curl -X GET http://localhost:8080/BrokerHub/user/downloadTemplate \
  --output "user_template.xlsx"
```

---

## 📝 Notes

1. **Date Format**: Use `YYYY-MM-DD` format for all date fields
2. **Decimal Values**: Use decimal format for monetary values (e.g., 1000.50)
3. **Boolean Values**: Use `true`/`false` for boolean fields
4. **Array Fields**: Use JSON array format for list fields
5. **File Uploads**: Use `multipart/form-data` content type

---

This comprehensive reference guide covers all major API endpoints with request/response examples and cURL commands. Use this as a quick reference while implementing frontend functionality.