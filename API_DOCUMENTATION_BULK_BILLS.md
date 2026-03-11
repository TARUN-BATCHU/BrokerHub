# Bulk Bill APIs - Custom Brokerage Feature Documentation

## Overview
All three bulk bill generation APIs now support an optional `customBrokerage` query parameter. This allows generating bills for multiple users with a custom brokerage amount instead of the calculated brokerage.

---

## API Endpoints

### 1. Bulk Bills HTML Generation

**Endpoint:** `POST /BrokerHub/Brokerage/bulk-bills/html/{financialYearId}`

**Method:** `POST`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| financialYearId | Long | Yes | The financial year ID for which bills are generated |

**Query Parameters:**
| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| customBrokerage | BigDecimal | No | Custom brokerage amount to apply to all users' bills. If not provided, normal calculated brokerage is used. | 10, 15.5, 20 |

**Request Body:**
```json
[324, 325, 326, 327]
```
- Type: `List<Long>`
- Description: Array of user IDs for which bills need to be generated

**Response:**
- **Content-Type:** `application/zip`
- **Body:** ZIP file containing HTML bills for all users
- **Filename:** `bulk-bills-html-FY{financialYearId}.zip`

**Example Requests:**

With custom brokerage:
```bash
POST http://localhost:8080/BrokerHub/Brokerage/bulk-bills/html/1?customBrokerage=10
Content-Type: application/json

[324, 325, 326, 327]
```

Without custom brokerage (normal behavior):
```bash
POST http://localhost:8080/BrokerHub/Brokerage/bulk-bills/html/1
Content-Type: application/json

[324, 325, 326, 327]
```

**JavaScript/Axios Example:**
```javascript
// With custom brokerage
const response = await axios.post(
  'http://localhost:8080/BrokerHub/Brokerage/bulk-bills/html/1',
  [324, 325, 326, 327],
  {
    params: { customBrokerage: 10 },
    responseType: 'blob'
  }
);

// Without custom brokerage
const response = await axios.post(
  'http://localhost:8080/BrokerHub/Brokerage/bulk-bills/html/1',
  [324, 325, 326, 327],
  {
    responseType: 'blob'
  }
);
```

---

### 2. Bulk Bills Excel Generation

**Endpoint:** `POST /BrokerHub/Brokerage/bulk-bills/excel/{financialYearId}`

**Method:** `POST`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| financialYearId | Long | Yes | The financial year ID for which bills are generated |

**Query Parameters:**
| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| customBrokerage | BigDecimal | No | Custom brokerage amount to apply to all users' bills. If not provided, normal calculated brokerage is used. | 10, 15.5, 20 |

**Request Body:**
```json
[324, 325, 326, 327]
```
- Type: `List<Long>`
- Description: Array of user IDs for which bills need to be generated

**Response:**
- **Content-Type:** `application/zip`
- **Body:** ZIP file containing Excel bills for all users
- **Filename:** `bulk-bills-excel-FY{financialYearId}.zip`

**Example Requests:**

With custom brokerage:
```bash
POST http://localhost:8080/BrokerHub/Brokerage/bulk-bills/excel/1?customBrokerage=15.5
Content-Type: application/json

[324, 325, 326, 327]
```

Without custom brokerage (normal behavior):
```bash
POST http://localhost:8080/BrokerHub/Brokerage/bulk-bills/excel/1
Content-Type: application/json

[324, 325, 326, 327]
```

**JavaScript/Axios Example:**
```javascript
// With custom brokerage
const response = await axios.post(
  'http://localhost:8080/BrokerHub/Brokerage/bulk-bills/excel/1',
  [324, 325, 326, 327],
  {
    params: { customBrokerage: 15.5 },
    responseType: 'blob'
  }
);

// Without custom brokerage
const response = await axios.post(
  'http://localhost:8080/BrokerHub/Brokerage/bulk-bills/excel/1',
  [324, 325, 326, 327],
  {
    responseType: 'blob'
  }
);
```

---

### 3. Bulk Print Bills Generation

**Endpoint:** `POST /BrokerHub/Brokerage/bulk-print-bills/{financialYearId}`

**Method:** `POST`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| financialYearId | Long | Yes | The financial year ID for which bills are generated |

**Query Parameters:**
| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| customBrokerage | BigDecimal | No | Custom brokerage amount to apply to all users' bills. If not provided, normal calculated brokerage is used. | 10, 15.5, 20 |

**Request Body:**
```json
[324, 325, 326, 327]
```
- Type: `List<Long>`
- Description: Array of user IDs for which bills need to be generated

**Response:**
- **Content-Type:** `application/zip`
- **Body:** ZIP file containing print-optimized HTML bills for all users
- **Filename:** `bulk-print-bills-FY{financialYearId}.zip`

**Example Requests:**

With custom brokerage:
```bash
POST http://localhost:8080/BrokerHub/Brokerage/bulk-print-bills/1?customBrokerage=20
Content-Type: application/json

[324, 325, 326, 327]
```

Without custom brokerage (normal behavior):
```bash
POST http://localhost:8080/BrokerHub/Brokerage/bulk-print-bills/1
Content-Type: application/json

[324, 325, 326, 327]
```

**JavaScript/Axios Example:**
```javascript
// With custom brokerage
const response = await axios.post(
  'http://localhost:8080/BrokerHub/Brokerage/bulk-print-bills/1',
  [324, 325, 326, 327],
  {
    params: { customBrokerage: 20 },
    responseType: 'blob'
  }
);

// Without custom brokerage
const response = await axios.post(
  'http://localhost:8080/BrokerHub/Brokerage/bulk-print-bills/1',
  [324, 325, 326, 327],
  {
    responseType: 'blob'
  }
);
```

---

## Changes Summary

### What Changed?
All three bulk bill APIs now accept an optional `customBrokerage` query parameter:
1. `/bulk-bills/html/{financialYearId}` - HTML bulk bills
2. `/bulk-bills/excel/{financialYearId}` - Excel bulk bills
3. `/bulk-print-bills/{financialYearId}` - Print-optimized bulk bills

### Backward Compatibility
✅ **Fully backward compatible** - Existing API calls without the `customBrokerage` parameter will continue to work as before, using the normal calculated brokerage.

### Parameter Details

**customBrokerage:**
- **Type:** `BigDecimal` (Number with decimal support)
- **Required:** No (Optional)
- **Format:** Numeric value (can include decimals)
- **Valid Examples:** `10`, `15.5`, `20`, `12.75`
- **Invalid Examples:** `"ten"`, `null` (just omit the parameter instead)
- **Behavior:**
  - If provided: All users' bills will use this custom brokerage amount
  - If omitted: Each user's bill will use their calculated brokerage amount (existing behavior)

---

## Frontend Implementation Guide

### React Example Component

```javascript
import React, { useState } from 'react';
import axios from 'axios';

const BulkBillGenerator = () => {
  const [userIds, setUserIds] = useState([324, 325, 326, 327]);
  const [customBrokerage, setCustomBrokerage] = useState('');
  const [financialYearId, setFinancialYearId] = useState(1);

  const downloadBulkBills = async (type) => {
    try {
      const params = {};
      
      // Only add customBrokerage if user has entered a value
      if (customBrokerage && customBrokerage.trim() !== '') {
        params.customBrokerage = parseFloat(customBrokerage);
      }

      const response = await axios.post(
        `http://localhost:8080/BrokerHub/Brokerage/bulk-bills/${type}/${financialYearId}`,
        userIds,
        {
          params: params,
          responseType: 'blob',
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      // Download the file
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `bulk-bills-${type}-FY${financialYearId}.zip`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error downloading bulk bills:', error);
      alert('Failed to download bulk bills');
    }
  };

  return (
    <div>
      <h2>Bulk Bill Generator</h2>
      
      <div>
        <label>Custom Brokerage (Optional):</label>
        <input
          type="number"
          step="0.01"
          value={customBrokerage}
          onChange={(e) => setCustomBrokerage(e.target.value)}
          placeholder="Leave empty for calculated brokerage"
        />
      </div>

      <div>
        <button onClick={() => downloadBulkBills('html')}>
          Download HTML Bills
        </button>
        <button onClick={() => downloadBulkBills('excel')}>
          Download Excel Bills
        </button>
        <button onClick={() => downloadBulkBills('print-bills')}>
          Download Print Bills
        </button>
      </div>
    </div>
  );
};

export default BulkBillGenerator;
```

### Fetch API Example

```javascript
const downloadBulkBills = async (userIds, financialYearId, customBrokerage = null) => {
  const url = new URL(`http://localhost:8080/BrokerHub/Brokerage/bulk-bills/html/${financialYearId}`);
  
  // Add customBrokerage only if provided
  if (customBrokerage !== null && customBrokerage !== undefined) {
    url.searchParams.append('customBrokerage', customBrokerage);
  }

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userIds)
  });

  if (response.ok) {
    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = `bulk-bills-html-FY${financialYearId}.zip`;
    a.click();
  }
};

// Usage examples:
// With custom brokerage
downloadBulkBills([324, 325, 326], 1, 10);

// Without custom brokerage (normal behavior)
downloadBulkBills([324, 325, 326], 1);
```

---

## Testing Checklist for Frontend Team

- [ ] Test HTML bulk bills WITH custom brokerage parameter
- [ ] Test HTML bulk bills WITHOUT custom brokerage parameter
- [ ] Test Excel bulk bills WITH custom brokerage parameter
- [ ] Test Excel bulk bills WITHOUT custom brokerage parameter
- [ ] Test Print bulk bills WITH custom brokerage parameter
- [ ] Test Print bulk bills WITHOUT custom brokerage parameter
- [ ] Test with decimal values (e.g., 15.5, 20.75)
- [ ] Test with integer values (e.g., 10, 20)
- [ ] Verify ZIP file downloads correctly
- [ ] Verify bills inside ZIP have correct brokerage amounts
- [ ] Test with empty string for customBrokerage (should behave as if not provided)
- [ ] Test backward compatibility with existing code

---

## Error Handling

**Possible Error Scenarios:**

1. **Invalid User IDs:** If any user ID doesn't exist, that user will be skipped (logged as warning)
2. **Invalid Financial Year:** Returns 400 Bad Request
3. **Invalid customBrokerage format:** Returns 400 Bad Request
4. **Empty user list:** May return empty ZIP or error

**Recommended Frontend Validation:**
```javascript
const validateCustomBrokerage = (value) => {
  if (value === '' || value === null || value === undefined) {
    return true; // Empty is valid (optional parameter)
  }
  
  const num = parseFloat(value);
  if (isNaN(num) || num < 0) {
    return false; // Invalid number or negative
  }
  
  return true;
};
```

---

## Migration Notes

### For Existing Code:
No changes required! All existing API calls will continue to work exactly as before.

### For New Features:
Simply add the `customBrokerage` parameter to the query string when needed.

**Before (still works):**
```javascript
axios.post('/BrokerHub/Brokerage/bulk-bills/html/1', [324, 325])
```

**After (with new feature):**
```javascript
axios.post('/BrokerHub/Brokerage/bulk-bills/html/1', [324, 325], {
  params: { customBrokerage: 10 }
})
```

---

## Support

For any questions or issues, please contact the backend team or refer to the single bill API documentation which follows the same pattern:
- Single bill API: `GET /BrokerHub/Brokerage/bill/{userId}/{financialYearId}?customBrokerage=10`
