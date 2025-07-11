# BrokerHub API Documentation

## LedgerDetails API
**Base URL**: `http://localhost:8080/BrokerHub/LedgerDetails`

## FinancialYear API  
**Base URL**: `http://localhost:8080/BrokerHub/FinancialYear`

## Transaction Number System - Detailed Explanation

### How Transaction Numbers Work
The transaction number system is designed to match how brokers work in real life:

1. **Physical Book Analogy**: Just like brokers maintain physical ledger books where they write transaction #1, #2, #3... for each financial year
2. **Per Broker Isolation**: Each broker has their own sequence starting from 1
3. **Per Financial Year Reset**: When a new financial year starts, all brokers reset their transaction numbers to 1
4. **System Uniqueness**: While brokers see simple numbers (1,2,3...), the system ensures uniqueness using the combination

### Transaction Number Examples
```
Broker A, Financial Year 2024: 1, 2, 3, 4, 5...
Broker A, Financial Year 2025: 1, 2, 3, 4, 5... (resets)
Broker B, Financial Year 2024: 1, 2, 3, 4, 5... (independent)
Broker B, Financial Year 2025: 1, 2, 3, 4, 5... (independent)
```

### Database Storage
- **brokerTransactionNumber**: The simple number brokers see (1, 2, 3...)
- **brokerId**: Which broker owns this transaction
- **financialYearId**: Which financial year this belongs to
- **ledgerDetailsId**: System-generated unique ID (internal use only)

### API Usage Pattern
When brokers want to:
- **Create**: System auto-generates next transaction number for their financial year
- **Fetch**: They provide their transaction number + their broker ID + financial year (optional if current FY is set)
- **Update**: They provide their transaction number + their broker ID + financial year (optional if current FY is set)

### Current Financial Year System
To simplify UI operations, brokers can set a "current financial year" preference:
1. **Set Current FY**: Broker sets their active financial year once
2. **Auto-Use**: All subsequent operations use this FY automatically if not specified
3. **Override**: Can still specify different FY when needed

---

## Financial Year Management APIs

### 1. Set Current Financial Year
**POST** `/BrokerHub/FinancialYear/setCurrentFinancialYear`

### Description
Sets the current active financial year for the broker. This will be used automatically in all ledger operations when financial year is not explicitly provided.

### Query Parameters
- `financialYearId` (Long, required) - Financial year ID to set as current

### Headers
- `Authorization: Bearer <token>` (required) - JWT token

### cURL Command
```bash
curl -X POST "http://localhost:8080/BrokerHub/FinancialYear/setCurrentFinancialYear?financialYearId=2024" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Success Response (200 OK)
```json
"Current financial year set successfully"
```

### Error Responses
#### 400 Bad Request
```json
"Invalid or missing authorization token"
```

#### 500 Internal Server Error
```json
"Failed to set current financial year"
```

---

### 2. Get Current Financial Year
**GET** `/BrokerHub/FinancialYear/getCurrentFinancialYear`

### Description
Retrieves the current active financial year for the broker.

### Headers
- `Authorization: Bearer <token>` (required) - JWT token

### cURL Command
```bash
curl -X GET "http://localhost:8080/BrokerHub/FinancialYear/getCurrentFinancialYear" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Success Response (200 OK)
```json
2024
```

### Error Responses
#### 404 Not Found
```json
// No current financial year set
```

#### 400 Bad Request
```json
// Invalid or missing authorization token
```

---

## Ledger Details APIs

### Updated Behavior
- **financialYearId is now OPTIONAL** in transaction number based operations
- **If not provided**: System uses broker's current financial year
- **If provided**: Uses the specified financial year (overrides current)

---

## 1. Create Ledger Details
**POST** `/createLedgerDetails`

### Description
Creates a new ledger transaction. The system automatically assigns the next transaction number for the broker's financial year.

### cURL Command
```bash
curl -X POST "http://localhost:8080/BrokerHub/LedgerDetails/createLedgerDetails" \
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
      },
      {
        "productId": 2,
        "buyerName": "XYZ Corp",
        "quantity": 50,
        "productCost": 2000,
        "brokerage": 30
      }
    ]
  }'
```

### Request Body (Detailed)
```json
{
  "brokerId": 1,                    // Required: Broker creating this transaction
  "financialYearId": 2024,          // Optional: Financial year (uses current FY if not provided)
  "fromSeller": 123,               // Required: Seller user ID
  "date": "2024-01-15",            // Required: Transaction date (YYYY-MM-DD)
  "brokerage": 50,                 // Optional: Overall brokerage
  "sellerBrokerage": "2%",         // Optional: Seller brokerage percentage
  "ledgerRecordDTOList": [         // Required: List of individual sales records
    {
      "productId": 1,             // Required: Product being sold
      "buyerName": "ABC Traders",  // Required: Buyer firm name (must exist in system)
      "quantity": 100,            // Required: Quantity in bags/units
      "productCost": 1500,        // Required: Cost per unit
      "brokerage": 25             // Required: Brokerage per unit
    }
  ]
}
```

### Simplified Request (Using Current Financial Year)
```json
{
  "brokerId": 1,
  "fromSeller": 123,
  "date": "2024-01-15",
  "ledgerRecordDTOList": [...]
}
```
*Note: financialYearId omitted - system uses current FY*

### Success Response (201 Created)
```json
"Successfully"
```

### Transaction Number Assignment
If this is:
- **First transaction** for Broker 1 in FY 2024 → Gets transaction number **1**
- **Fifth transaction** for Broker 1 in FY 2024 → Gets transaction number **5**
- **First transaction** for Broker 1 in FY 2025 → Gets transaction number **1** (resets)

### Error Responses

#### 400 Bad Request
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Financial year ID cannot be null",
  "path": "/BrokerHub/LedgerDetails/createLedgerDetails"
}
```

#### 500 Internal Server Error
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Failed to create ledger details",
  "path": "/BrokerHub/LedgerDetails/createLedgerDetails"
}
```

---

## 2. Get All Ledger Details
**GET** `/getAllLedgerDetails`

### Description
Retrieves all ledger details for the current broker (uses tenant context).

### cURL Command
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getAllLedgerDetails" \
  -H "Content-Type: application/json" \
  -d '1'
```

### Request Body
```json
1  // brokerId (Long) - though actual filtering uses tenant context
```

### Success Response (200 OK)
```json
[
  {
    "ledgerDetailsId": 1,
    "brokerTransactionNumber": 1,        // Broker's transaction #1
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
      },
      {
        "ledgerRecordId": 2,
        "quantity": 50,
        "brokerage": 30,
        "productCost": 2000,
        "totalProductsCost": 100000,
        "totalBrokerage": 1500,
        "toBuyer": {
          "userId": 789,
          "firmName": "XYZ Corp",
          "payableAmount": 100000
        },
        "product": {
          "productId": 2,
          "productName": "Rice",
          "category": "Grains"
        }
      }
    ]
  },
  {
    "ledgerDetailsId": 2,
    "brokerTransactionNumber": 2,        // Broker's transaction #2
    "financialYearId": 2024,
    "broker": {
      "brokerId": 1,
      "brokerName": "ABC Brokerage"
    },
    "fromSeller": {
      "userId": 124,
      "firmName": "Village Farmers"
    },
    "dailyLedger": {
      "date": "2024-01-16"
    },
    "records": [...]
  }
]
```

### Empty Response (200 OK)
```json
[]
```

---

## 3. Get Ledger Details by ID
**GET** `/getLedgerDetailsById`

### Query Parameters
- `ledgerDetailId` (Long, required) - Internal ledger detail ID
- `brokerId` (Long, required) - Broker ID for authorization

### Example
```
GET /getLedgerDetailsById?ledgerDetailId=1&brokerId=1
```

### Response
```json
{
  "ledgerDetailsId": 1,
  "brokerTransactionNumber": 1,
  "financialYearId": 2024,
  "broker": { "brokerId": 1 },
  "fromSeller": { "userId": 123, "firmName": "Seller Corp" },
  "records": [...]
}
```

---

## 4. Get Ledger Details by Transaction Number
**GET** `/getLedgerDetailsByTransactionNumber`

### Description
Retrieves a specific transaction using the broker's transaction number. This is the primary way brokers fetch their transactions.

### Query Parameters
- `transactionNumber` (Long, required) - Broker's transaction number (1, 2, 3...)
- `brokerId` (Long, required) - Broker ID for authorization
- `financialYearId` (Long, required) - Financial year ID

### cURL Command
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1&financialYearId=2024"
```

### Real-world Usage Example
**Scenario**: Broker wants to check their transaction #5 from financial year 2024
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber?transactionNumber=5&brokerId=1&financialYearId=2024"
```

### Success Response (200 OK)
```json
{
  "ledgerDetailsId": 1,                    // System internal ID
  "brokerTransactionNumber": 1,            // Broker's transaction #1
  "financialYearId": 2024,
  "broker": {
    "brokerId": 1,
    "brokerName": "ABC Brokerage",
    "contactInfo": "+91-9876543210",
    "totalBrokerage": 125000.50
  },
  "fromSeller": {
    "userId": 123,
    "firmName": "Farmer's Cooperative",
    "contactNumber": "+91-9876543210",
    "address": {
      "addressId": 1,
      "street": "Main Road",
      "city": "Pune",
      "state": "Maharashtra",
      "pincode": "411001"
    },
    "totalBagsSold": 500,
    "receivableAmount": 750000,
    "totalPayableBrokerage": 12500.00
  },
  "dailyLedger": {
    "dailyLedgerId": 1,
    "date": "2024-01-15",
    "totalTransactions": 5,
    "totalBrokerage": 15000.00,
    "financialYear": {
      "financialYearId": 2024,
      "startDate": "2024-04-01",
      "endDate": "2025-03-31"
    }
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
        "contactNumber": "+91-9876543211",
        "address": {
          "city": "Mumbai",
          "state": "Maharashtra"
        },
        "totalBagsBought": 300,
        "payableAmount": 450000,
        "totalPayableBrokerage": 7500.00
      },
      "product": {
        "productId": 1,
        "productName": "Wheat",
        "category": "Grains",
        "unit": "Quintal",
        "description": "Premium quality wheat"
      }
    },
    {
      "ledgerRecordId": 2,
      "quantity": 50,
      "brokerage": 30,
      "productCost": 2000,
      "totalProductsCost": 100000,
      "totalBrokerage": 1500,
      "toBuyer": {
        "userId": 789,
        "firmName": "XYZ Corp",
        "contactNumber": "+91-9876543212"
      },
      "product": {
        "productId": 2,
        "productName": "Rice",
        "category": "Grains"
      }
    }
  ]
}
```

### Transaction Not Found (404 Not Found)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "No ledger details found for transaction number: 999 in financial year: 2024",
  "path": "/BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber"
}
```

### Invalid Parameters (400 Bad Request)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Transaction number and financial year ID cannot be null",
  "path": "/BrokerHub/LedgerDetails/getLedgerDetailsByTransactionNumber"
}
```

---

## 5. Get Optimized Ledger Details by ID
**GET** `/getOptimizedLedgerDetailsById`

### Query Parameters
- `ledgerDetailId` (Long, required) - Internal ledger detail ID
- `brokerId` (Long, required) - Broker ID for authorization

### Example
```
GET /getOptimizedLedgerDetailsById?ledgerDetailId=1&brokerId=1
```

### Response
```json
{
  "ledgerDetailsId": 1,
  "brokerTransactionNumber": 1,
  "transactionDate": "2024-01-15",
  "fromSeller": {
    "userId": 123,
    "firmName": "Seller Corp",
    "addressId": 1
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
        "addressId": 2
      },
      "product": {
        "productId": 1,
        "productName": "Wheat"
      }
    }
  ],
  "transactionSummary": {
    "totalBagsSoldInTransaction": 100,
    "totalBrokerageInTransaction": 2500.00,
    "totalReceivableAmountInTransaction": 150000,
    "averageBrokeragePerBag": 25.00,
    "numberOfProducts": 1,
    "numberOfBuyers": 1
  }
}
```

### Status Codes
- `200 OK` - Success
- `404 Not Found` - Ledger not found
- `400 Bad Request` - Invalid parameters
- `500 Internal Server Error` - Server error

---

## 6. Get Optimized Ledger Details by Transaction Number
**GET** `/getOptimizedLedgerDetailsByTransactionNumber`

### Description
Retrieves optimized ledger details by transaction number. This endpoint solves lazy loading issues and provides better performance with pre-calculated summaries.

### Query Parameters
- `transactionNumber` (Long, required) - Broker's transaction number (1, 2, 3...)
- `brokerId` (Long, required) - Broker ID for authorization
- `financialYearId` (Long, required) - Financial year ID

### cURL Command
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1&financialYearId=2024"
```

### Real-world Usage Examples

#### Example 1: Broker checking transaction #1 from FY 2024
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber?transactionNumber=1&brokerId=1&financialYearId=2024"
```

#### Example 2: Broker checking transaction #25 from FY 2023
```bash
curl -X GET "http://localhost:8080/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber?transactionNumber=25&brokerId=1&financialYearId=2023"
```

### Success Response (200 OK)
```json
{
  "ledgerDetailsId": 1,
  "brokerTransactionNumber": 1,              // This is transaction #1 for this broker in FY 2024
  "transactionDate": "2024-01-15",
  "fromSeller": {
    "userId": 123,
    "firmName": "Farmer's Cooperative",
    "addressId": 1
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
        "addressId": 2
      },
      "product": {
        "productId": 1,
        "productName": "Wheat"
      }
    },
    {
      "ledgerRecordId": 2,
      "quantity": 75,
      "brokerage": 30,
      "productCost": 1800,
      "totalProductsCost": 135000,
      "totalBrokerage": 2250,
      "toBuyer": {
        "userId": 789,
        "firmName": "XYZ Corp",
        "addressId": 3
      },
      "product": {
        "productId": 2,
        "productName": "Rice"
      }
    }
  ],
  "transactionSummary": {
    "totalBagsSoldInTransaction": 175,         // 100 + 75
    "totalBrokerageInTransaction": 4750.00,   // 2500 + 2250
    "totalReceivableAmountInTransaction": 285000, // 150000 + 135000
    "averageBrokeragePerBag": 27.14,          // 4750 / 175
    "numberOfProducts": 2,                    // Wheat + Rice
    "numberOfBuyers": 2                       // ABC Traders + XYZ Corp
  }
}
```

### Multiple Transactions Comparison

#### Transaction #1 Response
```json
{
  "brokerTransactionNumber": 1,
  "transactionSummary": {
    "totalBagsSoldInTransaction": 175,
    "totalBrokerageInTransaction": 4750.00
  }
}
```

#### Transaction #2 Response (Different transaction)
```json
{
  "brokerTransactionNumber": 2,
  "transactionSummary": {
    "totalBagsSoldInTransaction": 200,
    "totalBrokerageInTransaction": 6000.00
  }
}
```

### Error Responses

#### Transaction Not Found (404 Not Found)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "No optimized ledger details found for transaction number: 999",
  "path": "/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber"
}
```

#### Invalid Parameters (400 Bad Request)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Transaction number and financial year ID cannot be null",
  "path": "/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber"
}
```

#### Server Error (500 Internal Server Error)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Failed to fetch optimized ledger details for transaction number: 1",
  "path": "/BrokerHub/LedgerDetails/getOptimizedLedgerDetailsByTransactionNumber"
}
```

---

## 7. Get Ledger Details by Date
**GET** `/getLedgerDetailsByDate`

### Query Parameters
- `date` (LocalDate, required) - Transaction date (YYYY-MM-DD)
- `brokerId` (Long, required) - Broker ID

### Example
```
GET /getLedgerDetailsByDate?date=2024-01-15&brokerId=1
```

### Response
```json
[
  {
    "date": "2024-01-15",
    "sellerName": "Seller Corp",
    "displayLedgerRecordDTOList": [
      {
        "buyerName": "ABC Traders",
        "productName": "Wheat",
        "quantity": 100,
        "productCost": 1500,
        "brokerage": 25,
        "total": 150000
      }
    ]
  }
]
```

---

## 8. Get Ledger Details by Seller
**GET** `/getLedgerDetailsBySeller`

### Query Parameters
- `sellerId` (Long, required) - Seller user ID
- `brokerId` (Long, required) - Broker ID

### Example
```
GET /getLedgerDetailsBySeller?sellerId=123&brokerId=1
```

### Response
```json
[
  {
    "brokerId": 1,
    "financialYearId": 2024,
    "fromSeller": 123,
    "date": "2024-01-15",
    "ledgerRecordDTOList": [...]
  }
]
```

---

## 9. Update Ledger Details by Transaction Number
**PUT** `/updateLedgerDetailByTransactionNumber`

### Description
Updates an existing transaction using the broker's transaction number. This completely replaces the existing transaction data.

### Query Parameters
- `transactionNumber` (Long, required) - Broker's transaction number (1, 2, 3...)
- `brokerId` (Long, required) - Broker ID for authorization
- `financialYearId` (Long, required) - Financial year ID

### cURL Command
```bash
curl -X PUT "http://localhost:8080/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber?transactionNumber=1&brokerId=1&financialYearId=2024" \
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
      },
      {
        "productId": 2,
        "buyerName": "New Buyer Corp",
        "quantity": 75,
        "productCost": 1800,
        "brokerage": 35
      }
    ]
  }'
```

### Real-world Usage Examples

#### Example 1: Broker updating their transaction #5 from FY 2024
```bash
curl -X PUT "http://localhost:8080/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber?transactionNumber=5&brokerId=1&financialYearId=2024" \
  -H "Content-Type: application/json" \
  -d '{
    "fromSeller": 125,
    "date": "2024-01-20",
    "ledgerRecordDTOList": [
      {
        "productId": 3,
        "buyerName": "Updated Buyer",
        "quantity": 200,
        "productCost": 1700,
        "brokerage": 28
      }
    ]
  }'
```

#### Example 2: Broker correcting transaction #1 from previous financial year
```bash
curl -X PUT "http://localhost:8080/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber?transactionNumber=1&brokerId=1&financialYearId=2023" \
  -H "Content-Type: application/json" \
  -d '{
    "fromSeller": 100,
    "date": "2023-05-15",
    "ledgerRecordDTOList": [
      {
        "productId": 1,
        "buyerName": "Corrected Buyer Name",
        "quantity": 120,
        "productCost": 1400,
        "brokerage": 25
      }
    ]
  }'
```

### Request Body (Detailed)
```json
{
  "fromSeller": 123,                    // Optional: Update seller (user ID)
  "date": "2024-01-15",                // Optional: Update transaction date
  "ledgerRecordDTOList": [             // Optional: Update all records (replaces existing)
    {
      "productId": 1,                 // Required if updating records
      "buyerName": "ABC Traders",      // Required if updating records
      "quantity": 150,                // Required if updating records
      "productCost": 1600,            // Required if updating records
      "brokerage": 30                 // Required if updating records
    },
    {
      "productId": 2,
      "buyerName": "New Buyer Corp",
      "quantity": 75,
      "productCost": 1800,
      "brokerage": 35
    }
  ]
}
```

### Success Response (200 OK)
```json
"Ledger details updated successfully"
```

### Update Process Explanation
1. **Find Transaction**: System finds transaction using `brokerId + transactionNumber + financialYearId`
2. **Validate**: Ensures transaction exists and belongs to the broker
3. **Replace Data**: Completely replaces existing records with new data
4. **Recalculate**: Updates all totals and brokerage calculations
5. **Save**: Persists changes to database

### Before and After Update Example

#### Before Update (Transaction #1)
```json
{
  "brokerTransactionNumber": 1,
  "records": [
    {
      "quantity": 100,
      "productCost": 1500,
      "brokerage": 25,
      "totalBrokerage": 2500
    }
  ]
}
```

#### After Update (Same Transaction #1)
```json
{
  "brokerTransactionNumber": 1,        // Transaction number stays the same
  "records": [
    {
      "quantity": 150,                // Updated quantity
      "productCost": 1600,            // Updated cost
      "brokerage": 30,                // Updated brokerage
      "totalBrokerage": 4500          // Recalculated (150 * 30)
    },
    {
      "quantity": 75,                 // New record added
      "productCost": 1800,
      "brokerage": 35,
      "totalBrokerage": 2625
    }
  ]
}
```

### Error Responses

#### Transaction Not Found (404 Not Found)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Ledger details not found with transaction number: 999",
  "path": "/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber"
}
```

#### Invalid Parameters (400 Bad Request)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Transaction number and financial year ID cannot be null",
  "path": "/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber"
}
```

#### Invalid Data (400 Bad Request)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Buyer name 'NonExistentBuyer' not found in system",
  "path": "/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber"
}
```

#### Server Error (500 Internal Server Error)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Failed to update ledger details: Database connection error",
  "path": "/BrokerHub/LedgerDetails/updateLedgerDetailByTransactionNumber"
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "error": "Transaction number and financial year ID cannot be null"
}
```

### 404 Not Found
```json
{
  "error": "No ledger details found for transaction number: 1"
}
```

### 500 Internal Server Error
```json
{
  "error": "Failed to update ledger details"
}
```

---

## Data Models

### LedgerDetailsDTO
```json
{
  "brokerId": "Long - Broker ID",
  "financialYearId": "Long - Financial Year ID (NEW)",
  "sellerBrokerage": "String - Seller brokerage percentage",
  "brokerage": "Long - Brokerage amount",
  "fromSeller": "Long - Seller user ID",
  "date": "LocalDate - Transaction date",
  "ledgerRecordDTOList": "Array of LedgerRecordDTO"
}
```

### LedgerRecordDTO
```json
{
  "productId": "Long - Product ID",
  "buyerName": "String - Buyer firm name",
  "quantity": "Long - Quantity in bags",
  "productCost": "Long - Cost per unit",
  "brokerage": "Long - Brokerage per unit"
}
```

---

## Transaction Number Deep Dive

### Real-world Broker Workflow

#### Scenario: ABC Brokerage starts FY 2024
1. **April 1, 2024**: First transaction of the year
   - Broker creates transaction → Gets **Transaction #1**
   - System stores: `brokerId=1, transactionNumber=1, financialYearId=2024`

2. **April 2, 2024**: Second transaction
   - Broker creates transaction → Gets **Transaction #2**
   - System stores: `brokerId=1, transactionNumber=2, financialYearId=2024`

3. **March 31, 2025**: Last transaction of FY 2024
   - Broker creates transaction → Gets **Transaction #500** (if it's their 500th)
   - System stores: `brokerId=1, transactionNumber=500, financialYearId=2024`

4. **April 1, 2025**: First transaction of FY 2025
   - Broker creates transaction → Gets **Transaction #1** (resets!)
   - System stores: `brokerId=1, transactionNumber=1, financialYearId=2025`

### Multiple Brokers Example

#### Same Day, Same Transaction Numbers
```
Date: April 1, 2024

Broker A (ID: 1):
- Creates transaction → Transaction #1 (FY 2024)
- System: brokerId=1, transactionNumber=1, financialYearId=2024

Broker B (ID: 2):
- Creates transaction → Transaction #1 (FY 2024)
- System: brokerId=2, transactionNumber=1, financialYearId=2024

Both have Transaction #1, but they're completely separate!
```

### API Call Patterns

#### Pattern 1: Broker checking their recent transactions
```bash
# Check transaction #1
curl -X GET "...?transactionNumber=1&brokerId=1&financialYearId=2024"

# Check transaction #2
curl -X GET "...?transactionNumber=2&brokerId=1&financialYearId=2024"

# Check transaction #50
curl -X GET "...?transactionNumber=50&brokerId=1&financialYearId=2024"
```

#### Pattern 2: Broker checking old transactions from previous year
```bash
# Check transaction #1 from FY 2023
curl -X GET "...?transactionNumber=1&brokerId=1&financialYearId=2023"

# Check transaction #1 from FY 2024 (different transaction!)
curl -X GET "...?transactionNumber=1&brokerId=1&financialYearId=2024"
```

### Database Storage vs Broker View

#### What Broker Sees
```
My Transactions for FY 2024:
#1 - Sold 100 bags wheat to ABC Traders
#2 - Sold 50 bags rice to XYZ Corp
#3 - Sold 75 bags wheat to DEF Ltd
```

#### What System Stores
```sql
ledger_details table:
id | broker_id | broker_transaction_number | financial_year_id | ...
1  | 1         | 1                        | 2024             | ...
2  | 1         | 2                        | 2024             | ...
3  | 1         | 3                        | 2024             | ...
4  | 2         | 1                        | 2024             | ... (Different broker)
5  | 1         | 1                        | 2025             | ... (Same broker, new FY)
```

## Important Notes

1. **Transaction Number Uniqueness**: Transaction numbers are unique per broker per financial year
2. **Multi-tenant Isolation**: All operations are isolated by broker ID - brokers can only see their own data
3. **Financial Year Mandatory**: Required for all transaction number based operations
4. **Optimized Endpoints**: Use optimized endpoints for better performance with complex data and pre-calculated summaries
5. **Date Format**: Use ISO date format (YYYY-MM-DD) for date parameters
6. **Transaction Number Reset**: Numbers reset to 1 at the start of each financial year for each broker
7. **Concurrent Brokers**: Multiple brokers can have the same transaction numbers simultaneously
8. **Historical Data**: Old financial year data remains accessible using the appropriate financialYearId

## Common Use Cases

### Use Case 1: Daily Operations
```bash
# Broker creates morning transaction
POST /createLedgerDetails
# Gets transaction #15 for today

# Broker checks what they just created
GET /...?transactionNumber=15&brokerId=1&financialYearId=2024

# Broker needs to update it
PUT /...?transactionNumber=15&brokerId=1&financialYearId=2024
```

### Use Case 2: End of Day Review
```bash
# Check all transactions for today
GET /getAllLedgerDetails

# Review specific transactions
GET /...?transactionNumber=13&brokerId=1&financialYearId=2024
GET /...?transactionNumber=14&brokerId=1&financialYearId=2024
GET /...?transactionNumber=15&brokerId=1&financialYearId=2024
```

### Use Case 3: Year-end Audit
```bash
# Check first transaction of current year
GET /...?transactionNumber=1&brokerId=1&financialYearId=2024

# Check first transaction of previous year
GET /...?transactionNumber=1&brokerId=1&financialYearId=2023

# Compare year-over-year performance
```