# Optimized Daily Ledger API

## Overview
This document describes the new optimized Daily Ledger API that returns streamlined data with only essential information, significantly reducing response size and improving performance.

## New Endpoint

### GET /BrokerHub/DailyLedger/getOptimizedDailyLedger

**URL:** `http://localhost:8080/BrokerHub/DailyLedger/getOptimizedDailyLedger?date=2023-06-17`

**Method:** GET

**Authentication:** Basic Auth (same as original API)
```
Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=
```

**Parameters:**
- `date` (required): LocalDate in format YYYY-MM-DD

## Response Structure

The optimized response contains only essential data:

```json
{
  "dailyLedgerId": 123,
  "date": "2023-06-17",
  "financialYearId": 456,
  "ledgerDetails": [
    {
      "ledgerDetailsId": 789,
      "fromSeller": {
        "userId": 101,
        "firmName": "ABC Trading Co",
        "addressId": 201
      },
      "records": [
        {
          "ledgerRecordId": 1001,
          "toBuyer": {
            "userId": 102,
            "firmName": "XYZ Enterprises",
            "addressId": 202
          },
          "product": {
            "productId": 301,
            "productName": "Rice"
          },
          "quantity": 100,
          "brokerage": 50,
          "productCost": 5000,
          "totalProductsCost": 500000,
          "totalBrokerage": 5000
        }
      ]
    }
  ]
}
```

## Data Optimization

### What's Removed (compared to original API):

1. **Financial Year**: Only `financialYearId` instead of full object with start/end dates and name
2. **User Details**: 
   - Removed: email, phone numbers, GST number, bank details, brokerage rates, total bags, payable/receivable amounts
   - Kept: userId, firmName, addressId
3. **Address Details**: Only `addressId` instead of full address with city, area, pincode
4. **Product Details**: Only `productId` and `productName` instead of brokerage rates, quantity, price, quality, image links
5. **Bank Details**: Completely removed from user objects
6. **Contact Information**: Phone numbers and email addresses removed

### Benefits:

1. **Reduced Response Size**: Approximately 70-80% smaller response payload
2. **Faster Network Transfer**: Less data to transmit over network
3. **Improved Performance**: Faster JSON serialization/deserialization
4. **Better Mobile Experience**: Reduced data usage for mobile clients
5. **Cleaner Data Structure**: Only business-essential information

## Usage Example

### cURL Command:
```bash
curl --location 'http://localhost:8080/BrokerHub/DailyLedger/getOptimizedDailyLedger?date=2023-06-17' \
--header 'Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM='
```

### Response Handling:
The response structure is designed to provide all essential transaction information while maintaining referential integrity through IDs. If you need additional details (like full address or bank information), you can make separate API calls using the provided IDs.

## Migration Guide

If you're currently using the original `/getDailyLedger` endpoint:

1. **Replace the endpoint URL** from `/getDailyLedger` to `/getOptimizedDailyLedger`
2. **Update your response parsing logic** to handle the new optimized structure
3. **Use IDs for additional lookups** if you need full details for addresses, financial years, etc.
4. **Test thoroughly** to ensure your application works with the reduced data set

## Backward Compatibility

The original `/getDailyLedger` endpoint remains unchanged and fully functional. You can migrate to the optimized version at your own pace.
