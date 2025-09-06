# New Ledger Details API Documentation

## Overview
A new API endpoint has been created to handle ledger details creation using seller names and product names instead of IDs.

## Endpoint
```
POST /BrokerHub/LedgerDetails/createLedgerDetailsFromNames
```

## Request Payload
The new API accepts the following payload format:

```json
{
  "brokerId": 10,
  "financialYearId": 10,
  "sellerBrokerage": 10,
  "seller_name": "SIVA PARVATHI KIRANA & GENERAL STORES",
  "order_date": "6/9/2025",
  "product_list": [
    {
      "product_name": "Fried Gram 30 kgs",
      "total_quantity": 150,
      "price": 3000
    }
  ],
  "buyers": [
    {
      "buyer_name": "GURUKRUPA INDUSTRIES",
      "buyerBrokerage": 10,
      "products": [
        {
          "product_name": "Fried Gram 30 kgs",
          "quantity": 60,
          "price": 3000
        }
      ]
    }
  ]
}
```

## Field Descriptions

### Root Level Fields
- `brokerId` (Long): The broker ID
- `financialYearId` (Long, optional): Financial year ID. If not provided, current active financial year will be used
- `sellerBrokerage` (Long): Brokerage amount for the seller
- `seller_name` (String): Name of the seller (must exist in the system)
- `order_date` (String): Date in format "M/d/yyyy" (e.g., "6/9/2025")
- `product_list` (Array): List of products being sold
- `buyers` (Array): List of buyers and their purchases

### Product List Fields
- `product_name` (String): Name of the product (must exist in the system)
- `total_quantity` (Long): Total quantity available
- `price` (Long): Price per unit

### Buyer Fields
- `buyer_name` (String): Name of the buyer (must exist in the system)
- `buyerBrokerage` (Long): Brokerage amount for this buyer
- `products` (Array): List of products this buyer is purchasing

### Buyer Product Fields
- `product_name` (String): Name of the product (must exist in the system)
- `quantity` (Long): Quantity being purchased
- `price` (Long): Price per unit

## Response
Returns the transaction number (Long) if successful, or error message if validation fails.

## Validation Rules
1. **Seller Name**: Must exist in the system for the given broker
2. **Buyer Names**: All buyer names must exist in the system for the given broker
3. **Product Names**: All product names must exist in the system for the given broker
4. **Date Format**: Must be in "M/d/yyyy" format
5. **Financial Year**: If not provided, system will use current active financial year

## Error Responses
- `400 Bad Request`: Validation errors (seller not found, product not found, etc.)
- `500 Internal Server Error`: System errors

## Example Usage

```bash
curl 'http://localhost:8080/BrokerHub/LedgerDetails/createLedgerDetailsFromNames' \
  -H 'Accept: application/json, text/plain, */*' \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
  -H 'Content-Type: application/json' \
  --data-raw '{
    "brokerId": 10,
    "financialYearId": 10,
    "sellerBrokerage": 10,
    "seller_name": "SIVA PARVATHI KIRANA & GENERAL STORES",
    "order_date": "6/9/2025",
    "product_list": [
      {
        "product_name": "Fried Gram 30 kgs",
        "total_quantity": 150,
        "price": 3000
      }
    ],
    "buyers": [
      {
        "buyer_name": "GURUKRUPA INDUSTRIES",
        "buyerBrokerage": 10,
        "products": [
          {
            "product_name": "Fried Gram 30 kgs",
            "quantity": 60,
            "price": 3000
          }
        ]
      }
    ]
  }'
```

## Implementation Details
- The new API internally converts the name-based payload to the existing ID-based format
- It then calls the existing `createLedgerDetails` method to maintain consistency
- All existing business logic and validations are preserved
- No changes were made to the existing API or database structure