# Multi-Tenant Brokerage Application API Documentation

## Overview
This document provides comprehensive API documentation for the multi-tenant brokerage application. All APIs now support multi-tenant architecture where each broker has isolated data access.

## Important Changes for UI Team

### 🔴 **BREAKING CHANGES**
1. **Authentication Required**: All APIs now require broker authentication
2. **Automatic Tenant Isolation**: APIs automatically filter data based on authenticated broker
3. **Broker ID Removal**: No need to pass `brokerId` in request bodies - automatically extracted from authentication
4. **Response Structure**: Some response structures have been optimized

### 🔐 **Authentication**
- **Type**: Basic Authentication
- **Header**: `Authorization: Basic <base64(username:password)>`
- **Context**: All requests automatically use the authenticated broker's context

---

## 📋 **USER MANAGEMENT APIs**

### 1. Create User
**Endpoint**: `POST /api/users`
**Changes**: ✅ Automatically assigns current broker to user

**Request Body**:
```json
{
  "firmName": "ABC Traders",
  "gstNumber": "GST123456789",
  "ownerName": "John Doe",
  "email": "john@abctraders.com",
  "phoneNumbers": ["9876543210"],
  "brokerageRate": 10,
  "userType": "SELLER",
  "address": {
    "city": "Mumbai",
    "area": "Andheri",
    "pincode": "400001"
  },
  "bankDetails": {
    "bankName": "HDFC Bank",
    "accountNumber": "12345678901",
    "ifscCode": "HDFC0001234",
    "branch": "Andheri Branch"
  }
}
```

**Response**:
```json
{
  "status": "success",
  "message": "User created successfully",
  "userId": 123
}
```

### 2. Get All Users
**Endpoint**: `GET /api/users`
**Changes**: ✅ Returns only current broker's users

**Response**:
```json
{
  "status": "success",
  "data": [
    {
      "userId": 123,
      "firmName": "ABC Traders",
      "gstNumber": "GST123456789",
      "ownerName": "John Doe",
      "userType": "SELLER",
      "email": "john@abctraders.com",
      "phoneNumbers": ["9876543210"],
      "brokerageRate": 10,
      "totalBagsSold": 100,
      "totalBagsBought": 50,
      "payableAmount": 50000,
      "receivableAmount": 75000,
      "totalPayableBrokerage": 1500.00,
      "addressId": 456
    }
  ]
}
```

### 3. Get Users by City
**Endpoint**: `GET /api/users/city/{cityName}`
**Changes**: ✅ Returns only current broker's users in specified city

### 4. Search User by Property
**Endpoint**: `GET /api/users/search?property={firmName|gstNumber}&value={searchValue}`
**Changes**: ✅ Searches only within current broker's users

---

## 📦 **PRODUCT MANAGEMENT APIs**

### 1. Create Product
**Endpoint**: `POST /api/products`
**Changes**: ✅ Automatically assigns current broker to product

**Request Body**:
```json
{
  "productName": "Rice",
  "productBrokerage": 10.0,
  "quantity": 100,
  "price": 50,
  "quality": "Grade A",
  "imgLink": "https://example.com/rice.jpg"
}
```

### 2. Get All Products
**Endpoint**: `GET /api/products?page={pageNumber}&size={pageSize}`
**Changes**: ✅ Returns only current broker's products with pagination

**Response**:
```json
{
  "status": "success",
  "data": [
    {
      "productId": 789,
      "productName": "Rice",
      "productBrokerage": 10.0,
      "quantity": 100,
      "price": 50,
      "quality": "Grade A",
      "imgLink": "https://example.com/rice.jpg"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "totalPages": 5,
    "totalElements": 50,
    "pageSize": 10
  }
}
```

### 3. Get Products by Name
**Endpoint**: `GET /api/products/name/{productName}`
**Changes**: ✅ Searches only within current broker's products

---

## 🏠 **ADDRESS MANAGEMENT APIs**

### 1. Create Address
**Endpoint**: `POST /api/addresses`
**Changes**: ✅ Automatically assigns current broker to address

**Request Body**:
```json
{
  "city": "Mumbai",
  "area": "Andheri",
  "pincode": "400001"
}
```

### 2. Get All Addresses
**Endpoint**: `GET /api/addresses`
**Changes**: ✅ Returns only current broker's addresses

### 3. Check City Exists
**Endpoint**: `GET /api/addresses/city/{cityName}/exists`
**Changes**: ✅ Checks only within current broker's addresses

---

## 🏦 **BANK DETAILS APIs**

### 1. Create Bank Details
**Endpoint**: `POST /api/bank-details`
**Changes**: ✅ Automatically assigns current broker to bank details

**Request Body**:
```json
{
  "bankName": "HDFC Bank",
  "accountNumber": "12345678901",
  "ifscCode": "HDFC0001234",
  "branch": "Andheri Branch"
}
```

### 2. Get Bank Details by Account Number
**Endpoint**: `GET /api/bank-details/account/{accountNumber}`
**Changes**: ✅ Searches only within current broker's bank details

---

## 📊 **DAILY LEDGER APIs**

### 1. Create Daily Ledger
**Endpoint**: `POST /api/daily-ledger`
**Changes**: ✅ Automatically assigns current broker to daily ledger

**Request Body**:
```json
{
  "financialYearId": 1,
  "date": "2024-01-15"
}
```

### 2. Get Daily Ledger by Date
**Endpoint**: `GET /api/daily-ledger/{date}`
**Changes**: ✅ Returns only current broker's daily ledger for specified date

**Response**:
```json
{
  "status": "success",
  "data": {
    "dailyLedgerId": 456,
    "date": "2024-01-15",
    "financialYearId": 1,
    "ledgerDetails": [
      {
        "ledgerDetailsId": 789,
        "transactionDate": "2024-01-15",
        "fromSeller": {
          "userId": 123,
          "firmName": "ABC Traders",
          "addressId": 456
        },
        "records": [
          {
            "ledgerRecordId": 101,
            "quantity": 10,
            "brokerage": 100,
            "productCost": 500,
            "totalProductsCost": 5000,
            "totalBrokerage": 1000,
            "toBuyer": {
              "userId": 124,
              "firmName": "XYZ Buyers",
              "addressId": 457
            },
            "product": {
              "productId": 789,
              "productName": "Rice"
            }
          }
        ],
        "transactionSummary": {
          "totalBagsSoldInTransaction": 10,
          "totalBrokerageInTransaction": 1000.00,
          "totalReceivableAmountInTransaction": 5000,
          "averageBrokeragePerBag": 100.00,
          "numberOfProducts": 1,
          "numberOfBuyers": 1
        }
      }
    ]
  }
}
```

### 3. Get Daily Ledger with Pagination
**Endpoint**: `GET /api/daily-ledger/{date}/paginated?page={pageNumber}&size={pageSize}`
**Changes**: ✅ Returns paginated current broker's daily ledger data

---

## 📋 **LEDGER DETAILS APIs**

### 1. Create Ledger Details
**Endpoint**: `POST /api/ledger-details`
**Changes**: ✅ Automatically assigns current broker to ledger details

**Request Body**:
```json
{
  "date": "2024-01-15",
  "fromSeller": 123,
  "brokerage": 100,
  "ledgerRecordDTOList": [
    {
      "buyerName": "XYZ Buyers",
      "productId": 789,
      "quantity": 10,
      "brokerage": 100,
      "productCost": 500
    }
  ]
}
```

### 2. Get All Ledger Details
**Endpoint**: `GET /api/ledger-details`
**Changes**: ✅ Returns only current broker's ledger details

### 3. Get Ledger Details by ID
**Endpoint**: `GET /api/ledger-details/{ledgerDetailId}`
**Changes**: ✅ Returns only if belongs to current broker

### 4. Get Ledger Details by Date
**Endpoint**: `GET /api/ledger-details/date/{date}`
**Changes**: ✅ Returns only current broker's ledger details for specified date

---

## 💰 **PAYMENT SYSTEM APIs**

### 1. Brokerage Payments
**Endpoint**: `GET /api/payments/brokerage`
**Changes**: ✅ Returns only current broker's brokerage payments

### 2. Pending Payments
**Endpoint**: `GET /api/payments/pending`
**Changes**: ✅ Returns only current broker's pending payments

### 3. Receivable Payments
**Endpoint**: `GET /api/payments/receivable`
**Changes**: ✅ Returns only current broker's receivable payments

---

## 🔧 **CACHE & OPTIMIZATION APIs**

### 1. Product Names Cache
**Endpoint**: `GET /api/cache/products/names`
**Changes**: ✅ Returns only current broker's product names (cached)

### 2. User Names Cache
**Endpoint**: `GET /api/cache/users/names`
**Changes**: ✅ Returns only current broker's user firm names (cached)

---

## ⚠️ **ERROR RESPONSES**

### Authentication Errors
```json
{
  "status": "error",
  "code": "UNAUTHORIZED",
  "message": "No authenticated user found"
}
```

### Access Denied Errors
```json
{
  "status": "error",
  "code": "ACCESS_DENIED",
  "message": "Access denied: You can only access your own data"
}
```

### Validation Errors
```json
{
  "status": "error",
  "code": "VALIDATION_ERROR",
  "message": "Invalid input data",
  "details": {
    "firmName": "Firm name is required",
    "gstNumber": "GST number format is invalid"
  }
}
```

---

## 📝 **MIGRATION NOTES FOR UI TEAM**

### 1. Remove Broker ID Parameters
- **Before**: APIs required `brokerId` parameter
- **After**: Broker context automatically extracted from authentication
- **Action**: Remove all `brokerId` parameters from API calls

### 2. Update Authentication
- Ensure all API calls include proper Basic Authentication headers
- Handle authentication errors appropriately

### 3. Response Structure Changes
- Some APIs now return optimized response structures
- Update UI components to handle new response formats
- Pay attention to nested object structures (e.g., `addressId` instead of full address object)

### 4. Error Handling
- Add handling for new error types: `UNAUTHORIZED`, `ACCESS_DENIED`
- Update error messages to be user-friendly

### 5. Caching
- Product and user name APIs are now cached per broker
- Consider implementing client-side caching for better performance

---

## 🚀 **PERFORMANCE IMPROVEMENTS**

1. **Broker-Specific Caching**: All cache operations are now broker-specific
2. **Optimized Queries**: Database queries include broker filtering for better performance
3. **Reduced Data Transfer**: APIs return minimal required data structures
4. **Pagination**: Large datasets are properly paginated

---

## 📞 **SUPPORT**

For any questions or issues with the API changes, please contact the backend development team.

**Database**: `brokerHub_multiTenant` (separate from original single-user database)
**Environment**: Multi-tenant production environment
