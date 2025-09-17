# Frontend API Update - Bulk Bill Generation

## 🚨 Breaking Changes & New Features

### 1. NEW ENDPOINT: Single User Bulk Bill Generation

**Previous (NOT WORKING):**
```javascript
// ❌ This was incorrect usage
fetch('/BrokerHub/Brokerage/bulk-bills/users/8', { method: 'POST' })
```

**New (RECOMMENDED):**
```javascript
// ✅ Use this for single user
const response = await fetch(`/BrokerHub/Brokerage/bulk-bills/user/${userId}/${financialYearId}`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' }
});
```

### 2. UPDATED: Multiple Users Bulk Bill (No Change in Usage)

```javascript
// ✅ Existing endpoint - no change needed
const response = await fetch(`/BrokerHub/Brokerage/bulk-bills/users/${financialYearId}`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify([userId1, userId2, userId3])
});
```

## 📋 Complete Frontend Implementation

### Step 1: Generate Bulk Bills

```javascript
async function generateBulkBills(userIds, financialYearId) {
  let endpoint, body;
  
  if (userIds.length === 1) {
    // Single user - use new endpoint
    endpoint = `/BrokerHub/Brokerage/bulk-bills/user/${userIds[0]}/${financialYearId}`;
    body = null;
  } else {
    // Multiple users - use existing endpoint
    endpoint = `/BrokerHub/Brokerage/bulk-bills/users/${financialYearId}`;
    body = JSON.stringify(userIds);
  }
  
  const response = await fetch(endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: body
  });
  
  const result = await response.json();
  
  if (result.status === 'success') {
    // Extract document ID from response message
    const documentId = extractDocumentId(result.data);
    return { success: true, documentId, message: result.message };
  } else {
    return { success: false, error: result.message };
  }
}

function extractDocumentId(message) {
  const match = message.match(/Document ID: (\d+)/);
  return match ? parseInt(match[1]) : null;
}
```

### Step 2: Poll Document Status

```javascript
async function pollDocumentStatus(documentId = null) {
  const response = await fetch('/BrokerHub/Documents/status');
  const result = await response.json();
  
  if (result.status === 'success') {
    const documents = result.data;
    
    if (documentId) {
      // Find specific document
      return documents.find(doc => doc.documentId === documentId);
    } else {
      // Return all documents
      return documents;
    }
  }
  return null;
}
```

### Step 3: Complete Workflow with UI Updates

```javascript
async function handleBulkBillGeneration(userIds, financialYearId) {
  try {
    // Show loading state
    showLoading('Generating bulk bills...');
    
    // Step 1: Start generation
    const result = await generateBulkBills(userIds, financialYearId);
    
    if (!result.success) {
      showError(result.error);
      return;
    }
    
    const documentId = result.documentId;
    showInfo(`Generation started. Document ID: ${documentId}`);
    
    // Step 2: Poll for completion
    const pollInterval = setInterval(async () => {
      const document = await pollDocumentStatus(documentId);
      
      if (document) {
        updateDocumentStatus(document);
        
        if (document.status === 'COMPLETED') {
          clearInterval(pollInterval);
          showSuccess('Bills generated successfully!');
          showDownloadButton(document.documentId);
        } else if (document.status === 'FAILED') {
          clearInterval(pollInterval);
          showError('Bill generation failed');
        }
      }
    }, 2000); // Poll every 2 seconds
    
    // Stop polling after 5 minutes
    setTimeout(() => clearInterval(pollInterval), 300000);
    
  } catch (error) {
    showError('Error: ' + error.message);
  }
}
```

### Step 4: Download Generated Bills

```javascript
async function downloadBulkBills(documentId) {
  try {
    const response = await fetch(`/BrokerHub/Documents/download/${documentId}`);
    
    if (response.ok) {
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `bulk-bills-${documentId}.zip`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } else {
      showError('Download failed');
    }
  } catch (error) {
    showError('Download error: ' + error.message);
  }
}
```

## 📊 Updated Response Formats

### Generation Response (UPDATED)
```json
{
  "status": "success",
  "message": "Request processed successfully",
  "data": "Bulk bill generation started for 1 user (Document ID: 123)"
}
```

### Status Response (UNCHANGED)
```json
{
  "status": "success",
  "message": "Documents retrieved successfully",
  "data": [
    {
      "documentId": 123,
      "documentType": "BULK_USER_BILLS",
      "status": "COMPLETED",
      "createdAt": "2025-01-27T10:30:00",
      "completedAt": "2025-01-27T10:31:00",
      "userIds": "8"
    }
  ]
}
```

## 🎯 UI/UX Recommendations

### 1. Immediate Feedback
```javascript
// Show document immediately after generation starts
function showDocumentTracking(documentId) {
  const html = `
    <div class="document-tracker" data-doc-id="${documentId}">
      <span>Document #${documentId}</span>
      <span class="status generating">Generating...</span>
      <div class="progress-bar"><div class="progress"></div></div>
    </div>
  `;
  document.getElementById('document-list').innerHTML += html;
}
```

### 2. Real-time Status Updates
```javascript
function updateDocumentStatus(document) {
  const tracker = document.querySelector(`[data-doc-id="${document.documentId}"]`);
  const statusEl = tracker.querySelector('.status');
  
  statusEl.className = `status ${document.status.toLowerCase()}`;
  statusEl.textContent = document.status;
  
  if (document.status === 'COMPLETED') {
    tracker.innerHTML += `<button onclick="downloadBulkBills(${document.documentId})">Download</button>`;
  }
}
```

### 3. Error Handling
```javascript
function showError(message) {
  // Show user-friendly error messages
  const errorMap = {
    'Broker not found': 'Session expired. Please login again.',
    'User not found': 'Selected user no longer exists.',
    'Failed to generate': 'Generation failed. Please try again.'
  };
  
  const userMessage = errorMap[message] || message;
  // Display error to user
}
```

## 🔄 Migration Steps

1. **Update API calls** - Replace single user calls with new endpoint
2. **Add document ID tracking** - Extract and store document IDs from responses
3. **Implement real-time polling** - Show status updates immediately
4. **Update UI components** - Add progress indicators and download buttons
5. **Test thoroughly** - Verify both single and multiple user scenarios

## ⚠️ Important Notes

- **Document IDs are now returned** - Use them for tracking and downloads
- **Status appears immediately** - No more waiting for async document creation
- **Better error handling** - More specific error messages
- **Polling recommended** - Check status every 2-3 seconds for better UX