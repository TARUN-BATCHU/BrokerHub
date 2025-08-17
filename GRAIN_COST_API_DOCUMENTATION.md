# Grain Cost Tracking API Documentation

## Overview
The Grain Cost API allows brokers to track grain prices over time for visualization and analysis purposes. The system stores product names, costs, and timestamps to help users monitor price trends.

## Base URL
```
http://localhost:8080/BrokerHub/grain-costs
```

## Authentication
All endpoints require Basic Authentication:
- **Username:** tarun
- **Password:** securePassword123

## Endpoints

### 1. Add Grain Cost Entry

**POST** `/BrokerHub/grain-costs/{brokerId}`

Adds a new grain cost entry for a specific broker.

#### Request Parameters
- `brokerId` (path parameter): The ID of the broker

#### Request Body
```json
{
    "productName": "Wheat",
    "cost": 2500.50,
    "date": "15-01-2024"  // Optional - uses current date if not provided (dd-MM-yyyy format)
}
```

#### CURL Command
```bash
curl -X POST "http://localhost:8080/BrokerHub/grain-costs/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -d '{
    "productName": "Wheat",
    "cost": 2500.50
  }'
```

#### Sample Response (Success - 200 OK)
```json
{
    "success": true,
    "message": "Grain cost added successfully",
    "data": {
        "id": 1,
        "productName": "Wheat",
        "cost": 2500.50,
        "date": "15-01-2024"
    },
    "error": null
}
```

#### Sample Response (Error - 400 Bad Request)
```json
{
    "success": false,
    "message": "Broker not found",
    "data": null,
    "error": "Invalid broker ID"
}
```

### 2. Get All Grain Costs

**GET** `/BrokerHub/grain-costs/{brokerId}`

Retrieves all grain cost entries for a specific broker, ordered by date (newest first).

#### Request Parameters
- `brokerId` (path parameter): The ID of the broker

#### CURL Command
```bash
curl -X GET "http://localhost:8080/BrokerHub/grain-costs/1" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM="
```

#### Sample Response (Success - 200 OK)
```json
{
    "success": true,
    "message": "Grain costs retrieved successfully",
    "data": [
        {
            "id": 3,
            "productName": "Rice",
            "cost": 3200.00,
            "date": "15-01-2024"
        },
        {
            "id": 2,
            "productName": "Wheat",
            "cost": 2600.00,
            "date": "15-01-2024"
        },
        {
            "id": 1,
            "productName": "Wheat",
            "cost": 2500.50,
            "date": "15-01-2024"
        }
    ],
    "error": null
}
```

#### Sample Response (Empty Data - 200 OK)
```json
{
    "success": true,
    "message": "Grain costs retrieved successfully",
    "data": [],
    "error": null
}
```

### 3. Delete Grain Cost Entry

**DELETE** `/BrokerHub/grain-costs/{brokerId}/{grainCostId}`

Deletes a specific grain cost entry. Only the broker who created the entry can delete it.

#### Request Parameters
- `brokerId` (path parameter): The ID of the broker
- `grainCostId` (path parameter): The ID of the grain cost entry to delete

#### CURL Command
```bash
curl -X DELETE "http://localhost:8080/BrokerHub/grain-costs/1/3" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM="
```

#### Sample Response (Success - 200 OK)
```json
{
    "success": true,
    "message": "Grain cost deleted successfully",
    "data": null,
    "error": null
}
```

#### Sample Response (Error - 400 Bad Request)
```json
{
    "success": false,
    "message": "Grain cost not found",
    "data": null,
    "error": "Invalid grain cost ID or access denied"
}
```

## Data Validation

### Request Validation Rules
- `productName`: Required, cannot be blank
- `cost`: Required, must be positive number
- `date`: Optional, must be in dd-MM-yyyy format, uses current date if not provided

### Error Responses
All validation errors return HTTP 400 with details:
```json
{
    "success": false,
    "message": "Validation failed",
    "data": null,
    "error": "Cost must be positive"
}
```

## Database Schema

### grain_costs Table
```sql
CREATE TABLE grain_costs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    broker_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (broker_id) REFERENCES broker(broker_id)
);
```

## Security & Multi-Tenancy

### Broker Isolation
- Each broker can only access their own grain cost data
- All operations (add, view, delete) are scoped to the authenticated broker
- Attempting to access another broker's data returns "not found" error
- Delete operations verify ownership before allowing deletion

## Usage Examples

### Complete Workflow
```bash
# Add Wheat price
curl -X POST "http://localhost:8080/BrokerHub/grain-costs/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -d '{"productName": "Wheat", "cost": 2500.50}'

# Add Rice price
curl -X POST "http://localhost:8080/BrokerHub/grain-costs/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -d '{"productName": "Rice", "cost": 3200.00}'

# Get all grain costs
curl -X GET "http://localhost:8080/BrokerHub/grain-costs/1" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM="

# Delete a specific grain cost (ID: 2)
curl -X DELETE "http://localhost:8080/BrokerHub/grain-costs/1/2" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM="
```

## Frontend Integration Notes

### For Graph Visualization
The API returns data in chronological order (newest first). For graph plotting:
1. Group data by `productName`
2. Sort by `date` for time-series visualization
3. Use `cost` values for Y-axis and `date` for X-axis

### Sample JavaScript Processing
```javascript
// Group costs by product for chart visualization
function groupCostsByProduct(costs) {
    return costs.reduce((acc, cost) => {
        if (!acc[cost.productName]) {
            acc[cost.productName] = [];
        }
        acc[cost.productName].push({
            date: cost.date,
            price: cost.cost
        });
        return acc;
    }, {});
}
```

## Error Handling

### Common HTTP Status Codes
- `200 OK`: Success
- `400 Bad Request`: Validation error, invalid broker ID, or access denied
- `401 Unauthorized`: Missing or invalid authentication
- `500 Internal Server Error`: Server error

### Error Response Format
```json
{
    "success": false,
    "message": "Error description",
    "data": null,
    "error": "Detailed error message"
}
```