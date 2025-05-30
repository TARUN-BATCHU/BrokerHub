# 🧪 Payment System API Testing Guide

## 📋 Prerequisites

1. **Application Running**: Ensure the Spring Boot application is running on `http://localhost:8080`
2. **Database Setup**: Execute the `database_tables_creation.sql` script
3. **Sample Data**: Insert sample data for testing (optional)
4. **Authentication**: Use Basic Auth with credentials `tarun:securePassword123`

## 🔐 Authentication Setup

All APIs require Basic Authentication:
- **Username**: `tarun`
- **Password**: `securePassword123`
- **Base64 Encoded**: `dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=`

## 📊 API Test Cases

### 1. Get All Firm Names

**Purpose**: Retrieve all unique firm names for search dropdown

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/firms" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

**Expected Response**:
```json
{
  "status": "success",
  "message": "Firms retrieved successfully",
  "data": [
    "Tarun Traders",
    "Siri Traders", 
    "Krishna Mills",
    "Rama Traders",
    "Lakshmi Mills",
    "Venkat Traders",
    "Sai Mills"
  ]
}
```

### 2. Get All Brokerage Payments

**Purpose**: Retrieve all brokerage payments for a broker

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

**Expected Response**:
```json
{
  "status": "success",
  "message": "Brokerage payments retrieved successfully",
  "data": [
    {
      "id": 1,
      "merchantId": "M001",
      "firmName": "Tarun Traders",
      "ownerName": "Tarun Batchu",
      "city": "Vijayawada",
      "userType": "TRADER",
      "soldBags": 80,
      "boughtBags": 70,
      "totalBags": 150,
      "brokerageRate": 10,
      "grossBrokerage": 1500,
      "discount": 150,
      "tds": 75,
      "netBrokerage": 1275,
      "paidAmount": 500,
      "pendingAmount": 775,
      "lastPaymentDate": "2024-01-15",
      "dueDate": "2024-02-15",
      "status": "PARTIAL_PAID",
      "partPayments": [
        {
          "id": "PP001",
          "amount": 500,
          "date": "2024-01-15",
          "method": "CASH",
          "notes": "Partial payment received"
        }
      ]
    }
  ]
}
```

### 3. Search Brokerage Payments by Firm

**Purpose**: Search brokerage payments by firm name

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage/search?firmName=Tarun%20Traders" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

### 4. Add Part Payment to Brokerage

**Purpose**: Add a partial payment to reduce brokerage debt

```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/brokerage/1/part-payment" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500,
    "method": "CASH",
    "notes": "Partial payment received",
    "paymentDate": "2024-01-20"
  }'
```

**Expected Response**:
```json
{
  "status": "success",
  "message": "Part payment added successfully",
  "data": {
    "partPaymentId": "PP003",
    "updatedPendingAmount": 275,
    "updatedPaidAmount": 1000,
    "updatedStatus": "PARTIAL_PAID"
  }
}
```

### 5. Get All Pending Payments

**Purpose**: Retrieve all pending payments (buyers owe sellers)

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/pending" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

**Expected Response**:
```json
{
  "status": "success",
  "message": "Pending payments retrieved successfully",
  "data": [
    {
      "id": 1,
      "buyerId": "B001",
      "buyerFirm": "Siri Traders",
      "buyerOwner": "Santosh Kumar",
      "buyerCity": "Vijayawada",
      "totalPendingAmount": 850000,
      "transactionCount": 2,
      "oldestTransactionDate": "2024-01-10",
      "dueDate": "2024-02-10",
      "status": "OVERDUE",
      "transactions": [
        {
          "id": "PT001",
          "date": "2024-01-10",
          "sellerFirm": "Tarun Traders",
          "sellerOwner": "Tarun Batchu",
          "product": "Rice",
          "quality": "Premium",
          "bags": 80,
          "ratePerBag": 5000,
          "totalAmount": 400000,
          "paidAmount": 0,
          "pendingAmount": 400000,
          "dueDate": "2024-02-10",
          "status": "PENDING"
        }
      ]
    }
  ]
}
```

### 6. Search Pending Payments by Buyer Firm

**Purpose**: Search pending payments by buyer firm name

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/pending/search?buyerFirm=Siri%20Traders" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

### 7. Get All Receivable Payments

**Purpose**: Retrieve all receivable payments (sellers are owed)

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/receivable" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

**Expected Response**:
```json
{
  "status": "success",
  "message": "Receivable payments retrieved successfully",
  "data": [
    {
      "id": 1,
      "sellerId": "S001",
      "sellerFirm": "Tarun Traders",
      "sellerOwner": "Tarun Batchu",
      "sellerCity": "Vijayawada",
      "totalReceivableAmount": 400000,
      "transactionCount": 1,
      "oldestTransactionDate": "2024-01-10",
      "dueDate": "2024-02-10",
      "status": "OVERDUE",
      "owedBy": [
        {
          "buyerFirm": "Siri Traders",
          "buyerOwner": "Santosh Kumar",
          "totalOwed": 400000,
          "transactions": [
            {
              "id": "RT001",
              "date": "2024-01-10",
              "product": "Rice",
              "quality": "Premium",
              "bags": 80,
              "ratePerBag": 5000,
              "totalAmount": 400000,
              "paidAmount": 0,
              "pendingAmount": 400000,
              "dueDate": "2024-02-10",
              "status": "PENDING"
            }
          ]
        }
      ]
    }
  ]
}
```

### 8. Search Receivable Payments by Seller Firm

**Purpose**: Search receivable payments by seller firm name

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/receivable/search?sellerFirm=Tarun%20Traders" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

## 🔧 Utility APIs

### Refresh Payment Cache

```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/refresh-cache" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

### Update Overdue Payment Statuses

```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/update-overdue-status" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

### Get Payment Dashboard Statistics

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/dashboard" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

## ❌ Error Test Cases

### 1. Invalid Broker ID

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/999/brokerage" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

**Expected Response**:
```json
{
  "status": "error",
  "message": "Broker not found",
  "error": "Invalid broker ID"
}
```

### 2. Invalid Part Payment Amount

```bash
curl -X POST "http://localhost:8080/BrokerHub/payments/1/brokerage/1/part-payment" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": -100,
    "method": "CASH",
    "paymentDate": "2024-01-20"
  }'
```

**Expected Response**:
```json
{
  "status": "error",
  "message": "Invalid request",
  "error": "Amount must be positive"
}
```

### 3. Unauthorized Access

```bash
curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage" \
  -H "Content-Type: application/json"
```

**Expected Response**: `401 Unauthorized`

## 📊 Performance Testing

### Load Testing with Multiple Requests

```bash
# Test concurrent requests
for i in {1..10}; do
  curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage" \
    -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
    -H "Content-Type: application/json" &
done
wait
```

### Cache Performance Testing

```bash
# First request (cache miss)
time curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"

# Second request (cache hit)
time curl -X GET "http://localhost:8080/BrokerHub/payments/1/brokerage" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

## 🎯 Test Scenarios

### Scenario 1: Complete Payment Workflow
1. Get all brokerage payments
2. Add a part payment
3. Verify updated amounts
4. Check payment status change

### Scenario 2: Search and Filter
1. Get all firm names
2. Search by specific firm
3. Verify filtered results

### Scenario 3: Error Handling
1. Test with invalid broker ID
2. Test with invalid payment amounts
3. Test without authentication

### Scenario 4: Cache Testing
1. Make initial request (cache miss)
2. Make same request (cache hit)
3. Refresh cache
4. Verify cache invalidation

## 📝 Test Checklist

- [ ] All 8 main APIs working correctly
- [ ] Authentication working properly
- [ ] Error responses formatted correctly
- [ ] Search functionality working
- [ ] Part payment addition working
- [ ] Cache functionality working
- [ ] Database queries optimized
- [ ] Response times acceptable
- [ ] Data validation working
- [ ] Status updates working correctly

## 🔍 Debugging Tips

1. **Check Application Logs**: Look for detailed error messages
2. **Verify Database**: Ensure tables are created and have data
3. **Test Authentication**: Verify Base64 encoding is correct
4. **Check Network**: Ensure application is accessible on port 8080
5. **Validate JSON**: Ensure request bodies are valid JSON

---

**Note**: Replace `localhost:8080` with your actual server URL if different. Ensure all required tables are created and sample data is inserted for comprehensive testing.
