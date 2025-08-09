# 🌐 UI Integration Guide for API Endpoints

## 🔐 Authentication

All API calls require Basic Authentication:
- **Username**: `tarun`
- **Password**: `securePassword123`
- **Base64 Encoded**: `dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=`

Include this header in all requests:
```
Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=
```

## 🌍 Base URL
```
http://localhost:8080/api
```

## 📋 Common Headers
```
Content-Type: application/json
Accept: application/json
```

## 🔍 Common Response Formats

### Success Response
```json
{
  "status": "success",
  "message": "Operation completed successfully",
  "data": { ... }
}
```

### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "error": "Detailed error message"
}
```

### Paginated Response
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 100,
  "totalPages": 10,
  "first": true,
  "last": false
}
```

## 📌 Key API Endpoints with Examples

### 1. User Management

#### Get All Users
```bash
curl -X GET "http://localhost:8080/api/users" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

#### Create New User
```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "firmName": "New Traders",
    "ownerName": "John Doe",
    "city": "Mumbai",
    "userType": "TRADER",
    "gstNumber": "GST123456789"
  }'
```

### 2. Address Management

#### Get All Addresses
```bash
curl -X GET "http://localhost:8080/api/addresses" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

Expected Response:
```json
{
  "status": "success",
  "message": "Addresses retrieved successfully",
  "data": [
    {
      "id": 1,
      "street": "123 Main Street",
      "city": "Mumbai",
      "state": "Maharashtra",
      "pincode": "400001",
      "landmark": "Near Central Park",
      "isDefault": true,
      "type": "BUSINESS"
    }
  ]
}
```

#### Create New Address
```bash
curl -X POST "http://localhost:8080/api/addresses" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Near Central Park",
    "isDefault": true,
    "type": "BUSINESS"
  }'
```

#### Update Address
```bash
curl -X PUT "http://localhost:8080/api/addresses/1" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "124 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "landmark": "Near Central Park",
    "isDefault": true,
    "type": "BUSINESS"
  }'
```

#### Delete Address
```bash
curl -X DELETE "http://localhost:8080/api/addresses/1" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

#### Get Address by ID
```bash
curl -X GET "http://localhost:8080/api/addresses/1" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

#### Check if City Exists
```bash
curl -X GET "http://localhost:8080/api/addresses/city/Mumbai/exists" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

#### Get Address by Pincode
```bash
curl -X GET "http://localhost:8080/api/addresses/pincode/400001" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

### 3. Product Management

#### Get All Products (Paginated)
```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=10" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

Expected Response:
```json
{
  "content": [
    {
      "id": 1,
      "name": "Rice",
      "quality": "Premium",
      "currentPrice": 5000,
      "unit": "Bag",
      "status": "ACTIVE"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 100,
  "totalPages": 10
}
```

### 3. Payment Management

#### Get Brokerage Payments
```bash
curl -X GET "http://localhost:8080/api/payments/brokerage" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

Expected Response:
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
      "status": "PARTIAL_PAID"
    }
  ]
}
```

#### Add Part Payment
```bash
curl -X POST "http://localhost:8080/api/payments/brokerage/1/part-payment" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500,
    "method": "CASH",
    "notes": "Partial payment received",
    "paymentDate": "2024-01-20"
  }'
```

### 4. Dashboard & Analytics

#### Get Dashboard Summary
```bash
curl -X GET "http://localhost:8080/api/dashboard/summary" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

#### Get Monthly Statistics
```bash
curl -X GET "http://localhost:8080/api/dashboard/monthly-stats" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

### 5. Reports

#### Generate Custom Report
```bash
curl -X GET "http://localhost:8080/api/reports/custom?startDate=2024-01-01&endDate=2024-01-31&type=detailed" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

## 🎯 Common Use Cases

### 1. User Registration Flow
1. Create new user (POST /users)
2. Add bank details (POST /bank-details)
3. Add address (POST /addresses)

### 2. Address Management Flow
1. Check if city exists (GET /addresses/city/{city}/exists)
2. Create new address (POST /addresses)
3. Set as default if needed (PUT /addresses/{id})
4. Verify address details (GET /addresses/{id})

### 3. Multiple Address Management
1. Get all addresses (GET /addresses)
2. Add new address (POST /addresses)
3. Update existing address (PUT /addresses/{id})
4. Delete unused address (DELETE /addresses/{id})
5. Verify pincode (GET /addresses/pincode/{pincode})

### 4. Payment Processing Flow
1. Get brokerage payments (GET /payments/brokerage)
2. Add part payment (POST /payments/brokerage/{id}/part-payment)
3. Verify updated status (GET /payments/brokerage/{id})

### 3. Dashboard Data Loading
1. Get dashboard summary (GET /dashboard/summary)
2. Get monthly stats (GET /dashboard/monthly-stats)
3. Get top products (GET /dashboard/top-products)
4. Get top merchants (GET /dashboard/top-merchants)

## ⚠️ Error Handling

Common HTTP Status Codes:
- `200`: Success
- `201`: Created
- `400`: Bad Request
- `401`: Unauthorized
- `403`: Forbidden
- `404`: Not Found
- `409`: Conflict
- `500`: Internal Server Error

Example Error Response:
```json
{
  "status": "error",
  "message": "Invalid request",
  "error": "Amount must be positive"
}
```

## 📅 Date Formats
- Dates: `YYYY-MM-DD` (e.g., "2024-01-20")
- DateTime: `YYYY-MM-DDTHH:mm:ss` (e.g., "2024-01-20T14:30:00")

## 🔍 Search Parameters

### Global Search
```bash
curl -X GET "http://localhost:8080/api/search/global?q=rice&type=products&page=0&size=10" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

### User Search
```bash
curl -X GET "http://localhost:8080/api/users/search?property=firmName&value=Traders" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -H "Content-Type: application/json"
```

## 💡 Tips for UI Integration

1. **Authentication**
   - Store auth token securely
   - Implement token refresh mechanism
   - Handle 401/403 errors globally

2. **Error Handling**
   - Show user-friendly error messages
   - Implement retry logic for network errors
   - Log errors for debugging

3. **Loading States**
   - Show loading indicators during API calls
   - Implement skeleton screens
   - Cache responses when appropriate

4. **Data Formatting**
   - Format dates using a consistent library
   - Format numbers and currencies
   - Handle empty/null values

5. **Pagination**
   - Implement infinite scroll or pagination controls
   - Show loading state for next page
   - Cache previous pages

6. **Search & Filters**
   - Implement debouncing for search
   - Show loading state during search
   - Cache recent searches

7. **Form Submissions**
   - Validate data before submission
   - Show success/error messages
   - Implement form state management

8. **Real-time Updates**
   - Implement polling where needed
   - Update local data after mutations
   - Handle concurrent updates

9. **Address Management**
   - Validate pincode format before submission
   - Implement city autocomplete using city/exists endpoint
   - Cache frequently accessed addresses
   - Show default address prominently
   - Confirm before address deletion
   - Implement address type filtering (BUSINESS/RESIDENTIAL)
   - Handle multiple addresses efficiently
   - Validate required fields (street, city, state, pincode)

10. **Address Form UX**
    - Implement pincode lookup for city/state autofill
    - Show address type selection clearly
    - Provide clear default address toggle
    - Implement address card view for multiple addresses
    - Show edit/delete actions on address hover
    - Validate pincode format in real-time
    - Show address verification status if applicable

## 🔧 Development Setup

1. Set up environment variables for API URL
2. Configure authentication interceptor
3. Set up error handling interceptor
4. Implement API service layer
5. Set up state management
6. Configure date/time formatting
7. Set up logging service

---

**Note**: Replace `localhost:8080` with your actual API server URL in production. Always use HTTPS in production environments.