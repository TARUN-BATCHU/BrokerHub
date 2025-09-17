# 📋 Brokerage Bill API - Frontend Integration Guide

## 🚀 Overview

The Brokerage Bill API provides professional bill generation with multiple formats and customization options. This guide covers all endpoints, parameters, and frontend integration examples.

## 🔗 API Endpoints

### 1. Professional HTML Bill
```http
GET /BrokerHub/Brokerage/bill/{userId}/{financialYearId}
```

**Features:**
- 🎨 Modern design with gradients and colors
- 📊 Visual summary cards
- 💰 Highlighted total brokerage
- 📱 Mobile responsive
- 🎯 Professional appearance

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `userId` | Long | Yes | User ID |
| `financialYearId` | Long | Yes | Financial Year ID |
| `customBrokerage` | BigDecimal | No | Custom brokerage rate per bag |

**Response:**
- **Content-Type:** `text/html`
- **Filename:** `brokerage-bill-{userId}.html`

### 2. Print-Optimized Bill
```http
GET /BrokerHub/Brokerage/bill/print/{userId}/{financialYearId}
```

**Features:**
- ⚫ Black & white (ink-friendly)
- 🖨️ Built-in print button
- 📄 Multiple paper sizes
- 🔄 Portrait/Landscape orientation
- 💾 Optimized for printing

**Parameters:**
| Parameter | Type | Required | Default | Valid Values |
|-----------|------|----------|---------|--------------|
| `userId` | Long | Yes | - | Any valid user ID |
| `financialYearId` | Long | Yes | - | Any valid financial year ID |
| `customBrokerage` | BigDecimal | No | null | Any positive decimal |
| `paperSize` | String | No | "a4" | "a4", "a5", "legal", "letter" |
| `orientation` | String | No | "portrait" | "portrait", "landscape" |

**Response:**
- **Content-Type:** `text/html`
- **Filename:** `print-bill-{userId}.html`

### 3. Bulk HTML Bills
```http
POST /BrokerHub/Brokerage/bulk-bills/html/{financialYearId}
```

**Request Body:**
```json
[11, 27, 35, 42]
```

**Response:**
- **Content-Type:** `application/zip`
- **Filename:** `bulk-bills-html-FY{financialYearId}.zip`

## 💻 Frontend Implementation

### JavaScript Functions

#### Professional Bill Download
```javascript
const downloadProfessionalBill = async (userId, financialYearId, customBrokerage = null) => {
  const params = customBrokerage ? `?customBrokerage=${customBrokerage}` : '';
  const url = `/BrokerHub/Brokerage/bill/${userId}/${financialYearId}${params}`;
  
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    
    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = `brokerage-bill-${userId}.html`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(downloadUrl);
  } catch (error) {
    console.error('Download failed:', error);
    alert('Failed to download bill. Please try again.');
  }
};
```

#### Print-Optimized Bill Download
```javascript
const downloadPrintBill = async (userId, financialYearId, options = {}) => {
  const {
    customBrokerage = null,
    paperSize = 'a4',
    orientation = 'portrait'
  } = options;
  
  const params = new URLSearchParams();
  if (customBrokerage) params.append('customBrokerage', customBrokerage);
  params.append('paperSize', paperSize);
  params.append('orientation', orientation);
  
  const url = `/BrokerHub/Brokerage/bill/print/${userId}/${financialYearId}?${params}`;
  
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    
    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = `print-bill-${userId}.html`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(downloadUrl);
  } catch (error) {
    console.error('Download failed:', error);
    alert('Failed to download print bill. Please try again.');
  }
};
```

#### Bulk Bills Download
```javascript
const downloadBulkBills = async (userIds, financialYearId) => {
  try {
    const response = await fetch(`/BrokerHub/Brokerage/bulk-bills/html/${financialYearId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userIds)
    });
    
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    
    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = `bulk-bills-FY${financialYearId}.zip`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(downloadUrl);
  } catch (error) {
    console.error('Bulk download failed:', error);
    alert('Failed to download bulk bills. Please try again.');
  }
};
```

### React Components

#### Bill Options Modal
```jsx
import React, { useState } from 'react';

const BillOptionsModal = ({ userId, financialYearId, onClose, onDownload }) => {
  const [billType, setBillType] = useState('professional');
  const [paperSize, setPaperSize] = useState('a4');
  const [orientation, setOrientation] = useState('portrait');
  const [customBrokerage, setCustomBrokerage] = useState('');

  const handleDownload = () => {
    if (billType === 'professional') {
      downloadProfessionalBill(userId, financialYearId, customBrokerage || null);
    } else {
      downloadPrintBill(userId, financialYearId, {
        customBrokerage: customBrokerage || null,
        paperSize,
        orientation
      });
    }
    onDownload?.();
    onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h3>📄 Download Bill Options</h3>
        
        <div className="form-group">
          <label>Bill Type:</label>
          <div className="radio-group">
            <label>
              <input 
                type="radio" 
                value="professional" 
                checked={billType === 'professional'}
                onChange={(e) => setBillType(e.target.value)} 
              />
              🎨 Professional (Colorful, Modern)
            </label>
            <label>
              <input 
                type="radio" 
                value="print" 
                checked={billType === 'print'}
                onChange={(e) => setBillType(e.target.value)} 
              />
              🖨️ Print-Friendly (Black & White)
            </label>
          </div>
        </div>

        {billType === 'print' && (
          <div className="print-options">
            <div className="form-group">
              <label>Paper Size:</label>
              <select value={paperSize} onChange={(e) => setPaperSize(e.target.value)}>
                <option value="a4">📄 A4 (210 × 297 mm)</option>
                <option value="a5">📄 A5 (148 × 210 mm)</option>
                <option value="legal">📄 Legal (8.5 × 14 in)</option>
                <option value="letter">📄 Letter (8.5 × 11 in)</option>
              </select>
            </div>
            
            <div className="form-group">
              <label>Orientation:</label>
              <select value={orientation} onChange={(e) => setOrientation(e.target.value)}>
                <option value="portrait">📱 Portrait</option>
                <option value="landscape">🖥️ Landscape</option>
              </select>
            </div>
          </div>
        )}

        <div className="form-group">
          <label>Custom Brokerage (optional):</label>
          <input 
            type="number" 
            step="0.01"
            placeholder="Enter custom rate per bag"
            value={customBrokerage}
            onChange={(e) => setCustomBrokerage(e.target.value)}
          />
        </div>

        <div className="modal-actions">
          <button className="btn-primary" onClick={handleDownload}>
            📥 Download Bill
          </button>
          <button className="btn-secondary" onClick={onClose}>
            ❌ Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default BillOptionsModal;
```

#### Quick Action Buttons
```jsx
const BillActions = ({ userId, financialYearId }) => {
  const [showModal, setShowModal] = useState(false);

  return (
    <div className="bill-actions">
      <button 
        className="btn btn-professional"
        onClick={() => downloadProfessionalBill(userId, financialYearId)}
      >
        🎨 Professional Bill
      </button>
      
      <button 
        className="btn btn-print"
        onClick={() => downloadPrintBill(userId, financialYearId)}
      >
        🖨️ Print Bill
      </button>
      
      <button 
        className="btn btn-options"
        onClick={() => setShowModal(true)}
      >
        ⚙️ More Options
      </button>

      {showModal && (
        <BillOptionsModal
          userId={userId}
          financialYearId={financialYearId}
          onClose={() => setShowModal(false)}
        />
      )}
    </div>
  );
};
```

## 📱 Usage Examples

### Basic Usage
```javascript
// Download professional bill
downloadProfessionalBill(27, 8);

// Download print bill with default settings
downloadPrintBill(27, 8);

// Download with custom brokerage
downloadProfessionalBill(27, 8, 15.50);
```

### Advanced Usage
```javascript
// Print bill with custom paper size and orientation
downloadPrintBill(27, 8, {
  paperSize: 'a4',
  orientation: 'landscape',
  customBrokerage: 12.75
});

// Bulk download for multiple users
downloadBulkBills([11, 27, 35, 42], 8);
```

### Error Handling
```javascript
const safeDownload = async (userId, financialYearId) => {
  try {
    await downloadProfessionalBill(userId, financialYearId);
    console.log('✅ Bill downloaded successfully');
  } catch (error) {
    console.error('❌ Download failed:', error);
    // Show user-friendly error message
    showNotification('Failed to download bill. Please try again.', 'error');
  }
};
```

## 🎨 CSS Styling

### Modal Styles
```css
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 16px;
}

.radio-group label {
  display: block;
  margin-bottom: 8px;
  cursor: pointer;
}

.btn {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  margin-right: 8px;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-professional {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-print {
  background: #28a745;
  color: white;
}
```

## 🔧 Configuration

### Environment Variables
```javascript
// API Base URL
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

// Update API calls
const url = `${API_BASE_URL}/BrokerHub/Brokerage/bill/${userId}/${financialYearId}`;
```

### Default Settings
```javascript
const DEFAULT_BILL_OPTIONS = {
  paperSize: 'a4',
  orientation: 'portrait',
  billType: 'professional'
};
```

## 🚨 Error Codes

| Status Code | Description | Action |
|-------------|-------------|---------|
| 200 | Success | Download file |
| 400 | Bad Request | Check parameters |
| 404 | User/Year not found | Verify IDs |
| 500 | Server Error | Retry or contact support |

## 📋 Testing

### Test Cases
```javascript
// Test professional bill download
test('downloads professional bill', async () => {
  const userId = 27;
  const financialYearId = 8;
  
  // Mock fetch
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      blob: () => Promise.resolve(new Blob(['test'], { type: 'text/html' }))
    })
  );

  await downloadProfessionalBill(userId, financialYearId);
  
  expect(fetch).toHaveBeenCalledWith(
    `/BrokerHub/Brokerage/bill/${userId}/${financialYearId}`
  );
});
```

## 🔄 Migration Guide

### From Old API
```javascript
// Old way
window.open(`/old-api/bill/${userId}`);

// New way
downloadProfessionalBill(userId, financialYearId);
```

## 📞 Support

For technical support or questions:
- 📧 Email: dev-team@brokerhub.com
- 📱 Slack: #brokerage-api
- 📖 Documentation: [Internal Wiki](link-to-wiki)

---

**Last Updated:** September 2025  
**API Version:** v1.0  
**Maintained by:** BrokerHub Development Team