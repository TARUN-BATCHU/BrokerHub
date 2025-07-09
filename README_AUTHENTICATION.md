# Authentication System Documentation

## Overview
This project uses JWT (JSON Web Token) based authentication for secure API access. The system supports both web and mobile clients with proper CORS configuration.

## Authentication Flow

### 1. Public Endpoints (No Authentication Required)
- `POST /BrokerHub/Broker/createBroker` - Create new broker account
- `POST /BrokerHub/Broker/login` - Login endpoint
- `POST /BrokerHub/Broker/createPassword` - Create password after verification
- `POST /BrokerHub/Broker/verify-account` - Verify OTP
- `GET /BrokerHub/Broker/forgotPassword` - Forgot password
- `GET /login` - Login page
- `GET /BrokerHub/Broker/BrokerFirmNameExists/**` - Check firm name availability
- `GET /BrokerHub/Broker/UserNameExists/**` - Check username availability
- `POST /BrokerHub/user/createUser` - Create user account
- `GET /` - Home page
- Static resources: `/error/**`, `/templates/**`

### 2. Protected Endpoints (Authentication Required)
All other endpoints require a valid JWT token in the Authorization header.

## How to Use

### 1. Login
```bash
POST /BrokerHub/Broker/login
Content-Type: application/json

{
  "userName": "your_username",
  "password": "your_password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "your_username",
  "brokerId": 1,
  "brokerName": "Broker Name",
  "message": "Login successful"
}
```

### 2. Using the Token
Include the JWT token in the Authorization header for all protected endpoints:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. Frontend Integration

#### React/JavaScript Example:
```javascript
// Login
const login = async (username, password) => {
  const response = await fetch('/BrokerHub/Broker/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ userName: username, password: password })
  });
  
  const data = await response.json();
  if (response.ok) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('brokerId', data.brokerId);
  }
  return data;
};

// Making authenticated requests
const makeAuthenticatedRequest = async (url, options = {}) => {
  const token = localStorage.getItem('token');
  
  return fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    }
  });
};
```

#### Mobile App Integration:
Store the JWT token securely (e.g., in secure storage) and include it in all API requests.

## Token Details
- **Expiration**: 24 hours (86400000 ms)
- **Algorithm**: HS256
- **Claims**: username, brokerId, issued at, expiration

## Security Features
- Stateless authentication (no server-side sessions)
- CORS enabled for cross-origin requests
- Secure password encoding with BCrypt
- Token-based authorization
- Proper error handling for unauthorized access

## Error Responses

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Access denied"
}
```

## Configuration
JWT settings can be configured in `application.properties`:
```properties
app.jwt.secret=your-secret-key-here
app.jwt.expiration=86400000
```

## Best Practices
1. Store tokens securely on the client side
2. Include tokens in Authorization header as Bearer tokens
3. Handle token expiration gracefully
4. Use HTTPS in production
5. Implement token refresh mechanism if needed
6. Clear tokens on logout