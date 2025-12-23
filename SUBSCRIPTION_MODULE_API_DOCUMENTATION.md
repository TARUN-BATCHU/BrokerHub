# Subscription Module API Documentation

## Overview
This document provides comprehensive API documentation for the BrokerHub Subscription Module implementation.

## Base URL
```
http://localhost:8080/api
```

## Authentication
All endpoints except `/plans` require JWT authentication via `Authorization: Bearer <token>` header.

## API Endpoints

### 1. Get All Active Plans
**Endpoint:** `GET /api/plans`  
**Authentication:** Not required  
**Description:** Retrieves all active subscription plans

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "planCode": "BASIC",
    "planName": "Basic Plan",
    "price": 499.00,
    "billingCycle": "MONTHLY",
    "features": {
      "projects": 5,
      "users": 3,
      "storage_gb": 20
    }
  },
  {
    "id": 2,
    "planCode": "PRO",
    "planName": "Pro Plan",
    "price": 999.00,
    "billingCycle": "MONTHLY",
    "features": {
      "projects": 10,
      "users": 5,
      "storage_gb": 50
    }
  }
]
```

### 2. Get Current Subscription
**Endpoint:** `GET /api/subscriptions/current`  
**Authentication:** Required  
**Description:** Gets the current user's active subscription

**Success Response (200 OK):**
```json
{
  "plan": "PRO",
  "status": "ACTIVE",
  "startDate": "2025-09-01",
  "endDate": "2025-10-01",
  "features": {
    "projects": 10,
    "users": 5,
    "storage_gb": 50
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "errorCode": "NO_SUBSCRIPTION",
  "message": "No active subscription found"
}
```

### 3. Request Subscription
**Endpoint:** `POST /api/subscriptions/request`  
**Authentication:** Required  
**Description:** Submits a subscription request (does not activate)

**Request Body:**
```json
{
  "planId": 2
}
```

**Response (200 OK):**
```json
{
  "message": "Subscription request submitted. Please complete payment.",
  "success": true
}
```

### 4. Admin - Activate Subscription
**Endpoint:** `POST /api/admin/subscriptions/activate`  
**Authentication:** Required (ADMIN role)  
**Description:** Activates a subscription for a user

**Request Body:**
```json
{
  "userId": 101,
  "planId": 2,
  "startDate": "2025-09-01",
  "endDate": "2025-10-01",
  "charges": [
    {
      "type": "SUBSCRIPTION_CHARGE",
      "amount": 500.00,
      "description": "Monthly subscription fee"
    },
    {
      "type": "SUPPORT_CHARGE",
      "amount": 100.00,
      "description": "Premium support"
    },
    {
      "type": "TAX",
      "amount": 108.00,
      "description": "GST 18%"
    }
  ]
}
```

**Success Response (200 OK):**
```json
{
  "message": "Subscription activated successfully",
  "success": true
}
```

**Error Response (400 Bad Request):**
```json
{
  "errorCode": "ACTIVE_SUBSCRIPTION_EXISTS",
  "message": "User already has an active subscription"
}
```

### 5. Admin - Expire Subscription
**Endpoint:** `POST /api/admin/subscriptions/expire?userId=101`  
**Authentication:** Required (ADMIN role)  
**Description:** Manually expires a user's subscription

**Response (200 OK):**
```json
{
  "message": "Subscription expired",
  "success": true
}
```

## Error Codes

| Code | Description |
|------|-------------|
| `NO_SUBSCRIPTION` | User has no subscription |
| `SUBSCRIPTION_EXPIRED` | Subscription has expired |
| `SUBSCRIPTION_SUSPENDED` | Subscription is suspended by admin |
| `ACTIVE_SUBSCRIPTION_EXISTS` | User already has active subscription |
| `SUBSCRIPTION_INACTIVE` | Subscription is not active (login blocked) |
| `PLAN_NOT_FOUND` | Subscription plan not found |
| `USER_NOT_FOUND` | User not found |
| `PLAN_INACTIVE` | Selected plan is not active |

## Login Flow Integration

The subscription validation is integrated into the login flow:

1. User provides credentials
2. System authenticates user
3. System checks subscription status:
   - If no subscription → deny login
   - If status != ACTIVE → deny login  
   - If end_date < today → mark EXPIRED and deny login
4. If subscription is ACTIVE → allow login

**Login Error Response (403 Forbidden):**
```json
{
  "errorCode": "SUBSCRIPTION_INACTIVE",
  "message": "Your subscription is not active"
}
```

## Database Schema

### subscription_plans
- `id` (PK)
- `plan_code` (UNIQUE: FREE, BASIC, PRO, PAY_AS_YOU_GO)
- `plan_name`
- `base_price`
- `billing_cycle` (MONTHLY/YEARLY)
- `currency`
- `features_json` (JSON with limits and features)
- `is_active`
- `created_at`
- `updated_at`

### user_subscriptions
- `id` (PK)
- `user_id` (FK to user)
- `plan_id` (FK to subscription_plans)
- `status` (ACTIVE/EXPIRED/SUSPENDED)
- `start_date`
- `end_date`
- `created_at`
- `updated_at`

### subscription_history
- `id` (PK)
- `subscription_id` (FK to user_subscriptions)
- `old_plan_id` (FK to subscription_plans)
- `new_plan_id` (FK to subscription_plans)
- `changed_at`
- `changed_by` (ADMIN/SYSTEM)
- `reason`

### subscription_charges
- `id` (PK)
- `subscription_id` (FK to user_subscriptions)
- `charge_type` (SUBSCRIPTION_CHARGE, DATABASE_CHARGE, etc.)
- `amount`
- `description`
- `created_at`

## Testing

### Test Scenarios

1. **Get Plans** - Verify all active plans are returned
2. **No Subscription** - User with no subscription gets 404
3. **Active Subscription** - User with active subscription gets details
4. **Expired Subscription** - Auto-expire and return 404
5. **Request Subscription** - Valid plan ID creates request
6. **Activate Subscription** - Admin can activate with charges
7. **Duplicate Activation** - Prevent multiple active subscriptions
8. **Login Enforcement** - Block login for inactive subscriptions
9. **Admin Expire** - Admin can manually expire subscriptions

### Sample cURL Commands

```bash
# Get all plans (public)
curl -X GET http://localhost:8080/api/plans

# Get current subscription (requires auth)
curl -X GET http://localhost:8080/api/subscriptions/current \
  -H "Authorization: Bearer <token>"

# Request subscription (requires auth)
curl -X POST http://localhost:8080/api/subscriptions/request \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"planId": 2}'

# Admin activate subscription (requires admin auth)
curl -X POST http://localhost:8080/api/admin/subscriptions/activate \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 101,
    "planId": 2,
    "startDate": "2025-09-01",
    "endDate": "2025-10-01",
    "charges": [
      {"type": "SUBSCRIPTION_CHARGE", "amount": 500.00}
    ]
  }'
```

## Implementation Notes

- Subscription validation is enforced at login time
- Admin users bypass subscription checks
- Subscriptions are automatically expired when end_date passes
- All subscription changes are logged in history table
- Multi-tenant isolation is maintained through broker relationships
- Feature limits are stored as JSON for flexibility
- Charges support multiple types for detailed billing