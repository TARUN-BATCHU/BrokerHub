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

**Response (201):**
```json
"Password created"
```

### 4. Verify Account
```http
POST /BrokerHub/Broker/verify-account?userName=broker123&otp=123456
```

**Response (200):**
```json
"OTP verified you can change password now"
```

### 5. Forgot Password
```http
GET /BrokerHub/Broker/forgotPassword?userName=broker123
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

**Response (200):**
```json
true  // Username exists
false // Username available
```

### 7. Check Firm Name Availability
```http
GET /BrokerHub/Broker/BrokerFirmNameExists/ABC%20Trading
```

**Response (200):**
```json
true  // Firm name exists
false // Firm name available
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

**Response (200):**
```json
{
  "financialYearId": 1,
  "overallTotals": {
    "totalBags": 1000,
    "totalBrokerage": 50000.00,
    "totalValue": 2000000.00
  },
  "monthlyBreakdown": [
    {
      "month": "JANUARY",
      "totalBags": 100,
      "totalBrokerage": 5000.00,
      "totalValue": 200000.00
    }
  ]
}
```

#### 2. Get Top Performers
```http
GET /BrokerHub/Dashboard/{brokerId}/getTopPerformers/{financialYearId}
Authorization: Bearer <token>
```

### Product APIs

#### 1. Get All Products
```http
GET /BrokerHub/Product/allProducts
Authorization: Bearer <token>
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

#### 3. Get Product Names
```http
GET /BrokerHub/Product/getProductNames
Authorization: Bearer <token>
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

#### 2. Get Daily Ledger
```http
GET /BrokerHub/DailyLedger/getDailyLedger?date=2024-01-15
Authorization: Bearer <token>
```

#### 3. Get Optimized Daily Ledger
```http
GET /BrokerHub/DailyLedger/getOptimizedDailyLedger?date=2024-01-15
Authorization: Bearer <token>
```

### Payment APIs

#### 1. Get All Brokerage Payments
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
      "merchantFirmName": "ABC Traders",
      "totalBags": 100,
      "grossBrokerage": 5000.00,
      "netBrokerage": 4250.00,
      "paidAmount": 2000.00,
      "pendingAmount": 2250.00,
      "status": "PARTIAL_PAID"
    }
  ]
}
```

#### 2. Search Brokerage Payments
```http
GET /BrokerHub/payments/{brokerId}/brokerage/search?firmName=ABC
Authorization: Bearer <token>
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

#### 2. Get User Names and IDs
```http
GET /BrokerHub/user/getUserNamesAndIds
Authorization: Bearer <token>
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

#### 2. Get All Financial Years
```http
GET /BrokerHub/FinancialYear/getAllFinancialYears
Authorization: Bearer <token>
```

### Address APIs

#### 1. Get All Addresses
```http
GET /BrokerHub/Address/getAllAddresses
Authorization: Bearer <token>
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

## 📝 Notes

1. **Date Format**: Use `YYYY-MM-DD` format for all date fields
2. **Decimal Values**: Use decimal format for monetary values (e.g., 1000.50)
3. **Boolean Values**: Use `true`/`false` for boolean fields
4. **Array Fields**: Use JSON array format for list fields
5. **File Uploads**: Use `multipart/form-data` content type

---

This reference guide covers all major API endpoints with request/response examples. Use this as a quick reference while implementing frontend functionality.