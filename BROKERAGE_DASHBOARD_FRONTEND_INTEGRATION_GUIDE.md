# Brokerage Dashboard - Frontend Integration Guide

## Overview

The Brokerage Dashboard is a comprehensive feature that allows brokers to manage and track brokerage payments from merchants. This document provides all the necessary information for the frontend team to create a React-based dashboard interface.

## Business Requirements

### Core Features
1. **Dashboard Overview** - Summary of all brokerage data with charts
2. **Merchant Management** - List and manage all merchants with their brokerage status
3. **Payment Status Tracking** - Update payment status (pending, partial, fully paid)
4. **Payment History** - View complete payment transaction history
5. **Brokerage Adjustment** - Modify brokerage amounts with reasons
6. **City-wise Analytics** - Detailed analytics by city with year-over-year comparison
7. **Automatic Calculation** - Calculate brokerage from existing transaction data

### Payment Status Types
- `PENDING` - Payment not yet made
- `PARTIAL_PAID` - Partial payment received
- `PAID` - Fully paid
- `OVERDUE` - Payment overdue
- `DUE_SOON` - Payment due soon

### Payment Methods
- `CASH` - Cash payment
- `BANK_TRANSFER` - Bank transfer
- `CHEQUE` - Cheque payment
- `UPI` - UPI payment
- `NEFT` - NEFT transfer
- `RTGS` - RTGS transfer
- `ONLINE` - Online payment
- `OTHER` - Other payment methods

## API Endpoints

### Base URL
```
/BrokerHub/api/brokerage-dashboard
```

**Note:** Make sure to include the `/BrokerHub` context path in all API calls.

---

## 1. Get Dashboard Overview

**Endpoint:** `GET /{brokerId}`

**Description:** Returns complete dashboard data including summary, merchants list, and chart data.

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/api/brokerage-dashboard/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Dashboard data retrieved successfully",
  "data": {
    "summary": {
      "totalBrokerageReceivable": 50000.00,
      "totalBrokerageReceived": 30000.00,
      "totalBrokeragePending": 20000.00,
      "totalMerchants": 25,
      "paidMerchants": 10,
      "pendingMerchants": 10,
      "partialPaidMerchants": 5
    },
    "merchants": [
      {
        "merchantId": 1,
        "firmName": "ABC Traders",
        "ownerName": "John Doe",
        "phoneNumber": "9876543210",
        "soldBags": 100,
        "boughtBags": 50,
        "totalBags": 150,
        "brokerageRate": 2.00,
        "calculatedBrokerage": 300.00,
        "actualBrokerage": 285.00,
        "paidAmount": 100.00,
        "pendingAmount": 185.00,
        "status": "PARTIAL_PAID",
        "lastPaymentDate": "2024-01-15",
        "dueDate": "2024-02-15",
        "paymentHistory": []
      }
    ],
    "chartData": [
      {
        "label": "Received",
        "value": 30000.00,
        "color": "#4CAF50"
      },
      {
        "label": "Pending",
        "value": 20000.00,
        "color": "#FF9800"
      }
    ]
  }
}
```

**Error Response (404):**
```json
{
  "success": false,
  "message": "Broker not found with id: 1",
  "data": null
}
```

---

## 2. Get Merchants List

**Endpoint:** `GET /{brokerId}/merchants`

**Description:** Returns detailed list of all merchants with their brokerage information.

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/api/brokerage-dashboard/1/merchants" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Merchants brokerage data retrieved successfully",
  "data": [
    {
      "merchantId": 1,
      "firmName": "ABC Traders",
      "ownerName": "John Doe",
      "phoneNumber": "9876543210",
      "soldBags": 100,
      "boughtBags": 50,
      "totalBags": 150,
      "brokerageRate": 2.00,
      "calculatedBrokerage": 300.00,
      "actualBrokerage": 285.00,
      "paidAmount": 100.00,
      "pendingAmount": 185.00,
      "status": "PARTIAL_PAID",
      "lastPaymentDate": "2024-01-15",
      "dueDate": "2024-02-15",
      "paymentHistory": [
        {
          "paymentId": 1,
          "amount": 100.00,
          "paymentDate": "2024-01-15",
          "paymentMethod": "UPI",
          "transactionReference": "TXN123456",
          "notes": "Partial payment"
        }
      ]
    }
  ]
}
```

---

## 3. Update Payment Status

**Endpoint:** `PUT /{brokerId}/payment-status`

**Description:** Updates the payment status for a merchant's brokerage.

**Request Body:**
```json
{
  "merchantId": 1,
  "status": "PARTIAL_PAID",
  "paidAmount": 100.00,
  "paymentDate": "2024-01-15",
  "paymentMethod": "UPI",
  "transactionReference": "TXN123456",
  "notes": "Partial payment received"
}
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/BrokerHub/api/brokerage-dashboard/1/payment-status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "status": "PARTIAL_PAID",
    "paidAmount": 100.00,
    "paymentDate": "2024-01-15",
    "paymentMethod": "UPI",
    "transactionReference": "TXN123456",
    "notes": "Partial payment received"
  }'
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Payment status updated successfully",
  "data": null
}
```

**Error Response (400):**
```json
{
  "success": false,
  "message": "Brokerage payment not found",
  "data": null
}
```

**Validation Error (400):**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "merchantId": "must not be null",
    "paidAmount": "must be greater than or equal to 0"
  }
}
```

---

## 4. Update Brokerage Amount

**Endpoint:** `PUT /{brokerId}/brokerage-amount`

**Description:** Allows broker to update the receivable brokerage amount for a merchant.

**Request Body:**
```json
{
  "merchantId": 1,
  "newBrokerageAmount": 100.00,
  "reason": "Rounded to nearest hundred for convenience"
}
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/BrokerHub/api/brokerage-dashboard/1/brokerage-amount" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "newBrokerageAmount": 100.00,
    "reason": "Rounded to nearest hundred"
  }'
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Brokerage amount updated successfully",
  "data": null
}
```

---

## 5. Get Payment History

**Endpoint:** `GET /{brokerId}/merchant/{merchantId}/history`

**Description:** Returns payment history for a specific merchant.

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/api/brokerage-dashboard/1/merchant/1/history" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Payment history retrieved successfully",
  "data": [
    {
      "paymentId": 1,
      "amount": 100.00,
      "paymentDate": "2024-01-15",
      "paymentMethod": "UPI",
      "transactionReference": "TXN123456",
      "notes": "Partial payment"
    },
    {
      "paymentId": 2,
      "amount": 50.00,
      "paymentDate": "2024-01-10",
      "paymentMethod": "CASH",
      "transactionReference": null,
      "notes": "Cash payment"
    }
  ]
}
```

---

## 6. Get Payment Methods

**Endpoint:** `GET /payment-methods`

**Description:** Returns list of available payment methods.

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/api/brokerage-dashboard/payment-methods" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Payment methods retrieved successfully",
  "data": [
    "CASH",
    "BANK_TRANSFER",
    "CHEQUE",
    "UPI",
    "NEFT",
    "RTGS",
    "ONLINE",
    "OTHER"
  ]
}
```

---

## 7. Calculate Brokerage

**Endpoint:** `POST /{brokerId}/calculate-brokerage`

**Description:** Triggers automatic calculation of brokerage for all merchants based on their trading activity.

**cURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/api/brokerage-dashboard/1/calculate-brokerage" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Brokerage calculation completed successfully",
  "data": null
}
```

**Error Response (500):**
```json
{
  "success": false,
  "message": "Current financial year not found for broker: 1",
  "data": null
}
```

---

## 8. Get City Analytics

**Endpoint:** `GET /{brokerId}/city/{city}/analytics`

**Description:** Returns detailed brokerage analytics for a specific city including year-over-year comparison.

**cURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/api/brokerage-dashboard/1/city/VIJAYAWADA/analytics" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "City analytics retrieved successfully",
  "data": {
    "city": "Mumbai",
    "totalMerchants": 25,
    "totalBagsSold": 1500,
    "totalBagsBought": 800,
    "totalBags": 2300,
    "totalActualBrokerage": 46000.00,
    "totalBrokeragePending": 15000.00,
    "totalBrokerageReceived": 31000.00,
    "totalPayments": 20,
    "totalPartialPayments": 8,
    "totalSuccessPayments": 12,
    "merchantsBusinessIncreased": 15,
    "merchantsBusinessDecreased": 5,
    "totalBrokerageChange": 5000.00
  }
}
```

---

## Frontend Implementation Guide

### 1. Dashboard Page Structure

```jsx
// Main Dashboard Component
const BrokerageDashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Fetch dashboard data on component mount
  useEffect(() => {
    fetchDashboardData();
  }, []);
  
  return (
    <div className="brokerage-dashboard">
      <DashboardSummary summary={dashboardData?.summary} />
      <BrokerageCharts chartData={dashboardData?.chartData} />
      <MerchantsTable merchants={dashboardData?.merchants} />
    </div>
  );
};
```

### 2. Key Components to Create

#### Dashboard Summary Cards
```jsx
const DashboardSummary = ({ summary }) => (
  <div className="summary-cards">
    <SummaryCard 
      title="Total Receivable" 
      value={`₹${summary?.totalBrokerageReceivable}`}
      color="blue"
    />
    <SummaryCard 
      title="Total Received" 
      value={`₹${summary?.totalBrokerageReceived}`}
      color="green"
    />
    <SummaryCard 
      title="Total Pending" 
      value={`₹${summary?.totalBrokeragePending}`}
      color="orange"
    />
    <SummaryCard 
      title="Total Merchants" 
      value={summary?.totalMerchants}
      color="purple"
    />
  </div>
);
```

#### Merchants Table with Actions
```jsx
const MerchantsTable = ({ merchants }) => {
  const [selectedMerchant, setSelectedMerchant] = useState(null);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  
  return (
    <div className="merchants-table">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Firm Name</TableHead>
            <TableHead>Owner</TableHead>
            <TableHead>Total Bags</TableHead>
            <TableHead>Brokerage Amount</TableHead>
            <TableHead>Paid Amount</TableHead>
            <TableHead>Pending Amount</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {merchants?.map(merchant => (
            <MerchantRow 
              key={merchant.merchantId} 
              merchant={merchant}
              onUpdatePayment={() => handleUpdatePayment(merchant)}
              onViewHistory={() => handleViewHistory(merchant)}
            />
          ))}
        </TableBody>
      </Table>
      
      {showPaymentModal && (
        <PaymentUpdateModal 
          merchant={selectedMerchant}
          onClose={() => setShowPaymentModal(false)}
          onUpdate={handlePaymentUpdate}
        />
      )}
    </div>
  );
};
```

#### Payment Update Modal
```jsx
const PaymentUpdateModal = ({ merchant, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({
    status: 'PARTIAL_PAID',
    paidAmount: '',
    paymentDate: new Date().toISOString().split('T')[0],
    paymentMethod: 'CASH',
    transactionReference: '',
    notes: ''
  });
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await updatePaymentStatus(merchant.merchantId, formData);
      onUpdate();
      onClose();
    } catch (error) {
      // Handle error
    }
  };
  
  return (
    <Modal>
      <form onSubmit={handleSubmit}>
        <Select 
          label="Payment Status"
          value={formData.status}
          onChange={(value) => setFormData({...formData, status: value})}
          options={[
            { value: 'PENDING', label: 'Pending' },
            { value: 'PARTIAL_PAID', label: 'Partial Paid' },
            { value: 'PAID', label: 'Fully Paid' }
          ]}
        />
        
        {formData.status !== 'PENDING' && (
          <>
            <Input 
              label="Paid Amount"
              type="number"
              value={formData.paidAmount}
              onChange={(e) => setFormData({...formData, paidAmount: e.target.value})}
              required
            />
            
            <Input 
              label="Payment Date"
              type="date"
              value={formData.paymentDate}
              onChange={(e) => setFormData({...formData, paymentDate: e.target.value})}
              required
            />
            
            <Select 
              label="Payment Method"
              value={formData.paymentMethod}
              onChange={(value) => setFormData({...formData, paymentMethod: value})}
              options={paymentMethods}
            />
          </>
        )}
        
        <Button type="submit">Update Payment</Button>
      </form>
    </Modal>
  );
};
```

#### City Analytics Component
```jsx
const CityAnalytics = () => {
  const [selectedCity, setSelectedCity] = useState('');
  const [analyticsData, setAnalyticsData] = useState(null);
  
  const fetchCityAnalytics = async (city) => {
    try {
      const response = await api.get(`/brokerage-dashboard/${brokerId}/city/${city}/analytics`);
      setAnalyticsData(response.data.data);
    } catch (error) {
      // Handle error
    }
  };
  
  return (
    <div className="city-analytics">
      <CitySelector 
        onCitySelect={(city) => {
          setSelectedCity(city);
          fetchCityAnalytics(city);
        }}
      />
      
      {analyticsData && (
        <div className="analytics-grid">
          <AnalyticsCard title="Total Merchants" value={analyticsData.totalMerchants} />
          <AnalyticsCard title="Total Bags" value={analyticsData.totalBags} />
          <AnalyticsCard title="Total Brokerage" value={`₹${analyticsData.totalActualBrokerage}`} />
          <AnalyticsCard title="Business Growth" value={analyticsData.totalBrokerageChange > 0 ? 'Increased' : 'Decreased'} />
        </div>
      )}
    </div>
  );
};
```

### 3. API Service Functions

```javascript
// api/brokerageService.js
const API_BASE = '/BrokerHub/api/brokerage-dashboard';

export const brokerageService = {
  getDashboard: (brokerId) => 
    api.get(`${API_BASE}/${brokerId}`),
    
  getMerchants: (brokerId) => 
    api.get(`${API_BASE}/${brokerId}/merchants`),
    
  updatePaymentStatus: (brokerId, data) => 
    api.put(`${API_BASE}/${brokerId}/payment-status`, data),
    
  updateBrokerageAmount: (brokerId, data) => 
    api.put(`${API_BASE}/${brokerId}/brokerage-amount`, data),
    
  getPaymentHistory: (brokerId, merchantId) => 
    api.get(`${API_BASE}/${brokerId}/merchant/${merchantId}/history`),
    
  getPaymentMethods: () => 
    api.get(`${API_BASE}/payment-methods`),
    
  calculateBrokerage: (brokerId) => 
    api.post(`${API_BASE}/${brokerId}/calculate-brokerage`),
    
  getCityAnalytics: (brokerId, city) => 
    api.get(`${API_BASE}/${brokerId}/city/${city}/analytics`)
};
```

### 4. State Management

```javascript
// hooks/useBrokerageDashboard.js
export const useBrokerageDashboard = (brokerId) => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const fetchDashboard = async () => {
    try {
      setLoading(true);
      const response = await brokerageService.getDashboard(brokerId);
      setDashboardData(response.data.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  
  const updatePayment = async (merchantId, paymentData) => {
    try {
      await brokerageService.updatePaymentStatus(brokerId, {
        merchantId,
        ...paymentData
      });
      await fetchDashboard(); // Refresh data
    } catch (err) {
      throw new Error(err.response?.data?.message || 'Failed to update payment');
    }
  };
  
  return {
    dashboardData,
    loading,
    error,
    fetchDashboard,
    updatePayment
  };
};
```

### 5. Error Handling

```javascript
// utils/errorHandler.js
export const handleApiError = (error) => {
  if (error.response?.data?.success === false) {
    return error.response.data.message;
  }
  return 'An unexpected error occurred';
};

// Usage in components
try {
  await updatePayment(data);
  showSuccessMessage('Payment updated successfully');
} catch (error) {
  showErrorMessage(handleApiError(error));
}
```

## Business Logic Notes

### Brokerage Calculation Formula
```
Total Bags = Sold Bags + Bought Bags
Gross Brokerage = Total Bags × Brokerage Rate
Discount = Gross Brokerage × 0.10 (10%)
TDS = Gross Brokerage × 0.05 (5%)
Net Brokerage = Gross Brokerage - Discount - TDS
Pending Amount = Net Brokerage - Paid Amount
```

### Status Updates
- When updating to `PARTIAL_PAID`, `paidAmount` is required
- When updating to `PAID`, system automatically calculates remaining amount
- `OVERDUE` status is automatically set based on due date
- Payment history is automatically maintained

### City Analytics
- Current year data vs previous financial year comparison
- Business growth indicators (increased/decreased merchants)
- Comprehensive metrics for business planning

This documentation provides everything needed to create a comprehensive React-based brokerage dashboard interface.