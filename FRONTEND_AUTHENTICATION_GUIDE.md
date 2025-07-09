# BrokerHub Frontend Authentication Guide

## 🔐 Authentication System Overview

BrokerHub uses **JWT (JSON Web Token)** based authentication. This means:
- No server-side sessions
- Stateless authentication
- Token-based security
- Works perfectly with React and mobile apps

---

## 📋 API Endpoints Classification

### 🟢 Public Endpoints (No Authentication Required)
These endpoints can be called without any authentication headers:

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/BrokerHub/Broker/createBroker` | Create new broker account |
| POST | `/BrokerHub/Broker/login` | User login |
| POST | `/BrokerHub/Broker/createPassword` | Create password after verification |
| POST | `/BrokerHub/Broker/verify-account` | Verify OTP |
| GET | `/BrokerHub/Broker/forgotPassword` | Forgot password |
| GET | `/BrokerHub/Broker/BrokerFirmNameExists/{firmName}` | Check firm name availability |
| GET | `/BrokerHub/Broker/UserNameExists/{userName}` | Check username availability |
| POST | `/BrokerHub/user/createUser` | Create user account |
| GET | `/login` | Login page |
| GET | `/` | Home page |

### 🔒 Protected Endpoints (Authentication Required)
All other endpoints require JWT token in Authorization header.

---

## 🚀 Implementation Guide

### 1. Login Implementation

#### Request:
```javascript
const login = async (userName, password) => {
  try {
    const response = await fetch('/BrokerHub/Broker/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        userName: userName,
        password: password
      })
    });

    const data = await response.json();
    
    if (response.ok) {
      // Store token and user info
      localStorage.setItem('authToken', data.token);
      localStorage.setItem('brokerId', data.brokerId);
      localStorage.setItem('brokerName', data.brokerName);
      localStorage.setItem('userName', data.username);
      
      return { success: true, data };
    } else {
      return { success: false, error: data };
    }
  } catch (error) {
    return { success: false, error: 'Network error' };
  }
};
```

#### Success Response (200):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJicm9rZXIxIiwiYnJva2VySWQiOjEsImlhdCI6MTcwMzI1NjAwMCwiZXhwIjoxNzAzMzQyNDAwfQ.signature",
  "type": "Bearer",
  "username": "broker1",
  "brokerId": 1,
  "brokerName": "ABC Trading",
  "message": "Login successful"
}
```

#### Error Response (401):
```json
"Invalid username or password"
```

### 2. Making Authenticated Requests

#### Create Axios Instance (Recommended):
```javascript
import axios from 'axios';

// Create axios instance with base configuration
const apiClient = axios.create({
  baseURL: 'http://localhost:8080', // Your backend URL
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle token expiration
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('authToken');
      localStorage.removeItem('brokerId');
      localStorage.removeItem('brokerName');
      localStorage.removeItem('userName');
      
      // Redirect to login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

#### Using the API Client:
```javascript
// Example: Get dashboard data
const getDashboardData = async (brokerId, financialYearId) => {
  try {
    const response = await apiClient.get(
      `/BrokerHub/Dashboard/${brokerId}/getFinancialYearAnalytics/${financialYearId}`
    );
    return response.data;
  } catch (error) {
    console.error('Error fetching dashboard data:', error);
    throw error;
  }
};

// Example: Create ledger details
const createLedgerDetails = async (ledgerData) => {
  try {
    const response = await apiClient.post(
      '/BrokerHub/LedgerDetails/createLedgerDetails',
      ledgerData
    );
    return response.data;
  } catch (error) {
    console.error('Error creating ledger details:', error);
    throw error;
  }
};
```

### 3. Authentication Context (React)

#### Create Auth Context:
```javascript
// contexts/AuthContext.js
import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    const brokerId = localStorage.getItem('brokerId');
    const brokerName = localStorage.getItem('brokerName');
    const userName = localStorage.getItem('userName');

    if (token && brokerId) {
      setIsAuthenticated(true);
      setUser({
        brokerId: parseInt(brokerId),
        brokerName,
        userName,
        token
      });
    }
    setLoading(false);
  }, []);

  const login = async (userName, password) => {
    try {
      const response = await fetch('/BrokerHub/Broker/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userName, password })
      });

      const data = await response.json();

      if (response.ok) {
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('brokerId', data.brokerId);
        localStorage.setItem('brokerName', data.brokerName);
        localStorage.setItem('userName', data.username);

        setIsAuthenticated(true);
        setUser({
          brokerId: data.brokerId,
          brokerName: data.brokerName,
          userName: data.username,
          token: data.token
        });

        return { success: true };
      } else {
        return { success: false, error: data };
      }
    } catch (error) {
      return { success: false, error: 'Network error' };
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('brokerId');
    localStorage.removeItem('brokerName');
    localStorage.removeItem('userName');
    
    setIsAuthenticated(false);
    setUser(null);
  };

  const value = {
    isAuthenticated,
    user,
    login,
    logout,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
```

#### Protected Route Component:
```javascript
// components/ProtectedRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

export default ProtectedRoute;
```

### 4. Login Component Example

```javascript
// components/Login.js
import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    const result = await login(userName, password);

    if (result.success) {
      navigate('/dashboard');
    } else {
      setError(result.error || 'Login failed');
    }
    
    setLoading(false);
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Username:</label>
        <input
          type="text"
          value={userName}
          onChange={(e) => setUserName(e.target.value)}
          required
        />
      </div>
      
      <div>
        <label>Password:</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>
      
      {error && <div style={{color: 'red'}}>{error}</div>}
      
      <button type="submit" disabled={loading}>
        {loading ? 'Logging in...' : 'Login'}
      </button>
    </form>
  );
};

export default Login;
```

---

## 📱 Mobile App Integration

### React Native Example:
```javascript
import AsyncStorage from '@react-native-async-storage/async-storage';

// Login function
const login = async (userName, password) => {
  try {
    const response = await fetch('http://your-backend-url/BrokerHub/Broker/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userName, password })
    });

    const data = await response.json();

    if (response.ok) {
      // Store securely
      await AsyncStorage.setItem('authToken', data.token);
      await AsyncStorage.setItem('brokerId', data.brokerId.toString());
      await AsyncStorage.setItem('brokerName', data.brokerName);
      
      return { success: true, data };
    } else {
      return { success: false, error: data };
    }
  } catch (error) {
    return { success: false, error: 'Network error' };
  }
};

// Authenticated request function
const makeAuthenticatedRequest = async (url, options = {}) => {
  const token = await AsyncStorage.getItem('authToken');
  
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

---

## 🔧 Error Handling

### Common HTTP Status Codes:

| Status | Meaning | Action Required |
|--------|---------|----------------|
| 200 | Success | Continue normally |
| 400 | Bad Request | Check request data |
| 401 | Unauthorized | Redirect to login |
| 403 | Forbidden | Show access denied |
| 404 | Not Found | Show not found message |
| 500 | Server Error | Show error message |

### Error Response Examples:

#### 401 Unauthorized:
```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

#### 400 Bad Request:
```json
"Username or password is missing"
```

---

## ✅ Checklist for Frontend Implementation

### Initial Setup:
- [ ] Install axios or configure fetch
- [ ] Create authentication context
- [ ] Set up protected routes
- [ ] Create login component
- [ ] Implement token storage

### For Each API Call:
- [ ] Check if endpoint requires authentication
- [ ] Include Bearer token in Authorization header
- [ ] Handle 401 responses (redirect to login)
- [ ] Handle other error responses appropriately

### Security Best Practices:
- [ ] Store tokens securely (localStorage for web, secure storage for mobile)
- [ ] Clear tokens on logout
- [ ] Handle token expiration
- [ ] Use HTTPS in production
- [ ] Validate responses before using data

---

## 🔄 Token Lifecycle

1. **Login**: Receive token, store securely
2. **API Calls**: Include token in Authorization header
3. **Token Expiry**: Handle 401 responses, redirect to login
4. **Logout**: Clear stored tokens
5. **App Restart**: Check for stored valid token

---

## 🌐 CORS Configuration

The backend is configured to accept requests from:
- `http://localhost:3000` (React dev server)
- `http://127.0.0.1:3000`
- `http://192.168.0.128:3000`
- `http://192.168.0.111:3000`

If you need additional origins, request backend team to update CORS configuration.

---

## 🆘 Troubleshooting

### Common Issues:

1. **401 Unauthorized on protected endpoints**
   - Check if token is included in Authorization header
   - Verify token format: `Bearer <token>`
   - Check if token has expired

2. **CORS errors**
   - Ensure your frontend URL is in the allowed origins list
   - Check if you're using the correct backend URL

3. **Token not persisting**
   - Verify localStorage/AsyncStorage implementation
   - Check browser developer tools for stored tokens

4. **Login successful but subsequent requests fail**
   - Ensure token is being included in all authenticated requests
   - Check axios interceptor configuration

---

This guide provides everything needed to implement authentication in your React frontend. The JWT-based system is secure, scalable, and works perfectly with both web and mobile applications.