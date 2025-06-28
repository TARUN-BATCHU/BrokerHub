# Address API Documentation

This document provides detailed information about the Address API endpoints, including example requests and responses.

## Base URL
```
http://localhost:8080/api
```

## Authentication
All endpoints require Basic Authentication:
```
Authorization: Basic <base64(username:password)>
```

## Available Endpoints

### 1. Create New Address
Creates a new address record in the system.

**Endpoint:** `POST /addresses`  
**Multi-Tenant:** ✅ (Auto-assigns broker)

**Sample Request:**
```bash
curl -X POST \
  'http://localhost:8080/api/addresses' \
  -H 'Authorization: Basic dXNlcjpwYXNz' \
  -H 'Content-Type: application/json' \
  -d '{
    "streetAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Near Central Park",
    "addressType": "BUSINESS"
}'
```

**Sample Response:**
```json
{
    "id": 1,
    "streetAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Near Central Park",
    "addressType": "BUSINESS",
    "createdAt": "2023-09-20T10:30:00",
    "updatedAt": "2023-09-20T10:30:00"
}
```

### 2. Get All Addresses
Retrieves all addresses associated with the current broker.

**Endpoint:** `GET /addresses`  
**Multi-Tenant:** ✅ (Broker-filtered)

**Sample Request:**
```bash
curl -X GET \
  'http://localhost:8080/api/addresses' \
  -H 'Authorization: Basic dXNlcjpwYXNz'
```

**Sample Response:**
```json
[
    {
        "id": 1,
        "streetAddress": "123 Main Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "landmark": "Near Central Park",
        "addressType": "BUSINESS"
    },
    {
        "id": 2,
        "streetAddress": "456 Park Avenue",
        "city": "Delhi",
        "state": "Delhi",
        "pincode": "110001",
        "landmark": "Near Metro Station",
        "addressType": "RESIDENTIAL"
    }
]
```

### 3. Get Address by ID
Retrieves a specific address by its ID.

**Endpoint:** `GET /addresses/{id}`  
**Multi-Tenant:** ✅ (Broker-filtered)

**Sample Request:**
```bash
curl -X GET \
  'http://localhost:8080/api/addresses/1' \
  -H 'Authorization: Basic dXNlcjpwYXNz'
```

**Sample Response:**
```json
{
    "id": 1,
    "streetAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Near Central Park",
    "addressType": "BUSINESS"
}
```

### 4. Update Address
Updates an existing address record.

**Endpoint:** `PUT /addresses/{id}`  
**Multi-Tenant:** ✅ (Broker-filtered)

**Sample Request:**
```bash
curl -X PUT \
  'http://localhost:8080/api/addresses/1' \
  -H 'Authorization: Basic dXNlcjpwYXNz' \
  -H 'Content-Type: application/json' \
  -d '{
    "streetAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Opposite Railway Station",
    "addressType": "BUSINESS"
}'
```

**Sample Response:**
```json
{
    "id": 1,
    "streetAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Opposite Railway Station",
    "addressType": "BUSINESS",
    "updatedAt": "2023-09-20T11:30:00"
}
```

### 5. Delete Address
Deletes an address record.

**Endpoint:** `DELETE /addresses/{id}`  
**Multi-Tenant:** ✅ (Broker-filtered)

**Sample Request:**
```bash
curl -X DELETE \
  'http://localhost:8080/api/addresses/1' \
  -H 'Authorization: Basic dXNlcjpwYXNz'
```

**Sample Response:**
```
204 No Content
```

### 6. Check if City Exists
Checks if any addresses exist in a specific city.

**Endpoint:** `GET /addresses/city/{city}/exists`  
**Multi-Tenant:** ✅ (Broker-filtered)

**Sample Request:**
```bash
curl -X GET \
  'http://localhost:8080/api/addresses/city/Mumbai/exists' \
  -H 'Authorization: Basic dXNlcjpwYXNz'
```

**Sample Response:**
```json
{
    "exists": true,
    "count": 5
}
```

### 7. Get Address by Pincode
Retrieves all addresses matching a specific pincode.

**Endpoint:** `GET /addresses/pincode/{pincode}`  
**Multi-Tenant:** ✅ (Broker-filtered)

**Sample Request:**
```bash
curl -X GET \
  'http://localhost:8080/api/addresses/pincode/400001' \
  -H 'Authorization: Basic dXNlcjpwYXNz'
```

**Sample Response:**
```json
[
    {
        "id": 1,
        "streetAddress": "123 Main Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "landmark": "Near Central Park",
        "addressType": "BUSINESS"
    },
    {
        "id": 3,
        "streetAddress": "789 Market Road",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "landmark": "Near Post Office",
        "addressType": "COMMERCIAL"
    }
]
```

## Error Responses

### 400 Bad Request
```json
{
    "timestamp": "2023-09-20T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid address data provided",
    "path": "/api/addresses"
}
```

### 404 Not Found
```json
{
    "timestamp": "2023-09-20T10:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "Address with ID 999 not found",
    "path": "/api/addresses/999"
}
```

### 403 Forbidden
```json
{
    "timestamp": "2023-09-20T10:30:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access denied to address resource",
    "path": "/api/addresses/1"
}
```

## Notes
- All requests must include proper authentication headers
- Dates are returned in ISO 8601 format
- The system automatically associates addresses with the authenticated broker
- Address types can be: RESIDENTIAL, BUSINESS, or COMMERCIAL