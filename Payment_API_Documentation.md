# Payment Controller API Documentation

## Overview
The Payment Controller provides comprehensive payment management functionality for the BrokerHub application, including brokerage payments, pending payments, and receivable payments management.

**Base URL:** `/BrokerHub/payments`

**Authentication:** JWT Bearer Token Required
- First login via `/BrokerHub/Broker/login` to get JWT token
- Include token in Authorization header: `Bearer <jwt_token>`

---

## Table of Contents
1. [Firm Names API](#firm-names-api)
2. [Brokerage Payments APIs](#brokerage-payments-apis)
3. [Pending Payments APIs](#pending-payments-apis)
4. [Receivable Payments APIs](#receivable-payments-apis)
5. [Utility Endpoints](#utility-endpoints)
6. [Data Models](#data-models)
7. [Error Handling](#error-handling)

---

## Firm Names API

### Get All Firm Names
Retrieves all unique firm names across all payment types for search dropdown functionality.

**Endpoint:** `GET /BrokerHub/payments/firms`

**cURL:**
```bash
# First, login to get JWT token
curl -X POST "http://localhost:8080/BrokerHub/Broker/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'

# Then use the JWT token
curl -X GET "http://localhost:8080/BrokerHub/payments/firms" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Firms retrieved successfully",
  "data": [
    "ABC Trading Co.",
    "XYZ Mills Pvt Ltd",
    "Global Traders",
    "Premium Grains Ltd"
  ]
}
```

**Error Response (500):**
```json
{
  "status": "error",
  "message": "Failed to retrieve firms",
  "error": "Internal server error"
}
```

---

## Brokerage Payments APIs

### Get All Brokerage Payments
Retrieves all brokerage payments for a specific broker.

**Endpoint:** `GET /BrokerHub/payments/{brokerId}/brokerage`

**Parameters:**
- `brokerId` (path): Broker ID (Long)

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Brokerage payments retrieved successfully",
  "data": [
    {
      "id": 1,
      "merchantId": "M001",
      "firmName": "ABC Trading Co.",
      "ownerName": "John Doe",
      "city": "Mumbai",
      "userType": "TRADER",
      "soldBags": 100,
      "boughtBags": 50,
      "totalBags": 150,
      "brokerageRate": 25.00,
      "grossBrokerage": 3750.00,
      "discount": 375.00,
      "tds": 337.50,
      "netBrokerage": 3037.50,
      "paidAmount": 1500.00,
      "pendingAmount": 1537.50,
      "lastPaymentDate": "2024-01-15",
      "dueDate": "2024-02-15",
      "status": "PARTIALLY_PAID",
      "partPayments": [
        {
          "id": "PP001",
          "amount": 1500.00,
          "paymentDate": "2024-01-15",
          "method": "UPI",
          "transactionReference": "UPI123456789",
          "notes": "Partial payment received"
        }
      ],
      "phoneNumber": "+91-9876543210",
      "email": "john@abctrading.com",
      "gstNumber": "27ABCDE1234F1Z5",
      "daysUntilDue": 15,
      "daysOverdue": 0,
      "paymentCompletionPercentage": 49.38
    }
  ]
}
```

### Search Brokerage Payments by Firm
Searches brokerage payments by firm name with case-insensitive partial matching.

**Endpoint:** `GET /BrokerHub/payments/{brokerId}/brokerage/search`

**Parameters:**
- `brokerId` (path): Broker ID (Long)
- `firmName` (query): Firm name to search (String)

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage/search?firmName=ABC" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):** Same structure as Get All Brokerage Payments

### Add Part Payment to Brokerage
Adds a partial payment to an existing brokerage payment.

**Endpoint:** `POST /BrokerHub/payments/{brokerId}/brokerage/{paymentId}/part-payment`

**Parameters:**
- `brokerId` (path): Broker ID (Long)
- `paymentId` (path): Brokerage payment ID (Long)

**Request Body:**
```json
{
  "amount": 1000.00,
  "method": "UPI",
  "notes": "Partial payment via UPI",
  "paymentDate": "2024-01-20",
  "transactionReference": "UPI987654321",
  "bankDetails": "HDFC Bank",
  "recordedBy": "admin"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/brokerage/1/part-payment" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00,
    "method": "UPI",
    "notes": "Partial payment via UPI",
    "paymentDate": "2024-01-20",
    "transactionReference": "UPI987654321",
    "bankDetails": "HDFC Bank",
    "recordedBy": "admin"
  }'
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Part payment added successfully",
  "data": {
    "partPaymentId": "PP002",
    "updatedPendingAmount": 537.50,
    "updatedPaidAmount": 2500.00,
    "updatedStatus": "PARTIALLY_PAID",
    "brokeragePaymentId": 1,
    "paymentAmount": 1000.00,
    "paymentCompletionPercentage": 82.31,
    "fullyPaid": false,
    "remainingAmount": 537.50,
    "totalBrokerageAmount": 3037.50
  }
}
```

**Validation Error Response (400):**
```json
{
  "status": "error",
  "message": "Validation failed",
  "error": "Amount must be positive"
}
```

---

## Pending Payments APIs

### Get All Pending Payments
Retrieves all pending payments for a specific broker.

**Endpoint:** `GET /BrokerHub/payments/{brokerId}/pending`

**Parameters:**
- `brokerId` (path): Broker ID (Long)

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/pending" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Pending payments retrieved successfully",
  "data": [
    {
      "id": 1,
      "buyerId": "B001",
      "buyerFirm": "XYZ Mills Pvt Ltd",
      "buyerOwner": "Jane Smith",
      "buyerCity": "Delhi",
      "buyerUserType": "MILLER",
      "totalPendingAmount": 125000.00,
      "transactionCount": 3,
      "oldestTransactionDate": "2024-01-01",
      "dueDate": "2024-02-01",
      "status": "OVERDUE",
      "transactions": [
        {
          "transactionId": "T001",
          "amount": 50000.00,
          "transactionDate": "2024-01-01",
          "productName": "Wheat",
          "quantity": 100,
          "rate": 500.00
        }
      ],
      "buyerPhone": "+91-9876543211",
      "buyerEmail": "jane@xyzmills.com",
      "buyerGstNumber": "07XYZAB5678C1D2",
      "daysOverdue": 19,
      "daysUntilDue": 0,
      "priorityLevel": "HIGH"
    }
  ]
}
```

### Search Pending Payments by Buyer Firm
Searches pending payments by buyer firm name.

**Endpoint:** `GET /BrokerHub/payments/{brokerId}/pending/search`

**Parameters:**
- `brokerId` (path): Broker ID (Long)
- `buyerFirm` (query): Buyer firm name to search (String)

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/pending/search?buyerFirm=XYZ" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):** Same structure as Get All Pending Payments

---

## Receivable Payments APIs

### Get All Receivable Payments
Retrieves all receivable payments for a specific broker.

**Endpoint:** `GET /BrokerHub/payments/{brokerId}/receivable`

**Parameters:**
- `brokerId` (path): Broker ID (Long)

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/receivable" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Receivable payments retrieved successfully",
  "data": [
    {
      "id": 1,
      "sellerId": "S001",
      "sellerFirm": "Global Traders",
      "sellerOwner": "Mike Johnson",
      "sellerCity": "Pune",
      "sellerUserType": "TRADER",
      "totalReceivableAmount": 85000.00,
      "transactionCount": 2,
      "oldestTransactionDate": "2024-01-05",
      "dueDate": "2024-02-05",
      "status": "PENDING",
      "owedBy": [
        {
          "buyerId": "B002",
          "buyerFirm": "Premium Grains Ltd",
          "totalOwed": 45000.00,
          "transactionCount": 1,
          "oldestTransactionDate": "2024-01-05"
        }
      ],
      "sellerPhone": "+91-9876543212",
      "sellerEmail": "mike@globaltraders.com",
      "sellerGstNumber": "27GLOBAL123F1Z5",
      "daysOverdue": 0,
      "daysUntilDue": 16,
      "priorityLevel": "MEDIUM",
      "uniqueBuyersCount": 2,
      "largestSingleDebt": 45000.00
    }
  ]
}
```

### Search Receivable Payments by Seller Firm
Searches receivable payments by seller firm name.

**Endpoint:** `GET /BrokerHub/payments/{brokerId}/receivable/search`

**Parameters:**
- `brokerId` (path): Broker ID (Long)
- `sellerFirm` (query): Seller firm name to search (String)

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/receivable/search?sellerFirm=Global" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):** Same structure as Get All Receivable Payments

---

## Utility Endpoints

### Refresh Payment Cache
Refreshes the payment cache for a specific broker.

**Endpoint:** `POST /BrokerHub/payments/{brokerId}/refresh-cache`

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/refresh-cache" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Payment cache refreshed successfully"
}
```

### Update Overdue Payment Statuses
Updates overdue payment statuses for a specific broker.

**Endpoint:** `POST /BrokerHub/payments/{brokerId}/update-overdue-status`

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/update-overdue-status" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Updated 5 overdue payment statuses"
}
```

### Get Payment Dashboard Statistics
Retrieves comprehensive payment dashboard statistics.

**Endpoint:** `GET /BrokerHub/payments/{brokerId}/dashboard`

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/dashboard" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Dashboard statistics retrieved successfully",
  "data": {
    "totalBrokerageAmount": 150000.00,
    "totalBrokeragePaid": 75000.00,
    "totalBrokeragePending": 75000.00,
    "merchantsWithPendingBrokerage": 25,
    "overdueBrokeragePayments": 8,
    "totalPendingPaymentAmount": 500000.00,
    "buyersWithPendingPayments": 15,
    "overduePendingPayments": 5,
    "averagePendingPaymentAmount": 33333.33,
    "totalReceivablePaymentAmount": 300000.00,
    "sellersWithReceivablePayments": 12,
    "overdueReceivablePayments": 3,
    "averageReceivablePaymentAmount": 25000.00,
    "totalActivePayments": 52,
    "totalAmountInCirculation": 875000.00,
    "brokerageCompletionPercentage": 50.00,
    "criticalPaymentsCount": 6,
    "paymentsDueSoonCount": 10,
    "recentPaymentsCount": 8,
    "recentPaymentsAmount": 45000.00,
    "newPendingPaymentsCount": 3
  }
}
```

### Get Payment Summary by Status
Retrieves payment summary grouped by status.

**Endpoint:** `GET /BrokerHub/payments/{brokerId}/summary`

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/summary" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Payment summary retrieved successfully",
  "data": {
    "brokeragePaymentSummary": [
      {
        "status": "PAID",
        "count": 15,
        "totalAmount": 75000.00,
        "percentage": 60.00,
        "statusDescription": "Fully Paid"
      },
      {
        "status": "PARTIALLY_PAID",
        "count": 8,
        "totalAmount": 50000.00,
        "percentage": 32.00,
        "statusDescription": "Partially Paid"
      },
      {
        "status": "OVERDUE",
        "count": 2,
        "totalAmount": 25000.00,
        "percentage": 8.00,
        "statusDescription": "Overdue"
      }
    ],
    "pendingPaymentSummary": [
      {
        "status": "PENDING",
        "count": 10,
        "totalAmount": 300000.00,
        "percentage": 66.67,
        "statusDescription": "Pending"
      },
      {
        "status": "OVERDUE",
        "count": 5,
        "totalAmount": 200000.00,
        "percentage": 33.33,
        "statusDescription": "Overdue"
      }
    ],
    "receivablePaymentSummary": [
      {
        "status": "PENDING",
        "count": 9,
        "totalAmount": 225000.00,
        "percentage": 75.00,
        "statusDescription": "Pending"
      },
      {
        "status": "OVERDUE",
        "count": 3,
        "totalAmount": 75000.00,
        "percentage": 25.00,
        "statusDescription": "Overdue"
      }
    ],
    "overallTotals": {
      "totalPaymentsCount": 52,
      "totalPaymentsAmount": 875000.00,
      "totalOverdueCount": 10,
      "totalOverdueAmount": 300000.00,
      "totalDueSoonCount": 8,
      "totalDueSoonAmount": 150000.00,
      "totalPaidCount": 15,
      "totalPaidAmount": 75000.00
    }
  }
}
```

### Generate Payment Data from Ledger
Generates payment data from ledger for a specific financial year.

**Endpoint:** `POST /BrokerHub/payments/{brokerId}/generate-from-ledger/{financialYearId}`

**Parameters:**
- `brokerId` (path): Broker ID (Long)
- `financialYearId` (path): Financial year ID (Long)

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/generate-from-ledger/2024" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "status": "success",
  "message": "Payment data generated successfully from ledger"
}
```

---

## Data Models

### Payment Status Enum
```
PENDING - Payment is pending
PARTIALLY_PAID - Payment is partially completed
PAID - Payment is fully completed
OVERDUE - Payment is overdue
CANCELLED - Payment is cancelled
```

### Payment Method Enum
```
CASH - Cash payment
CHEQUE - Cheque payment
UPI - UPI payment
BANK_TRANSFER - Bank transfer
NEFT - NEFT transfer
RTGS - RTGS transfer
CARD - Card payment
```

### Priority Level
```
LOW - Low priority
MEDIUM - Medium priority
HIGH - High priority
CRITICAL - Critical priority (overdue > 30 days or high amount)
```

---

## Error Handling

### Common Error Responses

**400 Bad Request:**
```json
{
  "status": "error",
  "message": "Validation failed",
  "error": "Amount must be positive"
}
```

**404 Not Found:**
```json
{
  "status": "error",
  "message": "Payment not found",
  "error": "No payment found with ID: 123"
}
```

**500 Internal Server Error:**
```json
{
  "status": "error",
  "message": "Failed to retrieve payments",
  "error": "Internal server error"
}
```

### Validation Rules

**Add Part Payment Request:**
- `amount`: Required, must be positive
- `method`: Required, must be valid PaymentMethod enum
- `paymentDate`: Required, cannot be in future
- `transactionReference`: Required for CHEQUE, UPI, BANK_TRANSFER, NEFT, RTGS
- `bankDetails`: Required for bank-related payment methods

**Search Parameters:**
- `firmName`, `buyerFirm`, `sellerFirm`: Case-insensitive partial matching
- `brokerId`: Must be valid Long value

---

## Authentication

All endpoints require JWT Bearer Token authentication:

### Step 1: Login to get JWT token
```bash
curl -X POST "http://localhost:8080/BrokerHub/Broker/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'
```

**Response:**
```json
{
  "status": "success",
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "brokerId": 1,
    "username": "your_username"
  }
}
```

### Step 2: Use JWT token in subsequent requests
Include in request headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Rate Limiting & Best Practices

1. **Pagination:** For large datasets, consider implementing pagination parameters
2. **Caching:** Use the refresh-cache endpoint judiciously to avoid performance issues
3. **Error Handling:** Always check the `status` field in responses
4. **Date Formats:** Use ISO 8601 format (YYYY-MM-DD) for dates
5. **Amount Precision:** Use up to 2 decimal places for monetary values
6. **Search Optimization:** Use specific search terms to reduce response size

---

## Frontend Integration Tips

1. **Loading States:** Show loading indicators during API calls
2. **Error Display:** Parse error messages from the `error` field
3. **Data Validation:** Validate form data before sending requests
4. **Status Colors:** Use different colors for different payment statuses
5. **Priority Indicators:** Highlight critical and high-priority payments
6. **Real-time Updates:** Consider implementing WebSocket for real-time payment updates
7. **Export Functionality:** Use the summary endpoints for generating reports

---

This documentation provides comprehensive information for frontend developers to integrate with the Payment Controller APIs effectively.