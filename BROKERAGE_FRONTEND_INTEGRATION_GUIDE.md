# Brokerage Feature - Frontend Integration Guide

## 📋 **Table of Contents**
1. [Feature Overview](#feature-overview)
2. [Authentication & Security](#authentication--security)
3. [API Reference](#api-reference)
4. [UI Design Guidelines](#ui-design-guidelines)
5. [Async Operations Handling](#async-operations-handling)
6. [Error Handling](#error-handling)
7. [Sample React Components](#sample-react-components)

---

## 🎯 **Feature Overview**

### **What is the Brokerage Feature?**
The Brokerage Feature allows brokers to:
- **Calculate total brokerage** earned in a financial year
- **Generate comprehensive reports** (summaries, user-wise, city-wise)
- **Export documents** in HTML and Excel formats
- **Process bulk operations** for multiple users/cities
- **Track document generation** status in real-time

### **Business Context**
- Brokers facilitate transactions between sellers (MILLER) and buyers (TRADER)
- At year-end, brokers generate brokerage bills for users
- Users pay brokerage fees based on their transaction volume
- System provides detailed breakdowns and professional documents

### **Key Benefits**
- **Automated calculations** - No manual brokerage computation
- **Professional documents** - Ready-to-send bills and reports
- **Bulk processing** - Handle hundreds of users efficiently
- **Real-time tracking** - Monitor document generation progress
- **Multi-format exports** - HTML and Excel options

---

## 🔐 **Authentication & Security**

### **JWT Token Requirement**
All API calls require JWT authentication token in the header:
```javascript
const headers = {
  'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
  'Content-Type': 'application/json'
};
```

### **Multi-Tenant Security**
- **Broker ID is automatically extracted** from JWT token
- **No need to pass broker ID** in API calls
- **Each broker sees only their own data**
- **Cannot access other brokers' information**

---

## 📡 **API Reference**

### **Base URL**
```
https://your-domain.com/BrokerHub
```

### **1. Core Brokerage APIs**

#### **1.1 Get Total Brokerage**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/total/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "Total brokerage retrieved successfully",
  "data": 150000.50
}
```

**UI Usage:** Display total earnings on dashboard

---

#### **1.2 Get Brokerage Summary**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/summary/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "Brokerage summary retrieved successfully",
  "data": {
    "totalBrokerageEarned": 150000.50,
    "totalBrokerageFromSellers": 80000.25,
    "totalBrokerageFromBuyers": 69999.25,
    "cityWiseBrokerage": [
      {
        "city": "Guntur",
        "totalBrokerage": 75000.00
      },
      {
        "city": "Vijayawada",
        "totalBrokerage": 45000.50
      }
    ],
    "productWiseBrokerage": [
      {
        "productName": "Rice",
        "totalBrokerage": 90000.00
      },
      {
        "productName": "Wheat",
        "totalBrokerage": 35000.25
      }
    ]
  }
}
```

**UI Usage:** Create dashboard with charts and breakdowns

---

#### **1.3 Get User Total Brokerage**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/user/123/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "User total brokerage retrieved successfully",
  "data": 12500.75
}
```

**UI Usage:** Show individual user's payable amount

---

#### **1.4 Get City Total Brokerage**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/city/Guntur/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "City total brokerage retrieved successfully",
  "data": 75000.00
}
```

**UI Usage:** City-wise analysis and filtering

---

#### **1.5 Get User Detailed Brokerage**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/user-detail/123/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "User brokerage detail retrieved successfully",
  "data": {
    "userBasicInfo": {
      "firmName": "ABC Traders",
      "ownerName": "John Doe",
      "city": "Guntur"
    },
    "brokerageSummary": {
      "totalBagsSold": 500,
      "totalBagsBought": 300,
      "productsBought": [
        {
          "productName": "Rice",
          "totalBags": 200
        }
      ],
      "productsSold": [
        {
          "productName": "Wheat",
          "totalBags": 300
        }
      ],
      "citiesSoldTo": [
        {
          "city": "Vijayawada",
          "totalBags": 250
        }
      ],
      "citiesBoughtFrom": [
        {
          "city": "Hyderabad",
          "totalBags": 150
        }
      ],
      "totalBrokeragePayable": 12500.75,
      "totalAmountEarned": 500000,
      "totalAmountPaid": 300000
    },
    "transactionDetails": [
      {
        "transactionNumber": 517,
        "transactionDate": "2024-03-15",
        "counterPartyFirmName": "XYZ Mills",
        "productName": "Rice",
        "productCost": 2500,
        "quantity": 10,
        "brokerage": 250.00
      }
    ]
  }
}
```

**UI Usage:** Detailed user profile with transaction history

---

### **2. Document Generation APIs**

#### **2.1 Generate HTML Bill**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/bill/123/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output "bill_123.html"
```

**Response:** HTML file download

**UI Usage:** Download button with file save dialog

---

#### **2.2 Generate User Excel Bill**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/excel/user/123/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output "bill_123.xlsx"
```

**Response:** Excel file download

**UI Usage:** Excel export button

---

#### **2.3 Generate Summary Excel**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/excel/summary/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output "summary_2024.xlsx"
```

**Response:** Excel file download

**UI Usage:** Export summary report button

---

#### **2.4 Generate City Excel Report**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Brokerage/excel/city/Guntur/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output "city_guntur_2024.xlsx"
```

**Response:** Excel file download

**UI Usage:** City-wise export functionality

---

### **3. Bulk Processing APIs (Async)**

#### **3.1 Bulk HTML Bills for City**
```bash
curl -X POST "https://your-domain.com/BrokerHub/Brokerage/bulk-bills/city/Guntur/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "Bulk bill generation started for city: Guntur",
  "data": "Request processed successfully"
}
```

**UI Usage:** Trigger bulk processing with progress tracking

---

#### **3.2 Bulk HTML Bills for Users**
```bash
curl -X POST "https://your-domain.com/BrokerHub/Brokerage/bulk-bills/users/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '[123, 456, 789]'
```

**Response:**
```json
{
  "status": "success",
  "message": "Bulk bill generation started for 3 users",
  "data": "Request processed successfully"
}
```

**UI Usage:** Multi-select users and bulk process

---

#### **3.3 Bulk Excel for City**
```bash
curl -X POST "https://your-domain.com/BrokerHub/Brokerage/bulk-excel/city/Guntur/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "Bulk Excel generation started for city: Guntur",
  "data": "Request processed successfully"
}
```

---

#### **3.4 Bulk Excel for Users**
```bash
curl -X POST "https://your-domain.com/BrokerHub/Brokerage/bulk-excel/users/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '[123, 456, 789]'
```

**Response:**
```json
{
  "status": "success",
  "message": "Bulk Excel generation started for 3 users",
  "data": "Request processed successfully"
}
```

---

### **4. Document Status APIs**

#### **4.1 Get All Document Status**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Documents/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "Document status retrieved successfully",
  "data": [
    {
      "documentId": 1,
      "documentType": "BULK_CITY_BILLS",
      "status": "COMPLETED",
      "city": "Guntur",
      "createdAt": "2024-03-15T10:30:00",
      "completedAt": "2024-03-15T10:35:00",
      "filePath": "bills/1/2024/Guntur/"
    },
    {
      "documentId": 2,
      "documentType": "BULK_USER_EXCEL",
      "status": "GENERATING",
      "userIds": "123,456,789",
      "createdAt": "2024-03-15T11:00:00",
      "completedAt": null,
      "filePath": null
    }
  ]
}
```

**UI Usage:** Real-time status monitoring dashboard

---

#### **4.2 Get Status by Document Type**
```bash
curl -X GET "https://your-domain.com/BrokerHub/Documents/status/BULK_CITY_BILLS" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:** Same structure as above, filtered by document type

---

### **5. Cache Management APIs**

#### **5.1 Clear All Brokerage Cache**
```bash
curl -X DELETE "https://your-domain.com/BrokerHub/Cache/brokerage" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "status": "success",
  "message": "All brokerage cache cleared",
  "data": "Cache cleared successfully"
}
```

**UI Usage:** Admin cache management panel

---

## 🎨 **UI Design Guidelines**

### **1. Dashboard Layout**

#### **Main Brokerage Dashboard**
```
┌─────────────────────────────────────────────────────────────┐
│                    BROKERAGE DASHBOARD                      │
├─────────────────────────────────────────────────────────────┤
│  Financial Year: [2024 ▼]                    [Refresh]     │
├─────────────────────────────────────────────────────────────┤
│  📊 OVERVIEW                                               │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │Total Earned │ │From Sellers │ │From Buyers  │          │
│  │₹1,50,000    │ │₹80,000      │ │₹70,000      │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
├─────────────────────────────────────────────────────────────┤
│  📈 BREAKDOWNS                                             │
│  ┌─────────────────────┐ ┌─────────────────────┐          │
│  │   City-wise         │ │   Product-wise      │          │
│  │ Guntur: ₹75,000     │ │ Rice: ₹90,000       │          │
│  │ Vijayawada: ₹45,000 │ │ Wheat: ₹35,000      │          │
│  │ [View All]          │ │ [View All]          │          │
│  └─────────────────────┘ └─────────────────────┘          │
├─────────────────────────────────────────────────────────────┤
│  🔧 ACTIONS                                                │
│  [📄 Generate Summary] [📊 Export Excel] [👥 User Reports] │
└─────────────────────────────────────────────────────────────┘
```

#### **Key Components:**
- **Financial Year Selector** - Dropdown with available years
- **Overview Cards** - Total, sellers, buyers amounts
- **Breakdown Charts** - City-wise and product-wise pie/bar charts
- **Action Buttons** - Quick access to common operations

---

### **2. User Management Interface**

#### **User List with Brokerage Info**
```
┌─────────────────────────────────────────────────────────────┐
│                      USER BROKERAGE                        │
├─────────────────────────────────────────────────────────────┤
│  Search: [____________] City: [All ▼] [🔍 Search]          │
├─────────────────────────────────────────────────────────────┤
│  ☑ Firm Name        │ City      │ Brokerage │ Actions      │
│  ☑ ABC Traders      │ Guntur    │ ₹12,500   │ [👁][📄][📊] │
│  ☑ XYZ Mills        │ Vijayawada│ ₹8,750    │ [👁][📄][📊] │
│  ☑ PQR Exports      │ Guntur    │ ₹15,200   │ [👁][📄][📊] │
├─────────────────────────────────────────────────────────────┤
│  Selected: 2 users                                         │
│  [📄 Bulk Bills] [📊 Bulk Excel] [🗑 Clear Selection]      │
└─────────────────────────────────────────────────────────────┘
```

#### **Key Features:**
- **Multi-select checkboxes** for bulk operations
- **Search and filter** functionality
- **Individual actions** - View details, generate bill, export
- **Bulk actions** - Process multiple users at once

---

### **3. User Detail Modal**

#### **Detailed User View**
```
┌─────────────────────────────────────────────────────────────┐
│  👤 ABC Traders - John Doe                          [✕]    │
├─────────────────────────────────────────────────────────────┤
│  📍 Location: Guntur                                       │
│  💰 Total Brokerage: ₹12,500.75                           │
├─────────────────────────────────────────────────────────────┤
│  📊 SUMMARY                                                │
│  Bags Sold: 500    │ Bags Bought: 300                     │
│  Amount Earned: ₹5,00,000 │ Amount Paid: ₹3,00,000        │
├─────────────────────────────────────────────────────────────┤
│  📦 PRODUCTS                                               │
│  Bought: Rice (200), Wheat (100)                          │
│  Sold: Rice (300), Wheat (200)                            │
├─────────────────────────────────────────────────────────────┤
│  🏙 CITIES                                                  │
│  Sold To: Vijayawada (250), Hyderabad (150)               │
│  Bought From: Chennai (200), Bangalore (100)              │
├─────────────────────────────────────────────────────────────┤
│  📋 TRANSACTIONS                                           │
│  [Table with transaction history - paginated]             │
├─────────────────────────────────────────────────────────────┤
│  [📄 Generate Bill] [📊 Export Excel] [🔄 Refresh]        │
└─────────────────────────────────────────────────────────────┘
```

---

### **4. Bulk Operations Interface**

#### **Bulk Processing Panel**
```
┌─────────────────────────────────────────────────────────────┐
│                    BULK OPERATIONS                         │
├─────────────────────────────────────────────────────────────┤
│  📄 HTML BILLS                                             │
│  ┌─────────────────────┐ ┌─────────────────────┐          │
│  │   By City           │ │   By Users          │          │
│  │ City: [Guntur ▼]    │ │ Selected: 5 users   │          │
│  │ [🚀 Generate]       │ │ [🚀 Generate]       │          │
│  └─────────────────────┘ └─────────────────────┘          │
├─────────────────────────────────────────────────────────────┤
│  📊 EXCEL REPORTS                                          │
│  ┌─────────────────────┐ ┌─────────────────────┐          │
│  │   By City           │ │   By Users          │          │
│  │ City: [Guntur ▼]    │ │ Selected: 5 users   │          │
│  │ [🚀 Generate]       │ │ [🚀 Generate]       │          │
│  └─────────────────────┘ └─────────────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

---

## ⚡ **Async Operations Handling**

### **1. Understanding Async Operations**

#### **What are Async Operations?**
- **Bulk bill generation** for multiple users
- **Bulk Excel generation** for cities/users
- **Background processing** to avoid UI blocking
- **Long-running tasks** that take minutes to complete

#### **Why Async?**
- **Non-blocking UI** - Users can continue working
- **Better performance** - Server handles multiple requests
- **Progress tracking** - Real-time status updates
- **Error handling** - Individual failure tracking

---

### **2. Async Flow Implementation**

#### **Step 1: Initiate Async Operation**
```javascript
const startBulkGeneration = async (city, financialYear) => {
  try {
    // Show loading state
    setIsGenerating(true);
    setGenerationStatus('Starting bulk generation...');
    
    // Start async operation
    const response = await fetch(`/BrokerHub/Brokerage/bulk-bills/city/${city}/${financialYear}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.status === 'success') {
      // Start polling for status
      startStatusPolling();
      setGenerationStatus('Generation started. Tracking progress...');
    }
  } catch (error) {
    setGenerationStatus('Failed to start generation');
    setIsGenerating(false);
  }
};
```

#### **Step 2: Poll for Status Updates**
```javascript
const startStatusPolling = () => {
  const pollInterval = setInterval(async () => {
    try {
      const response = await fetch('/BrokerHub/Documents/status', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      const result = await response.json();
      const latestDoc = result.data[0]; // Most recent document
      
      if (latestDoc.status === 'COMPLETED') {
        setGenerationStatus('Generation completed successfully!');
        setIsGenerating(false);
        clearInterval(pollInterval);
        
        // Show download options
        setDownloadReady(true);
        setDownloadPath(latestDoc.filePath);
        
      } else if (latestDoc.status === 'FAILED') {
        setGenerationStatus('Generation failed. Please try again.');
        setIsGenerating(false);
        clearInterval(pollInterval);
        
      } else {
        // Still generating
        const elapsed = Date.now() - new Date(latestDoc.createdAt).getTime();
        const minutes = Math.floor(elapsed / 60000);
        setGenerationStatus(`Generating... (${minutes} minutes elapsed)`);
      }
      
    } catch (error) {
      console.error('Status polling error:', error);
    }
  }, 5000); // Poll every 5 seconds
  
  // Store interval ID for cleanup
  setPollingInterval(pollInterval);
};
```

#### **Step 3: Handle Completion**
```javascript
const handleDownload = () => {
  // For individual files, direct download
  window.open(`/download/${downloadPath}`, '_blank');
  
  // For bulk operations, show file list
  showBulkDownloadModal();
};

const showBulkDownloadModal = () => {
  // Modal with list of generated files
  // Each file has download link
  // Option to download all as ZIP
};
```

---

### **3. UI Components for Async Operations**

#### **Progress Indicator Component**
```jsx
const AsyncProgressIndicator = ({ 
  isGenerating, 
  status, 
  onCancel, 
  downloadReady, 
  onDownload 
}) => {
  return (
    <div className="async-progress-panel">
      {isGenerating && (
        <div className="progress-section">
          <div className="spinner"></div>
          <div className="status-text">{status}</div>
          <button onClick={onCancel} className="cancel-btn">
            Cancel Operation
          </button>
        </div>
      )}
      
      {downloadReady && (
        <div className="download-section">
          <div className="success-icon">✅</div>
          <div className="success-text">Generation completed!</div>
          <button onClick={onDownload} className="download-btn">
            📥 Download Files
          </button>
        </div>
      )}
    </div>
  );
};
```

#### **Status Dashboard Component**
```jsx
const DocumentStatusDashboard = () => {
  const [documents, setDocuments] = useState([]);
  
  useEffect(() => {
    fetchDocumentStatus();
    const interval = setInterval(fetchDocumentStatus, 10000);
    return () => clearInterval(interval);
  }, []);
  
  const fetchDocumentStatus = async () => {
    const response = await fetch('/BrokerHub/Documents/status', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const result = await response.json();
    setDocuments(result.data);
  };
  
  return (
    <div className="status-dashboard">
      <h3>📋 Document Generation Status</h3>
      <div className="status-list">
        {documents.map(doc => (
          <div key={doc.documentId} className={`status-item ${doc.status.toLowerCase()}`}>
            <div className="doc-info">
              <span className="doc-type">{doc.documentType}</span>
              <span className="doc-target">
                {doc.city || `${doc.userIds?.split(',').length} users`}
              </span>
            </div>
            <div className="doc-status">
              <StatusBadge status={doc.status} />
              <span className="doc-time">
                {formatTime(doc.createdAt)}
              </span>
            </div>
            {doc.status === 'COMPLETED' && (
              <button className="download-btn">📥</button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};
```

---

### **4. Error Handling for Async Operations**

#### **Error States to Handle**
```javascript
const AsyncErrorHandler = {
  // Network errors
  NETWORK_ERROR: 'Unable to connect to server',
  
  // Server errors
  SERVER_ERROR: 'Server encountered an error',
  
  // Generation errors
  GENERATION_FAILED: 'Document generation failed',
  
  // Timeout errors
  TIMEOUT_ERROR: 'Operation timed out',
  
  // Permission errors
  PERMISSION_ERROR: 'Insufficient permissions'
};

const handleAsyncError = (error, operation) => {
  console.error(`Async operation failed: ${operation}`, error);
  
  // Show user-friendly error message
  showErrorNotification({
    title: 'Operation Failed',
    message: AsyncErrorHandler[error.type] || 'An unexpected error occurred',
    action: 'Retry',
    onAction: () => retryOperation(operation)
  });
  
  // Log for debugging
  logError({
    operation,
    error: error.message,
    timestamp: new Date().toISOString(),
    userId: getCurrentUserId()
  });
};
```

---

### **5. Best Practices for Async UI**

#### **User Experience Guidelines**
1. **Clear Communication**
   - Show what's happening
   - Estimate completion time
   - Provide cancel option

2. **Progress Feedback**
   - Visual progress indicators
   - Status text updates
   - Time elapsed display

3. **Error Recovery**
   - Clear error messages
   - Retry mechanisms
   - Alternative options

4. **Performance**
   - Efficient polling intervals
   - Cleanup on unmount
   - Memory leak prevention

#### **Implementation Checklist**
- [ ] Loading states for all async operations
- [ ] Progress indicators with meaningful messages
- [ ] Status polling with appropriate intervals
- [ ] Error handling with user-friendly messages
- [ ] Cancel functionality for long operations
- [ ] Download management for completed files
- [ ] Cleanup of intervals and subscriptions
- [ ] Responsive design for mobile devices

---

## 🚨 **Error Handling**

### **Common Error Scenarios**

#### **1. Authentication Errors**
```javascript
const handleAuthError = (response) => {
  if (response.status === 401) {
    // Token expired
    localStorage.removeItem('jwtToken');
    redirectToLogin();
    showNotification('Session expired. Please login again.');
  }
};
```

#### **2. Validation Errors**
```javascript
const handleValidationError = (error) => {
  if (error.status === 400) {
    showFieldErrors(error.data.validationErrors);
  }
};
```

#### **3. Server Errors**
```javascript
const handleServerError = (error) => {
  if (error.status >= 500) {
    showNotification('Server error. Please try again later.');
    logError(error);
  }
};
```

---

## 🔧 **Sample React Components**

### **1. Main Brokerage Dashboard**
```jsx
import React, { useState, useEffect } from 'react';
import { BrokerageAPI } from '../services/brokerageAPI';

const BrokerageDashboard = () => {
  const [financialYear, setFinancialYear] = useState('1');
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadBrokerageSummary();
  }, [financialYear]);

  const loadBrokerageSummary = async () => {
    try {
      setLoading(true);
      const data = await BrokerageAPI.getSummary(financialYear);
      setSummary(data);
    } catch (error) {
      console.error('Failed to load summary:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div className="brokerage-dashboard">
      <div className="dashboard-header">
        <h1>Brokerage Dashboard</h1>
        <select 
          value={financialYear} 
          onChange={(e) => setFinancialYear(e.target.value)}
        >
          <option value="1">2024-25</option>
          <option value="2">2023-24</option>
        </select>
      </div>

      <div className="overview-cards">
        <div className="card">
          <h3>Total Earned</h3>
          <div className="amount">₹{summary?.totalBrokerageEarned}</div>
        </div>
        <div className="card">
          <h3>From Sellers</h3>
          <div className="amount">₹{summary?.totalBrokerageFromSellers}</div>
        </div>
        <div className="card">
          <h3>From Buyers</h3>
          <div className="amount">₹{summary?.totalBrokerageFromBuyers}</div>
        </div>
      </div>

      <div className="breakdown-section">
        <div className="city-breakdown">
          <h3>City-wise Breakdown</h3>
          {summary?.cityWiseBrokerage?.map(city => (
            <div key={city.city} className="breakdown-item">
              <span>{city.city}</span>
              <span>₹{city.totalBrokerage}</span>
            </div>
          ))}
        </div>

        <div className="product-breakdown">
          <h3>Product-wise Breakdown</h3>
          {summary?.productWiseBrokerage?.map(product => (
            <div key={product.productName} className="breakdown-item">
              <span>{product.productName}</span>
              <span>₹{product.totalBrokerage}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="actions">
        <button onClick={() => exportSummaryExcel()}>
          📊 Export Summary
        </button>
        <button onClick={() => openUserManagement()}>
          👥 Manage Users
        </button>
      </div>
    </div>
  );
};
```

### **2. Async Bulk Operations Component**
```jsx
import React, { useState, useEffect } from 'react';

const BulkOperations = () => {
  const [selectedCity, setSelectedCity] = useState('');
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [isGenerating, setIsGenerating] = useState(false);
  const [generationStatus, setGenerationStatus] = useState('');
  const [pollingInterval, setPollingInterval] = useState(null);

  const startBulkGeneration = async (type, target) => {
    try {
      setIsGenerating(true);
      setGenerationStatus('Starting generation...');

      let endpoint;
      if (type === 'city-bills') {
        endpoint = `/BrokerHub/Brokerage/bulk-bills/city/${target}/1`;
      } else if (type === 'user-bills') {
        endpoint = `/BrokerHub/Brokerage/bulk-bills/users/1`;
      }

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
          'Content-Type': 'application/json'
        },
        body: type === 'user-bills' ? JSON.stringify(target) : undefined
      });

      const result = await response.json();
      
      if (result.status === 'success') {
        startStatusPolling();
      }
    } catch (error) {
      setGenerationStatus('Failed to start generation');
      setIsGenerating(false);
    }
  };

  const startStatusPolling = () => {
    const interval = setInterval(async () => {
      try {
        const response = await fetch('/BrokerHub/Documents/status', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
          }
        });
        
        const result = await response.json();
        const latestDoc = result.data[0];
        
        if (latestDoc.status === 'COMPLETED') {
          setGenerationStatus('Generation completed!');
          setIsGenerating(false);
          clearInterval(interval);
        } else if (latestDoc.status === 'FAILED') {
          setGenerationStatus('Generation failed');
          setIsGenerating(false);
          clearInterval(interval);
        } else {
          setGenerationStatus('Generating documents...');
        }
      } catch (error) {
        console.error('Polling error:', error);
      }
    }, 5000);

    setPollingInterval(interval);
  };

  useEffect(() => {
    return () => {
      if (pollingInterval) {
        clearInterval(pollingInterval);
      }
    };
  }, [pollingInterval]);

  return (
    <div className="bulk-operations">
      <h2>Bulk Operations</h2>
      
      {isGenerating && (
        <div className="progress-panel">
          <div className="spinner"></div>
          <div>{generationStatus}</div>
        </div>
      )}

      <div className="operation-section">
        <h3>Generate Bills by City</h3>
        <select 
          value={selectedCity} 
          onChange={(e) => setSelectedCity(e.target.value)}
          disabled={isGenerating}
        >
          <option value="">Select City</option>
          <option value="Guntur">Guntur</option>
          <option value="Vijayawada">Vijayawada</option>
        </select>
        <button 
          onClick={() => startBulkGeneration('city-bills', selectedCity)}
          disabled={!selectedCity || isGenerating}
        >
          Generate City Bills
        </button>
      </div>

      <div className="operation-section">
        <h3>Generate Bills for Selected Users</h3>
        <div>Selected: {selectedUsers.length} users</div>
        <button 
          onClick={() => startBulkGeneration('user-bills', selectedUsers)}
          disabled={selectedUsers.length === 0 || isGenerating}
        >
          Generate User Bills
        </button>
      </div>
    </div>
  );
};
```

### **3. API Service Layer**
```javascript
// services/brokerageAPI.js
class BrokerageAPI {
  static baseURL = 'https://your-domain.com/BrokerHub';
  
  static getHeaders() {
    return {
      'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
      'Content-Type': 'application/json'
    };
  }

  static async getSummary(financialYear) {
    const response = await fetch(
      `${this.baseURL}/Brokerage/summary/${financialYear}`,
      { headers: this.getHeaders() }
    );
    const result = await response.json();
    return result.data;
  }

  static async getUserDetail(userId, financialYear) {
    const response = await fetch(
      `${this.baseURL}/Brokerage/user-detail/${userId}/${financialYear}`,
      { headers: this.getHeaders() }
    );
    const result = await response.json();
    return result.data;
  }

  static async downloadUserBill(userId, financialYear, format = 'html') {
    const endpoint = format === 'excel' 
      ? `excel/user/${userId}/${financialYear}`
      : `bill/${userId}/${financialYear}`;
      
    const response = await fetch(
      `${this.baseURL}/Brokerage/${endpoint}`,
      { headers: this.getHeaders() }
    );
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `bill_${userId}.${format === 'excel' ? 'xlsx' : 'html'}`;
    a.click();
  }

  static async getDocumentStatus() {
    const response = await fetch(
      `${this.baseURL}/Documents/status`,
      { headers: this.getHeaders() }
    );
    const result = await response.json();
    return result.data;
  }
}

export { BrokerageAPI };
```

---

## 📱 **Mobile Responsiveness**

### **Responsive Design Guidelines**

#### **Breakpoints**
```css
/* Mobile First Approach */
.brokerage-dashboard {
  padding: 1rem;
}

/* Tablet */
@media (min-width: 768px) {
  .brokerage-dashboard {
    padding: 2rem;
  }
  
  .overview-cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
  }
}

/* Desktop */
@media (min-width: 1024px) {
  .breakdown-section {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
  }
}
```

#### **Mobile-Specific Features**
- **Touch-friendly buttons** (min 44px height)
- **Swipe gestures** for navigation
- **Collapsible sections** for space efficiency
- **Bottom sheet modals** for mobile UX

---

## 🎯 **Implementation Priority**

### **Phase 1: Core Features**
1. ✅ Dashboard with summary cards
2. ✅ User list with basic brokerage info
3. ✅ Individual document downloads
4. ✅ Basic error handling

### **Phase 2: Advanced Features**
1. ✅ User detail modal with transaction history
2. ✅ Bulk operations interface
3. ✅ Async status tracking
4. ✅ Progress indicators

### **Phase 3: Polish & Optimization**
1. ✅ Mobile responsiveness
2. ✅ Performance optimization
3. ✅ Advanced error handling
4. ✅ User experience improvements

---

## 📞 **Support & Integration**

### **Backend Team Contact** (Name: TARUN BATCHU, phone: 8332827443, email: tarunbatchu2000@gmail.com)
- **API Issues**: Check server logs and error responses
- **Authentication**: Ensure JWT token is valid and not expired
- **Performance**: Monitor API response times and caching

### **Testing Checklist**
- [ ] All API endpoints working with proper authentication
- [ ] Error handling for network failures
- [ ] Async operations with proper status tracking
- [ ] File downloads working correctly
- [ ] Mobile responsiveness tested
- [ ] Cross-browser compatibility verified

### **Deployment Notes**
- **Environment Variables**: Set API base URL for different environments
- **CORS Configuration**: Ensure frontend domain is whitelisted
- **File Downloads**: Configure proper MIME types for downloads
- **Caching**: Implement appropriate caching strategies

---

This comprehensive guide provides everything needed to implement the brokerage feature frontend. The React team can use this as a complete reference for building a professional, user-friendly interface with proper async handling and error management.