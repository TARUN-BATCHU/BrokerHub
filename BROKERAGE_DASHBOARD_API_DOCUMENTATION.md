# Brokerage Dashboard API Documentation

## Overview
The Brokerage Dashboard provides comprehensive APIs for brokers to manage and track brokerage payments from merchants. This includes viewing payment statuses, updating payment information, and visualizing brokerage data through charts and analytics.

## Base URL
```
/api/brokerage-dashboard
```

## Endpoints

### 1. Get Dashboard Data
**GET** `/{brokerId}`

Returns complete dashboard data including summary, merchants list, and chart data.

**Response:**
```json
{
  "success": true,
  "message": "Dashboard data retrieved successfully",
  "data": {
    "summary": {
      "totalBrokerageReceivable": 50000.00,
      "totalBrokerageReceived": 30000.00,
      "totalBrokeragePending": 20000.00,
      "totalMerchants": 25,
      "paidMerchants": 10,
      "pendingMerchants": 10,
      "partialPaidMerchants": 5
    },
    "merchants": [...],
    "chartData": [
      {
        "label": "Received",
        "value": 30000.00,
        "color": "#4CAF50"
      },
      {
        "label": "Pending",
        "value": 20000.00,
        "color": "#FF9800"
      }
    ]
  }
}
```

### 2. Get Merchants Brokerage List
**GET** `/{brokerId}/merchants`

Returns detailed list of all merchants with their brokerage information.

**Response:**
```json
{
  "success": true,
  "message": "Merchants brokerage data retrieved successfully",
  "data": [
    {
      "merchantId": 1,
      "firmName": "ABC Traders",
      "ownerName": "John Doe",
      "phoneNumber": "9876543210",
      "soldBags": 100,
      "boughtBags": 50,
      "totalBags": 150,
      "brokerageRate": 2.00,
      "calculatedBrokerage": 300.00,
      "actualBrokerage": 285.00,
      "paidAmount": 100.00,
      "pendingAmount": 185.00,
      "status": "PARTIAL_PAID",
      "lastPaymentDate": "2024-01-15",
      "dueDate": "2024-02-15",
      "paymentHistory": [...]
    }
  ]
}
```

### 3. Update Payment Status
**PUT** `/{brokerId}/payment-status`

Updates the payment status for a merchant's brokerage.

**Request Body:**
```json
{
  "merchantId": 1,
  "status": "PARTIAL_PAID",
  "paidAmount": 100.00,
  "paymentDate": "2024-01-15",
  "paymentMethod": "UPI",
  "transactionReference": "TXN123456",
  "notes": "Partial payment received"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment status updated successfully"
}
```

### 4. Update Brokerage Amount
**PUT** `/{brokerId}/brokerage-amount`

Allows broker to update the receivable brokerage amount for a merchant.

**Request Body:**
```json
{
  "merchantId": 1,
  "newBrokerageAmount": 100.00,
  "reason": "Rounded to nearest hundred"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Brokerage amount updated successfully"
}
```

### 5. Get Payment History
**GET** `/{brokerId}/merchant/{merchantId}/history`

Returns payment history for a specific merchant.

**Response:**
```json
{
  "success": true,
  "message": "Payment history retrieved successfully",
  "data": [
    {
      "paymentId": 1,
      "amount": 100.00,
      "paymentDate": "2024-01-15",
      "paymentMethod": "UPI",
      "transactionReference": "TXN123456",
      "notes": "Partial payment"
    }
  ]
}
```

### 6. Get Payment Methods
**GET** `/payment-methods`

Returns list of available payment methods.

**Response:**
```json
{
  "success": true,
  "message": "Payment methods retrieved successfully",
  "data": [
    "CASH",
    "BANK_TRANSFER",
    "CHEQUE",
    "UPI",
    "NEFT",
    "RTGS",
    "ONLINE",
    "OTHER"
  ]
}
```

### 7. Calculate Brokerage
**POST** `/{brokerId}/calculate-brokerage`

Triggers automatic calculation of brokerage for all merchants based on their trading activity.

**Response:**
```json
{
  "success": true,
  "message": "Brokerage calculation completed successfully"
}
```

## Payment Status Values
- `PENDING`: Payment not yet made
- `PARTIAL_PAID`: Partial payment received
- `PAID`: Fully paid
- `OVERDUE`: Payment overdue
- `DUE_SOON`: Payment due soon

## Payment Methods
- `CASH`: Cash payment
- `BANK_TRANSFER`: Bank transfer
- `CHEQUE`: Cheque payment
- `UPI`: UPI payment
- `NEFT`: NEFT transfer
- `RTGS`: RTGS transfer
- `ONLINE`: Online payment
- `OTHER`: Other payment methods

## Features Implemented

1. **Dashboard Overview**: Complete summary of brokerage receivables, received amounts, and pending amounts
2. **Merchant List**: Detailed view of all merchants with their brokerage status
3. **Payment Status Management**: Update payment status with partial/full payment tracking
4. **Brokerage Amount Adjustment**: Allow brokers to adjust brokerage amounts with reasons
5. **Payment History**: Track all payment transactions for each merchant
6. **Payment Methods**: Support for multiple payment methods
7. **Automatic Calculation**: Calculate brokerage based on bags sold/bought from ledger records
8. **Chart Data**: Visualization data for dashboard charts
9. **Multi-tenant Support**: All operations are broker-specific

## Usage Notes

- All endpoints require valid broker authentication
- Brokerage calculation is based on: `(soldBags + boughtBags) * brokerageRate`
- Net brokerage includes discount (10%) and TDS (5%) deductions
- Payment history is automatically maintained for audit purposes
- Due dates are set to 30 days from creation by default