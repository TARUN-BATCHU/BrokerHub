# 🔗 BrokerHub - Complete Unified API Documentation

## 🔐 Authentication

### Base URL
```
Development: http://localhost:8080
Production: https://your-domain.com
```

### Authentication Headers
```
Authorization: Basic <base64(username:password)>
Content-Type: application/json
```

---

## 🟢 Public Endpoints (No Authentication Required)

### Broker Management

#### 1. Broker Registration
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

#### 3. Check Username Availability
```bash
curl -X GET "http://localhost:8080/BrokerHub/Broker/UserNameExists/broker123"
```

**Response (200):**
```json
true
```

#### 4. Check Firm Name Availability
```bash
curl -X GET "http://localhost:8080/BrokerHub/Broker/BrokerFirmNameExists/ABC%20Trading"
```

**Response (200):**
```json
false
```

#### 5. Create User
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

---

## 🔒 Protected Endpoints (Authentication Required)

### Financial Year APIs

#### 1. Create Financial Year
```bash
curl -X POST http://localhost:8080/BrokerHub/FinancialYear/create \
  -H "Authorization: Bearer <token>" \
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

#### 2. Get All Financial Years
```bash
curl -X GET http://localhost:8080/BrokerHub/FinancialYear/getAllFinancialYears \
  -H "Authorization: Bearer <token>"
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

#### 3. Set Current Financial Year
```bash
curl -X POST "http://localhost:8080/BrokerHub/FinancialYear/setCurrentFinancialYear?financialYearId=2024" \
  -H "Authorization: Bearer <token>"
```

**Response (200):**
```json
"Current financial year set successfully"
```

#### 4. Get Current Financial Year
```bash
curl -X GET "http://localhost:8080/BrokerHub/FinancialYear/getCurrentFinancialYear" \
  -H "Authorization: Bearer <token>"
```

**Response (200):**
```json
2024
```

### Product APIs

#### 1. Create Product
```bash
curl -X POST http://localhost:8080/BrokerHub/Product/createProduct \
  -H "Authorization: Bearer <token>" \
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

#### 2. Get All Products
```bash
curl -X GET http://localhost:8080/BrokerHub/Product/allProducts \
  -H "Authorization: Bearer <token>"
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

#### 3. Get Product Names
```bash
curl -X GET http://localhost:8080/BrokerHub/Product/getProductNames \
  -H "Authorization: Bearer <token>"
```

**Response (200):**
```json
["Wheat", "Rice", "Cotton", "Sugar"]
```

#### 4. Bulk Upload Products
```bash
curl -X POST http://localhost:8080/BrokerHub/Product/bulkUpload \
  -H "Authorization: Bearer <token>" \
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

### User Management APIs

#### 1. Get All Users
```bash
curl -X GET http://localhost:8080/BrokerHub/user/allUsers \
  -H "Authorization: Bearer <token>"
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

#### 2. Get User Names and IDs
```bash
curl -X GET http://localhost:8080/BrokerHub/user/getUserNamesAndIds \
  -H "Authorization: Bearer <token>"
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
```bash
curl -X POST http://localhost:8080/BrokerHub/user/bulkUpload \
  -H "Authorization: Bearer <token>" \
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

### Ledger Details APIs

#### 1. Create Ledger Details
```bash
curl -X POST http://localhost:8080/BrokerHub/LedgerDetails/createLedgerDetails \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "brokerId": 1,
    "financialYearId": 2024,
    "fromSeller": 123,
    "date": "2024-01-15",
    "brokerage": 50,
    "sellerBrokerage": "2%",
    "ledgerRecordDTOList": [
      {
        "productId": 1,
        "buyerName": "ABC Traders",
        "quantity": 100,
        "productCost": 1500,
        "brokerage": 25
      }
    ]
  }'
```

**Response (201):**
```json
5
```

#### 2. Get Next Transaction Number
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getNextTransactionNumber?financialYearId=2024" \
  -H "Authorization: Bearer <token>"
```

**Response (200):**
```json
6
```

#### 3. Get Ledger Details by Transaction Number
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1&financialYearId=2024" \
  -H "Authorization: Bearer <token>"
```

**Response (200):**
```json
{
  "ledgerDetailsId": 1,
  "brokerTransactionNumber": 1,
  "financialYearId": 2024,
  "broker": {
    "brokerId": 1,
    "brokerName": "ABC Brokerage",
    "totalBrokerage": 125000.50
  },
  "fromSeller": {
    "userId": 123,
    "firmName": "Farmer's Cooperative",
    "contactNumber": "+91-9876543210",
    "totalBagsSold": 500,
    "receivableAmount": 750000
  },
  "dailyLedger": {
    "dailyLedgerId": 1,
    "date": "2024-01-15",
    "totalTransactions": 5
  },
  "records": [
    {
      "ledgerRecordId": 1,
      "quantity": 100,
      "brokerage": 25,
      "productCost": 1500,
      "totalProductsCost": 150000,
      "totalBrokerage": 2500,
      "toBuyer": {
        "userId": 456,
        "firmName": "ABC Traders",
        "payableAmount": 150000
      },
      "product": {
        "productId": 1,
        "productName": "Wheat",
        "category": "Grains"
      }
    }
  ]
}
```

#### 4. Update Ledger Details by Transaction Number
```bash
curl -X PUT "http://localhost:8080/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber?transactionNumber=1&brokerId=1&financialYearId=2024" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "fromSeller": 123,
    "date": "2024-01-15",
    "ledgerRecordDTOList": [
      {
        "productId": 1,
        "buyerName": "ABC Traders",
        "quantity": 150,
        "productCost": 1600,
        "brokerage": 30
      }
    ]
  }'
```

**Response (200):**
```json
"Ledger details updated successfully"
```

### Daily Ledger APIs

#### 1. Get Daily Ledger
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getDailyLedger?date=2024-01-15" \
  -H "Authorization: Bearer <token>"
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
      "fromSeller": {
        "userId": 10,
        "userType": "MILLER",
        "firmName": "XYZ Mills",
        "ownerName": "Miller Owner"
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

#### 2. Get Optimized Daily Ledger
```bash
curl -X GET "http://localhost:8080/BrokerHub/DailyLedger/getOptimizedDailyLedger?date=2024-01-15" \
  -H "Authorization: Bearer <token>"
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

### Payment APIs

#### 1. Get All Brokerage Payments
```bash
curl -X GET http://localhost:8080/BrokerHub/payments/1/brokerage \
  -H "Authorization: Bearer <token>"
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

#### 2. Add Part Payment
```bash
curl -X POST http://localhost:8080/BrokerHub/payments/1/brokerage/1/part-payment \
  -H "Authorization: Bearer <token>" \
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

#### 3. Get All Pending Payments
```bash
curl -X GET http://localhost:8080/BrokerHub/payments/1/pending \
  -H "Authorization: Bearer <token>"
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

#### 4. Get All Receivable Payments
```bash
curl -X GET http://localhost:8080/BrokerHub/payments/1/receivable \
  -H "Authorization: Bearer <token>"
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

### Dashboard APIs

#### 1. Get Financial Year Analytics
```bash
curl -X GET http://localhost:8080/BrokerHub/Dashboard/1/getFinancialYearAnalytics/1 \
  -H "Authorization: Bearer <token>"
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
  ]
}
```

#### 2. Get Top Performers
```bash
curl -X GET http://localhost:8080/BrokerHub/Dashboard/1/getTopPerformers/1 \
  -H "Authorization: Bearer <token>"
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

### Address APIs

#### 1. Create Address
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Authorization: Basic <base64(username:password)>" \
  -H "Content-Type: application/json" \
  -d '{
    "streetAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Near Central Park",
    "addressType": "BUSINESS"
  }'
```

**Response (200):**
```json
{
  "id": 1,
  "streetAddress": "123 Main Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "landmark": "Near Central Park",
  "addressType": "BUSINESS",
  "createdAt": "2023-09-20T10:30:00",
  "updatedAt": "2023-09-20T10:30:00"
}
```

#### 2. Get All Addresses
```bash
curl -X GET http://localhost:8080/api/addresses \
  -H "Authorization: Basic <base64(username:password)>"
```

**Response (200):**
```json
[
  {
    "id": 1,
    "streetAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Near Central Park",
    "addressType": "BUSINESS"
  }
]
```

---

## 📊 Response Status Codes

| Code | Status | Meaning |
|------|--------|---------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Authentication required |
| 403 | Forbidden | Access denied |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 500 | Internal Server Error | Server error |

---

## 📝 Common Request Patterns

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

## 🔧 Important Notes

1. **Date Format**: Use `YYYY-MM-DD` format for all date fields
2. **Multi-tenant**: All APIs are broker-isolated automatically
3. **Transaction Numbers**: Reset to 1 at start of each financial year per broker
4. **File Uploads**: Use `multipart/form-data` content type
5. **Pagination**: Default page=0, size=10, max size=100
6. **Caching**: Product and user name APIs are cached per broker

---

This unified documentation combines all API endpoints from the BrokerHub system with their complete request/response structures and cURL examples.