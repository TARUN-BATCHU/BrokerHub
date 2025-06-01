# Multi-Tenant API Endpoints Reference

## Base URL
```
http://localhost:8080/api
```

## Authentication
All endpoints require Basic Authentication:
```
Authorization: Basic <base64(username:password)>
```

---

## 👥 USER ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `POST` | `/users` | Create new user | ✅ Auto-assigns broker |
| `GET` | `/users` | Get all users | ✅ Broker-filtered |
| `GET` | `/users/{id}` | Get user by ID | ✅ Broker-filtered |
| `PUT` | `/users/{id}` | Update user | ✅ Broker-filtered |
| `DELETE` | `/users/{id}` | Delete user | ✅ Broker-filtered |
| `GET` | `/users/city/{city}` | Get users by city | ✅ Broker-filtered |
| `GET` | `/users/search` | Search users | ✅ Broker-filtered |

### Query Parameters for Search:
- `property`: `firmName` or `gstNumber`
- `value`: Search value

---

## 📦 PRODUCT ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `POST` | `/products` | Create new product | ✅ Auto-assigns broker |
| `GET` | `/products` | Get all products (paginated) | ✅ Broker-filtered |
| `GET` | `/products/{id}` | Get product by ID | ✅ Broker-filtered |
| `PUT` | `/products/{id}` | Update product | ✅ Broker-filtered |
| `DELETE` | `/products/{id}` | Delete product | ✅ Broker-filtered |
| `GET` | `/products/name/{name}` | Get products by name | ✅ Broker-filtered |

### Query Parameters for Products:
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)

---

## 🏠 ADDRESS ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `POST` | `/addresses` | Create new address | ✅ Auto-assigns broker |
| `GET` | `/addresses` | Get all addresses | ✅ Broker-filtered |
| `GET` | `/addresses/{id}` | Get address by ID | ✅ Broker-filtered |
| `PUT` | `/addresses/{id}` | Update address | ✅ Broker-filtered |
| `DELETE` | `/addresses/{id}` | Delete address | ✅ Broker-filtered |
| `GET` | `/addresses/city/{city}/exists` | Check if city exists | ✅ Broker-filtered |
| `GET` | `/addresses/pincode/{pincode}` | Get address by pincode | ✅ Broker-filtered |

---

## 🏦 BANK DETAILS ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `POST` | `/bank-details` | Create bank details | ✅ Auto-assigns broker |
| `GET` | `/bank-details` | Get all bank details | ✅ Broker-filtered |
| `GET` | `/bank-details/{id}` | Get bank details by ID | ✅ Broker-filtered |
| `PUT` | `/bank-details/{id}` | Update bank details | ✅ Broker-filtered |
| `DELETE` | `/bank-details/{id}` | Delete bank details | ✅ Broker-filtered |
| `GET` | `/bank-details/account/{accountNumber}` | Get by account number | ✅ Broker-filtered |

---

## 📊 DAILY LEDGER ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `POST` | `/daily-ledger` | Create daily ledger | ✅ Auto-assigns broker |
| `GET` | `/daily-ledger/{date}` | Get daily ledger by date | ✅ Broker-filtered |
| `GET` | `/daily-ledger/{date}/optimized` | Get optimized daily ledger | ✅ Broker-filtered |
| `GET` | `/daily-ledger/{date}/paginated` | Get paginated daily ledger | ✅ Broker-filtered |
| `GET` | `/daily-ledger/{date}/pdf` | Generate PDF report | ✅ Broker-filtered |

### Query Parameters for Paginated:
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)

---

## 📋 LEDGER DETAILS ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `POST` | `/ledger-details` | Create ledger details | ✅ Auto-assigns broker |
| `GET` | `/ledger-details` | Get all ledger details | ✅ Broker-filtered |
| `GET` | `/ledger-details/{id}` | Get ledger details by ID | ✅ Broker-filtered |
| `GET` | `/ledger-details/{id}/optimized` | Get optimized ledger details | ✅ Broker-filtered |
| `GET` | `/ledger-details/date/{date}` | Get ledger details by date | ✅ Broker-filtered |
| `GET` | `/ledger-details/seller/{sellerId}` | Get by seller ID | ✅ Broker-filtered |

---

## 💰 PAYMENT ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `GET` | `/payments/brokerage` | Get brokerage payments | ✅ Broker-filtered |
| `GET` | `/payments/brokerage/{id}` | Get brokerage payment by ID | ✅ Broker-filtered |
| `POST` | `/payments/brokerage` | Create brokerage payment | ✅ Auto-assigns broker |
| `PUT` | `/payments/brokerage/{id}` | Update brokerage payment | ✅ Broker-filtered |
| `GET` | `/payments/pending` | Get pending payments | ✅ Broker-filtered |
| `GET` | `/payments/receivable` | Get receivable payments | ✅ Broker-filtered |
| `POST` | `/payments/transaction` | Create payment transaction | ✅ Auto-assigns broker |

---

## 📈 FINANCIAL YEAR ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `POST` | `/financial-years` | Create financial year | ✅ Auto-assigns broker |
| `GET` | `/financial-years` | Get all financial years | ✅ Broker-filtered |
| `GET` | `/financial-years/{id}` | Get financial year by ID | ✅ Broker-filtered |
| `PUT` | `/financial-years/{id}` | Update financial year | ✅ Broker-filtered |
| `DELETE` | `/financial-years/{id}` | Delete financial year | ✅ Broker-filtered |
| `GET` | `/financial-years/current` | Get current financial year | ✅ Broker-filtered |

---

## 🔧 CACHE ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `GET` | `/cache/products/names` | Get cached product names | ✅ Broker-specific cache |
| `GET` | `/cache/products/names/distinct` | Get distinct product names | ✅ Broker-specific cache |
| `GET` | `/cache/products/basic-info` | Get basic product info | ✅ Broker-specific cache |
| `GET` | `/cache/users/names` | Get cached user firm names | ✅ Broker-specific cache |
| `GET` | `/cache/users/basic-info` | Get basic user info | ✅ Broker-specific cache |
| `DELETE` | `/cache/products/clear` | Clear product cache | ✅ Broker-specific |
| `DELETE` | `/cache/users/clear` | Clear user cache | ✅ Broker-specific |

---

## 📊 DASHBOARD & ANALYTICS ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `GET` | `/dashboard/summary` | Get dashboard summary | ✅ Broker-filtered |
| `GET` | `/dashboard/monthly-stats` | Get monthly statistics | ✅ Broker-filtered |
| `GET` | `/dashboard/top-products` | Get top products | ✅ Broker-filtered |
| `GET` | `/dashboard/top-merchants` | Get top merchants | ✅ Broker-filtered |
| `GET` | `/analytics/brokerage-trends` | Get brokerage trends | ✅ Broker-filtered |
| `GET` | `/analytics/product-performance` | Get product performance | ✅ Broker-filtered |

---

## 🔐 BROKER MANAGEMENT ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `POST` | `/brokers/register` | Register new broker | ❌ Public endpoint |
| `GET` | `/brokers/profile` | Get current broker profile | ✅ Current broker only |
| `PUT` | `/brokers/profile` | Update broker profile | ✅ Current broker only |
| `POST` | `/brokers/forgot-password` | Forgot password | ❌ Public endpoint |
| `POST` | `/brokers/verify-otp` | Verify OTP | ❌ Public endpoint |
| `GET` | `/brokers/total-brokerage` | Get total brokerage | ✅ Current broker only |

---

## 📱 MOBILE APP ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `GET` | `/mobile/dashboard` | Mobile dashboard | ✅ Broker-filtered |
| `GET` | `/mobile/quick-stats` | Quick statistics | ✅ Broker-filtered |
| `POST` | `/mobile/quick-transaction` | Quick transaction entry | ✅ Auto-assigns broker |
| `GET` | `/mobile/recent-transactions` | Recent transactions | ✅ Broker-filtered |

---

## 🔍 SEARCH & FILTER ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `GET` | `/search/global` | Global search | ✅ Broker-filtered |
| `GET` | `/search/users` | Search users | ✅ Broker-filtered |
| `GET` | `/search/products` | Search products | ✅ Broker-filtered |
| `GET` | `/search/transactions` | Search transactions | ✅ Broker-filtered |

### Query Parameters for Search:
- `q`: Search query
- `type`: Search type (users, products, transactions)
- `page`: Page number
- `size`: Page size

---

## 📄 REPORT ENDPOINTS

| Method | Endpoint | Description | Multi-Tenant |
|--------|----------|-------------|--------------|
| `GET` | `/reports/daily/{date}` | Daily report | ✅ Broker-filtered |
| `GET` | `/reports/monthly/{year}/{month}` | Monthly report | ✅ Broker-filtered |
| `GET` | `/reports/yearly/{year}` | Yearly report | ✅ Broker-filtered |
| `GET` | `/reports/custom` | Custom date range report | ✅ Broker-filtered |
| `GET` | `/reports/export/pdf` | Export PDF report | ✅ Broker-filtered |
| `GET` | `/reports/export/excel` | Export Excel report | ✅ Broker-filtered |

### Query Parameters for Custom Reports:
- `startDate`: Start date (YYYY-MM-DD)
- `endDate`: End date (YYYY-MM-DD)
- `type`: Report type (daily, summary, detailed)

---

## ⚡ QUICK REFERENCE

### Common HTTP Status Codes:
- `200`: Success
- `201`: Created
- `400`: Bad Request
- `401`: Unauthorized
- `403`: Forbidden (Access Denied)
- `404`: Not Found
- `409`: Conflict (Duplicate)
- `500`: Internal Server Error

### Common Headers:
```
Content-Type: application/json
Authorization: Basic <credentials>
Accept: application/json
```

### Date Format:
- All dates should be in `YYYY-MM-DD` format
- DateTime fields use ISO 8601 format: `YYYY-MM-DDTHH:mm:ss`

### Pagination Response Format:
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
