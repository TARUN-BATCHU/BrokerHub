# Dashboard API Documentation

Base URL: `http://localhost:8080/BrokerHub/Dashboard`

## 1. Get Financial Year Analytics

**Endpoint:** `GET /{brokerId}/getFinancialYearAnalytics/{financialYearId}`

**Description:** Get comprehensive analytics for a financial year including month-wise breakdown, product-wise analytics, city-wise analytics, and merchant type analytics.

**Curl Command:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getFinancialYearAnalytics/2024" \
  -H "Content-Type: application/json"
```

**Sample Response:**
```json
{
  "financialYearId": 2024,
  "financialYearName": "FY 2024-25",
  "startDate": "2024-04-01",
  "endDate": "2025-03-31",
  "totalBrokerage": 125000.50,
  "totalQuantity": 50000,
  "totalTransactionValue": 2500000.00,
  "totalTransactions": 150,
  "monthlyAnalytics": [
    {
      "month": "2024-04",
      "monthName": "April 2024",
      "totalBrokerage": 12500.00,
      "totalQuantity": 5000,
      "totalTransactionValue": 250000.00,
      "totalTransactions": 15,
      "productAnalytics": [
        {
          "productId": 1,
          "productName": "Wheat",
          "totalQuantity": 3000,
          "totalBrokerage": 7500.00,
          "totalTransactionValue": 150000.00,
          "totalTransactions": 10,
          "averagePrice": 50.00,
          "averageBrokeragePerUnit": 2.50
        }
      ],
      "cityAnalytics": [
        {
          "cityName": "Mumbai",
          "totalQuantity": 2000,
          "totalBrokerage": 5000.00,
          "totalTransactionValue": 100000.00,
          "totalTransactions": 8,
          "totalSellers": 5,
          "totalBuyers": 3,
          "productBreakdown": [
            {
              "productId": 1,
              "productName": "Wheat",
              "totalQuantity": 2000,
              "totalBrokerage": 5000.00,
              "totalTransactionValue": 100000.00,
              "totalTransactions": 8,
              "averagePrice": 50.00,
              "averageBrokeragePerUnit": 2.50
            }
          ]
        }
      ],
      "merchantTypeAnalytics": [
        {
          "merchantType": "MILLER",
          "totalQuantitySold": 2000,
          "totalQuantityBought": 1500,
          "totalBrokeragePaid": 8750.00,
          "totalTransactionValue": 175000.00,
          "totalTransactions": 10,
          "totalMerchants": 5
        }
      ]
    }
  ],
  "overallProductTotals": [
    {
      "productId": 1,
      "productName": "Wheat",
      "totalQuantity": 30000,
      "totalBrokerage": 75000.00,
      "totalTransactionValue": 1500000.00,
      "totalTransactions": 90,
      "averagePrice": 50.00,
      "averageBrokeragePerUnit": 2.50
    }
  ],
  "overallCityTotals": [
    {
      "cityName": "Mumbai",
      "totalQuantity": 20000,
      "totalBrokerage": 50000.00,
      "totalTransactionValue": 1000000.00,
      "totalTransactions": 60,
      "totalSellers": 15,
      "totalBuyers": 12,
      "productBreakdown": [
        {
          "productId": 1,
          "productName": "Wheat",
          "totalQuantity": 20000,
          "totalBrokerage": 50000.00,
          "totalTransactionValue": 1000000.00,
          "totalTransactions": 60,
          "averagePrice": 50.00,
          "averageBrokeragePerUnit": 2.50
        }
      ]
    }
  ],
  "overallMerchantTypeTotals": [
    {
      "merchantType": "MILLER",
      "totalQuantitySold": 25000,
      "totalQuantityBought": 20000,
      "totalBrokeragePaid": 87500.00,
      "totalTransactionValue": 1750000.00,
      "totalTransactions": 105,
      "totalMerchants": 25
    }
  ]
}
```

## 2. Get Top Performers

**Endpoint:** `GET /{brokerId}/getTopPerformers/{financialYearId}`

**Description:** Get all top performers (buyers, sellers, merchants) for a financial year.

**Curl Command:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getTopPerformers/2024" \
  -H "Content-Type: application/json"
```

**Sample Response:**
```json
{
  "financialYearId": 2024,
  "financialYearName": "FY 2024-25",
  "topBuyersByQuantity": [
    {
      "buyerId": 101,
      "buyerName": "Rajesh Kumar",
      "firmName": "Kumar Trading Co.",
      "city": "Mumbai",
      "userType": "TRADER",
      "totalQuantityBought": 15000,
      "totalAmountSpent": 750000.00,
      "totalBrokeragePaid": 18750.00,
      "totalTransactions": 45,
      "averageTransactionSize": 333.33,
      "phoneNumber": "+91-9876543210",
      "email": "rajesh@kumartrading.com"
    }
  ],
  "topSellersByQuantity": [
    {
      "sellerId": 201,
      "sellerName": "Suresh Patel",
      "firmName": "Patel Mills",
      "city": "Ahmedabad",
      "userType": "MILLER",
      "totalQuantitySold": 18000,
      "totalAmountReceived": 900000.00,
      "totalBrokerageGenerated": 22500.00,
      "totalTransactions": 50,
      "averageTransactionSize": 360.00,
      "phoneNumber": "+91-9876543211",
      "email": "suresh@patelmills.com"
    }
  ],
  "topMerchantsByBrokerage": [
    {
      "merchantId": 301,
      "merchantName": "Amit Shah",
      "firmName": "Shah Enterprises",
      "city": "Delhi",
      "userType": "TRADER",
      "totalBrokeragePaid": 25000.00,
      "totalQuantityTraded": 20000,
      "totalQuantityBought": 12000,
      "totalQuantitySold": 8000,
      "totalAmountTraded": 1000000.00,
      "totalTransactions": 60,
      "averageBrokeragePerTransaction": 416.67,
      "averageBrokeragePerUnit": 1.25,
      "phoneNumber": "+91-9876543212",
      "email": "amit@shahenterprises.com"
    }
  ]
}
```

## 3. Get Top 5 Buyers by Quantity

**Endpoint:** `GET /{brokerId}/getTop5BuyersByQuantity/{financialYearId}`

**Description:** Get top 5 buyers by quantity for a financial year.

**Curl Command:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getTop5BuyersByQuantity/2024" \
  -H "Content-Type: application/json"
```

**Sample Response:**
```json
[
  {
    "buyerId": 101,
    "buyerName": "Rajesh Kumar",
    "firmName": "Kumar Trading Co.",
    "city": "Mumbai",
    "userType": "TRADER",
    "totalQuantityBought": 15000,
    "totalAmountSpent": 750000.00,
    "totalBrokeragePaid": 18750.00,
    "totalTransactions": 45,
    "averageTransactionSize": 333.33,
    "phoneNumber": "+91-9876543210",
    "email": "rajesh@kumartrading.com"
  },
  {
    "buyerId": 102,
    "buyerName": "Priya Sharma",
    "firmName": "Sharma Mills",
    "city": "Pune",
    "userType": "MILLER",
    "totalQuantityBought": 12000,
    "totalAmountSpent": 600000.00,
    "totalBrokeragePaid": 15000.00,
    "totalTransactions": 35,
    "averageTransactionSize": 342.86,
    "phoneNumber": "+91-9876543213",
    "email": "priya@sharmamills.com"
  }
]
```

## 4. Get Top 5 Sellers by Quantity

**Endpoint:** `GET /{brokerId}/getTop5SellersByQuantity/{financialYearId}`

**Description:** Get top 5 sellers by quantity for a financial year.

**Curl Command:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getTop5SellersByQuantity/2024" \
  -H "Content-Type: application/json"
```

**Sample Response:**
```json
[
  {
    "sellerId": 201,
    "sellerName": "Suresh Patel",
    "firmName": "Patel Mills",
    "city": "Ahmedabad",
    "userType": "MILLER",
    "totalQuantitySold": 18000,
    "totalAmountReceived": 900000.00,
    "totalBrokerageGenerated": 22500.00,
    "totalTransactions": 50,
    "averageTransactionSize": 360.00,
    "phoneNumber": "+91-9876543211",
    "email": "suresh@patelmills.com"
  },
  {
    "sellerId": 202,
    "sellerName": "Vikram Singh",
    "firmName": "Singh Trading",
    "city": "Jaipur",
    "userType": "TRADER",
    "totalQuantitySold": 14000,
    "totalAmountReceived": 700000.00,
    "totalBrokerageGenerated": 17500.00,
    "totalTransactions": 40,
    "averageTransactionSize": 350.00,
    "phoneNumber": "+91-9876543214",
    "email": "vikram@singhtrading.com"
  }
]
```

## 5. Get Top 5 Merchants by Brokerage

**Endpoint:** `GET /{brokerId}/getTop5MerchantsByBrokerage/{financialYearId}`

**Description:** Get top 5 merchants by brokerage amount for a financial year.

**Curl Command:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getTop5MerchantsByBrokerage/2024" \
  -H "Content-Type: application/json"
```

**Sample Response:**
```json
[
  {
    "merchantId": 301,
    "merchantName": "Amit Shah",
    "firmName": "Shah Enterprises",
    "city": "Delhi",
    "userType": "TRADER",
    "totalBrokeragePaid": 25000.00,
    "totalQuantityTraded": 20000,
    "totalQuantityBought": 12000,
    "totalQuantitySold": 8000,
    "totalAmountTraded": 1000000.00,
    "totalTransactions": 60,
    "averageBrokeragePerTransaction": 416.67,
    "averageBrokeragePerUnit": 1.25,
    "phoneNumber": "+91-9876543212",
    "email": "amit@shahenterprises.com"
  },
  {
    "merchantId": 302,
    "merchantName": "Neha Gupta",
    "firmName": "Gupta Industries",
    "city": "Kolkata",
    "userType": "MILLER",
    "totalBrokeragePaid": 22000.00,
    "totalQuantityTraded": 18000,
    "totalQuantityBought": 10000,
    "totalQuantitySold": 8000,
    "totalAmountTraded": 880000.00,
    "totalTransactions": 55,
    "averageBrokeragePerTransaction": 400.00,
    "averageBrokeragePerUnit": 1.22,
    "phoneNumber": "+91-9876543215",
    "email": "neha@guptaindustries.com"
  }
]
```

## 6. Refresh Analytics Cache

**Endpoint:** `POST /refreshCache/{financialYearId}`

**Description:** Refresh analytics cache for a specific financial year.

**Curl Command:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Dashboard/refreshCache/2024" \
  -H "Content-Type: application/json"
```

**Sample Response:**
```
Analytics cache refreshed successfully for financial year: 2024
```

## 7. Refresh All Analytics Cache

**Endpoint:** `POST /refreshAllCache`

**Description:** Refresh all analytics cache.

**Curl Command:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Dashboard/refreshAllCache" \
  -H "Content-Type: application/json"
```

**Sample Response:**
```
All analytics cache refreshed successfully
```

## Error Responses

### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/BrokerHub/Dashboard/1/getFinancialYearAnalytics/2024"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/BrokerHub/Dashboard/1/getFinancialYearAnalytics/2024"
}
```

## Notes for Frontend Developers

1. **Base URL**: Replace `localhost:8080` with your actual server URL
2. **Authentication**: Add authentication headers if required by your security configuration
3. **Path Parameters**: 
   - `brokerId`: The ID of the broker (Long)
   - `financialYearId`: The ID of the financial year (Long)
4. **Response Format**: All responses are in JSON format
5. **Date Format**: Dates are in ISO format (YYYY-MM-DD)
6. **Decimal Values**: All monetary values are returned as BigDecimal with 2 decimal places
7. **Error Handling**: Always check for HTTP status codes and handle 404/500 errors appropriately
8. **Caching**: Use cache refresh APIs sparingly as they can be resource-intensive operations