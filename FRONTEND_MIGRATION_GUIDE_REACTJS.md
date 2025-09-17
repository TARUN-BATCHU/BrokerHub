# Frontend Migration Guide - Bulk Bill Generation (ReactJS)

## 🚨 BREAKING CHANGES - Action Required

The bulk bill generation system has been **completely revamped** from async to synchronous. All existing bulk bill related code needs to be updated.

---

## 📋 What Changed

### ❌ **REMOVED (No longer work):**
```javascript
// These endpoints are DEPRECATED and will return 404
POST /BrokerHub/Brokerage/bulk-bills/users/{financialYearId}
POST /BrokerHub/Brokerage/bulk-bills/user/{userId}/{financialYearId}
POST /BrokerHub/Brokerage/bulk-excel/users/{financialYearId}
POST /BrokerHub/Brokerage/bulk-excel/city/{city}/{financialYearId}
GET  /BrokerHub/Documents/status
GET  /BrokerHub/Documents/download/{documentId}
```

### ✅ **NEW (Use these):**
```javascript
// New synchronous download endpoints
POST /BrokerHub/Brokerage/bulk-bills/html/{financialYearId}
POST /BrokerHub/Brokerage/bulk-bills/excel/{financialYearId}
```

---

## 🔧 New API Specifications

### 1. Download Bulk Bills (HTML Format)

**Endpoint:** `POST /BrokerHub/Brokerage/bulk-bills/html/{financialYearId}`

**Request:**
```http
POST /BrokerHub/Brokerage/bulk-bills/html/8
Content-Type: application/json

[22, 25, 30]
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/zip
Content-Disposition: attachment; filename="bulk-bills-html-FY8.zip"

[ZIP FILE BINARY DATA]
```

**cURL Example:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Brokerage/bulk-bills/html/8" \
  -H "Content-Type: application/json" \
  -d "[22, 25, 30]" \
  --output "bulk-bills-html.zip"
```

### 2. Download Bulk Bills (Excel Format)

**Endpoint:** `POST /BrokerHub/Brokerage/bulk-bills/excel/{financialYearId}`

**Request:**
```http
POST /BrokerHub/Brokerage/bulk-bills/excel/8
Content-Type: application/json

[22]
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/zip
Content-Disposition: attachment; filename="bulk-bills-excel-FY8.zip"

[ZIP FILE BINARY DATA]
```

**cURL Example:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/Brokerage/bulk-bills/excel/8" \
  -H "Content-Type: application/json" \
  -d "[22]" \
  --output "bulk-bills-excel.zip"
```

---

## ⚛️ ReactJS Implementation

### 1. Create Bulk Bill Service

**File:** `src/services/bulkBillService.js`

```javascript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const bulkBillService = {
  /**
   * Download bulk bills in HTML format
   * @param {number[]} userIds - Array of user IDs
   * @param {number} financialYearId - Financial year ID
   * @returns {Promise<Blob>} ZIP file blob
   */
  async downloadHtmlBills(userIds, financialYearId) {
    const response = await fetch(
      `${API_BASE_URL}/BrokerHub/Brokerage/bulk-bills/html/${financialYearId}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userIds),
      }
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.blob();
  },

  /**
   * Download bulk bills in Excel format
   * @param {number[]} userIds - Array of user IDs
   * @param {number} financialYearId - Financial year ID
   * @returns {Promise<Blob>} ZIP file blob
   */
  async downloadExcelBills(userIds, financialYearId) {
    const response = await fetch(
      `${API_BASE_URL}/BrokerHub/Brokerage/bulk-bills/excel/${financialYearId}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userIds),
      }
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.blob();
  },

  /**
   * Trigger file download in browser
   * @param {Blob} blob - File blob
   * @param {string} filename - Download filename
   */
  triggerDownload(blob, filename) {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }
};
```

### 2. Create Bulk Bill Hook

**File:** `src/hooks/useBulkBills.js`

```javascript
import { useState } from 'react';
import { bulkBillService } from '../services/bulkBillService';

export const useBulkBills = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const downloadBulkBills = async (userIds, financialYearId, format = 'excel') => {
    if (!userIds || userIds.length === 0) {
      setError('Please select at least one user');
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      let blob;
      let filename;

      if (format === 'html') {
        blob = await bulkBillService.downloadHtmlBills(userIds, financialYearId);
        filename = `bulk-bills-html-FY${financialYearId}.zip`;
      } else {
        blob = await bulkBillService.downloadExcelBills(userIds, financialYearId);
        filename = `bulk-bills-excel-FY${financialYearId}.zip`;
      }

      bulkBillService.triggerDownload(blob, filename);
      
      return {
        success: true,
        message: `Successfully downloaded ${userIds.length} ${format.toUpperCase()} bills!`
      };

    } catch (err) {
      const errorMessage = err.message || 'Failed to download bulk bills';
      setError(errorMessage);
      return {
        success: false,
        error: errorMessage
      };
    } finally {
      setIsLoading(false);
    }
  };

  return {
    downloadBulkBills,
    isLoading,
    error,
    clearError: () => setError(null)
  };
};
```

### 3. Update Bulk Bill Component

**File:** `src/components/BulkBillDownload.jsx`

```javascript
import React, { useState } from 'react';
import { useBulkBills } from '../hooks/useBulkBills';

const BulkBillDownload = ({ selectedUsers, financialYearId }) => {
  const { downloadBulkBills, isLoading, error, clearError } = useBulkBills();
  const [format, setFormat] = useState('excel');

  const handleDownload = async () => {
    if (!selectedUsers || selectedUsers.length === 0) {
      alert('Please select at least one user');
      return;
    }

    const userIds = selectedUsers.map(user => user.userId);
    const result = await downloadBulkBills(userIds, financialYearId, format);
    
    if (result.success) {
      // Show success message
      console.log(result.message);
    }
  };

  return (
    <div className="bulk-bill-download">
      <h3>Download Bulk Bills</h3>
      
      {/* Format Selection */}
      <div className="format-selection">
        <label>
          <input
            type="radio"
            value="excel"
            checked={format === 'excel'}
            onChange={(e) => setFormat(e.target.value)}
            disabled={isLoading}
          />
          Excel Format (.xlsx)
        </label>
        <label>
          <input
            type="radio"
            value="html"
            checked={format === 'html'}
            onChange={(e) => setFormat(e.target.value)}
            disabled={isLoading}
          />
          HTML Format (.html)
        </label>
      </div>

      {/* Download Button */}
      <button
        onClick={handleDownload}
        disabled={isLoading || !selectedUsers || selectedUsers.length === 0}
        className={`download-btn ${isLoading ? 'loading' : ''}`}
      >
        {isLoading ? (
          <>
            <span className="spinner"></span>
            Generating {format.toUpperCase()} bills for {selectedUsers?.length || 0} users...
          </>
        ) : (
          `Download ${selectedUsers?.length || 0} Bills (${format.toUpperCase()})`
        )}
      </button>

      {/* Error Display */}
      {error && (
        <div className="error-message">
          <span>{error}</span>
          <button onClick={clearError}>×</button>
        </div>
      )}

      {/* Selected Users Info */}
      {selectedUsers && selectedUsers.length > 0 && (
        <div className="selected-info">
          <p>Selected {selectedUsers.length} users for FY {financialYearId}</p>
          <ul>
            {selectedUsers.slice(0, 5).map(user => (
              <li key={user.userId}>{user.firmName}</li>
            ))}
            {selectedUsers.length > 5 && (
              <li>... and {selectedUsers.length - 5} more</li>
            )}
          </ul>
        </div>
      )}
    </div>
  );
};

export default BulkBillDownload;
```

### 4. Add CSS Styles

**File:** `src/styles/BulkBillDownload.css`

```css
.bulk-bill-download {
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin: 20px 0;
}

.format-selection {
  margin: 15px 0;
}

.format-selection label {
  display: block;
  margin: 8px 0;
  cursor: pointer;
}

.format-selection input[type="radio"] {
  margin-right: 8px;
}

.download-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  min-width: 200px;
}

.download-btn:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.download-btn.loading {
  background-color: #28a745;
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #ffffff;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 1s ease-in-out infinite;
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  background-color: #f8d7da;
  color: #721c24;
  padding: 10px;
  border-radius: 4px;
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error-message button {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #721c24;
}

.selected-info {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  margin-top: 15px;
}

.selected-info ul {
  margin: 10px 0;
  padding-left: 20px;
}

.selected-info li {
  margin: 4px 0;
}
```

---

## 🔄 Migration Steps

### Step 1: Remove Old Code
```javascript
// ❌ DELETE these functions/components
const generateBulkBills = () => { /* OLD ASYNC CODE */ };
const checkDocumentStatus = () => { /* OLD STATUS POLLING */ };
const downloadDocument = () => { /* OLD DOWNLOAD CODE */ };
```

### Step 2: Install New Files
1. Create `src/services/bulkBillService.js`
2. Create `src/hooks/useBulkBills.js`
3. Update bulk bill components
4. Add CSS styles

### Step 3: Update Imports
```javascript
// ❌ Remove old imports
import { documentService } from './oldDocumentService';

// ✅ Add new imports
import { useBulkBills } from '../hooks/useBulkBills';
```

### Step 4: Update Component Usage
```javascript
// ❌ Old usage
<BulkBillGenerator onGenerate={handleAsyncGeneration} />

// ✅ New usage
<BulkBillDownload 
  selectedUsers={selectedUsers} 
  financialYearId={currentFinancialYear} 
/>
```

---

## 🧪 Testing Guide

### Test Single User
```javascript
// Test data
const testUsers = [{ userId: 22, firmName: "Test Company" }];
const testFinancialYear = 8;

// Test Excel download
await downloadBulkBills([22], 8, 'excel');

// Test HTML download  
await downloadBulkBills([22], 8, 'html');
```

### Test Multiple Users
```javascript
// Test data
const testUsers = [
  { userId: 22, firmName: "Company A" },
  { userId: 25, firmName: "Company B" },
  { userId: 30, firmName: "Company C" }
];

// Test bulk download
await downloadBulkBills([22, 25, 30], 8, 'excel');
```

### Browser Testing
1. Open browser developer tools
2. Go to Network tab
3. Click download button
4. Verify POST request to correct endpoint
5. Check response headers for ZIP content
6. Verify file downloads automatically

---

## 🚀 Key Benefits

### ✅ **Simplified Flow**
- One click = One download
- No status polling needed
- No document tracking

### ✅ **Better UX**
- Clear loading states
- Immediate feedback
- Progress indication

### ✅ **Reliable**
- No async race conditions
- No database dependencies
- Direct file streaming

---

## 🔍 Troubleshooting

### Common Issues

**1. 404 Error on API Call**
```javascript
// ❌ Wrong - using old endpoint
POST /BrokerHub/Brokerage/bulk-bills/users/8

// ✅ Correct - using new endpoint
POST /BrokerHub/Brokerage/bulk-bills/excel/8
```

**2. Empty Request Body**
```javascript
// ❌ Wrong - missing user IDs
body: JSON.stringify([])

// ✅ Correct - with user IDs
body: JSON.stringify([22, 25, 30])
```

**3. File Not Downloading**
```javascript
// ✅ Ensure proper blob handling
const blob = await response.blob();
bulkBillService.triggerDownload(blob, filename);
```

### Debug Steps
1. Check browser console for errors
2. Verify API endpoint URLs
3. Check request payload format
4. Verify response content-type
5. Test with cURL first

---

## 📞 Support

If you encounter issues during migration:

1. **Check API endpoints** - Ensure using new URLs
2. **Verify request format** - JSON array of user IDs
3. **Test with cURL** - Validate backend functionality
4. **Check browser console** - Look for JavaScript errors
5. **Review network tab** - Verify request/response format

This migration ensures a much more reliable and user-friendly bulk bill generation experience!