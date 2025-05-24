# Bulk User Upload Feature

This feature allows you to upload multiple users at once using an Excel file (.xlsx format).

## Endpoints

### 1. Download Template
**GET** `/BrokerHub/user/downloadTemplate`

Downloads an Excel template file with sample data and instructions.

**Response:** Excel file download

### 2. Bulk Upload Users
**POST** `/BrokerHub/user/bulkUpload`

Uploads users from an Excel file.

**Parameters:**
- `file` (multipart/form-data): Excel file (.xlsx)

**Response:**
```json
{
  "totalRecords": 10,
  "successfulRecords": 8,
  "failedRecords": 2,
  "errorMessages": [
    "Row 3: User with firm name 'ABC Corp' already exists",
    "Row 7: Firm name is required"
  ],
  "message": "Partial success: 8 users uploaded, 2 failed"
}
```

## Excel File Format

### Required Columns (in order):
1. **userType** - TRADER or MILLER (default: TRADER)
2. **gstNumber** - GST registration number
3. **firmName** - Company/Firm name (Required)
4. **ownerName** - Owner's name
5. **city** - City name
6. **area** - Area/locality
7. **pincode** - Postal code (Required for address lookup)
8. **email** - Email address
9. **bankName** - Bank name
10. **accountNumber** - Bank account number
11. **ifscCode** - Bank IFSC code
12. **branch** - Bank branch name
13. **phoneNumbers** - Phone numbers (comma-separated for multiple)
14. **brokerageRate** - Brokerage rate percentage
15. **shopNumber** - Shop/office number
16. **byProduct** - By-product (required for MILLER type only)

### Sample Data:
```
userType    | gstNumber     | firmName      | ownerName | city   | area      | pincode
TRADER      | GST123456789  | ABC Trading   | John Doe  | Mumbai | Andheri   | 400058
MILLER      | GST987654321  | XYZ Mills     | Jane Smith| Delhi  | Karol Bagh| 110005
```

## Usage Instructions

### 1. Download Template
```bash
curl -X GET http://localhost:8080/BrokerHub/user/downloadTemplate \
  -o user_bulk_upload_template.xlsx
```

### 2. Upload Filled Excel File
```bash
curl -X POST http://localhost:8080/BrokerHub/user/bulkUpload \
  -F "file=@your_users_file.xlsx" \
  -H "Content-Type: multipart/form-data"
```

### 3. Frontend Usage (JavaScript)
```javascript
// Download template
const downloadTemplate = async () => {
  const response = await fetch('/BrokerHub/user/downloadTemplate');
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'user_bulk_upload_template.xlsx';
  a.click();
};

// Upload file
const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch('/BrokerHub/user/bulkUpload', {
    method: 'POST',
    body: formData
  });
  
  const result = await response.json();
  console.log(result);
};
```

## Validation Rules

1. **File Format**: Only .xlsx files are accepted
2. **Required Fields**: firmName and pincode are mandatory
3. **Duplicate Check**: Users with existing firmName or gstNumber will be rejected
4. **User Type**: Defaults to TRADER if not specified
5. **Phone Numbers**: Multiple numbers can be provided comma-separated
6. **By-Product**: Required only for MILLER type users

## Error Handling

The system provides detailed error messages for each failed record:
- Row-specific error messages
- Validation failures
- Duplicate user detection
- File format errors

## Response Codes

- **200 OK**: Successful upload (all or partial success)
- **400 Bad Request**: Validation errors or no successful uploads
- **500 Internal Server Error**: Server-side errors

## Features

- ✅ Excel file validation
- ✅ Row-by-row processing with error tracking
- ✅ Duplicate user detection
- ✅ Detailed error reporting
- ✅ Sample template generation
- ✅ Support for both TRADER and MILLER user types
- ✅ Automatic default value assignment
- ✅ Bank details and address linking
- ✅ Phone number parsing (comma-separated)

## Technical Implementation

- **Apache POI**: For Excel file processing
- **Spring Boot**: REST API endpoints
- **Validation**: Field-level and business rule validation
- **Error Tracking**: Comprehensive error collection and reporting
- **Transaction Safety**: Each user creation is independent
