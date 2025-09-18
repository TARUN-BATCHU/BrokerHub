# Contacts Management System API Documentation

## Overview
The Contacts Management System provides a comprehensive solution for managing business contacts across different categories. It supports:
- **Nested Sections**: Create hierarchical categories (e.g., Buyers > Gram Dal Buyers, Moong Dal Buyers)
- **Many-to-Many Relationships**: Same contact can belong to multiple sections (e.g., a merchant who buys both gram dal and moong dal)
- **No Data Duplication**: Contacts are stored once and linked to multiple sections via mappings

## Database Schema

### Tables Created
- `contact_sections` - Categories/sections with hierarchical support (parent_section_id)
- `contacts` - Main contact information (no direct section reference)
- `contact_section_mappings` - Many-to-many relationship between contacts and sections
- `contact_phones` - Multiple phone numbers per contact
- `contact_addresses` - Multiple addresses per contact

## API Endpoints

### Base URL
```
/api/contacts
```

### Authentication
All endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## Contact Sections Management

### 1. Create Contact Section
**POST** `/api/contacts/sections`

Creates a new contact section/category.

**Request Body:**
```json
{
  "sectionName": "Gram Dal Buyers",
  "description": "Buyers specializing in gram dal",
  "parentSectionId": 1
}
```

**For Root Section:**
```json
{
  "sectionName": "Buyers",
  "description": "All grain buyers",
  "parentSectionId": null
}
```

**Response:**
```json
{
  "success": true,
  "message": "Section created successfully",
  "data": {
    "id": 1,
    "sectionName": "Sellers",
    "description": "Grain sellers and suppliers",
    "brokerId": 123,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 2. Get All Sections
**GET** `/api/contacts/sections`

Retrieves all contact sections for the current broker.

**Response:**
```json
{
  "success": true,
  "message": "Sections retrieved successfully",
  "data": [
    {
      "id": 1,
      "sectionName": "Buyers",
      "description": "All grain buyers",
      "brokerId": 123,
      "parentSectionId": null,
      "parentSectionName": null,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    },
    {
      "id": 2,
      "sectionName": "Gram Dal Buyers",
      "description": "Buyers specializing in gram dal",
      "brokerId": 123,
      "parentSectionId": 1,
      "parentSectionName": "Buyers",
      "createdAt": "2024-01-15T10:31:00",
      "updatedAt": "2024-01-15T10:31:00"
    }
  ]
}
```

### 3. Get Section by ID
**GET** `/api/contacts/sections/{sectionId}`

**Response:**
```json
{
  "success": true,
  "message": "Section retrieved successfully",
  "data": {
    "id": 1,
    "sectionName": "Sellers",
    "description": "Grain sellers and suppliers",
    "brokerId": 123,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 4. Update Section
**PUT** `/api/contacts/sections/{sectionId}`

**Request Body:**
```json
{
  "sectionName": "Updated Sellers",
  "description": "Updated description for sellers"
}
```

### 5. Delete Section
**DELETE** `/api/contacts/sections/{sectionId}`

**Note:** Cannot delete sections that have existing contacts.

### 6. Get Root Sections
**GET** `/api/contacts/sections/root`

Retrieves only top-level sections (no parent).

### 7. Get Child Sections
**GET** `/api/contacts/sections/{parentId}/children`

Retrieves all child sections of a parent section.

---

## Contact Management

### 1. Create Contact
**POST** `/api/contacts`

Creates a new contact with phone numbers and addresses.

**Request Body:**
```json
{
  "firmName": "ABC Grain Traders",
  "userName": "John Doe",
  "gstNumber": "27ABCDE1234F1Z5",
  "additionalInfo": "Specializes in wheat and rice trading",
  "sectionIds": [1, 2],
  "phoneNumbers": [
    {
      "phoneNumber": "9876543210",
      "phoneType": "Primary"
    },
    {
      "phoneNumber": "9876543211",
      "phoneType": "WhatsApp"
    }
  ],
  "addresses": [
    {
      "address": "123 Market Street, Grain Market",
      "city": "Mumbai",
      "state": "Maharashtra",
      "pincode": "400001",
      "addressType": "Shop"
    },
    {
      "address": "456 Residential Area",
      "city": "Mumbai",
      "state": "Maharashtra",
      "pincode": "400002",
      "addressType": "Home"
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Contact created successfully",
  "data": {
    "id": 1,
    "firmName": "ABC Grain Traders",
    "userName": "John Doe",
    "gstNumber": "27ABCDE1234F1Z5",
    "additionalInfo": "Specializes in wheat and rice trading",
    "brokerId": 123,
    "sectionIds": [1, 2],
    "sectionNames": ["Buyers", "Gram Dal Buyers"],
    "phoneNumbers": [
      {
        "id": 1,
        "phoneNumber": "9876543210",
        "phoneType": "Primary"
      },
      {
        "id": 2,
        "phoneNumber": "9876543211",
        "phoneType": "WhatsApp"
      }
    ],
    "addresses": [
      {
        "id": 1,
        "address": "123 Market Street, Grain Market",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "addressType": "Shop"
      },
      {
        "id": 2,
        "address": "456 Residential Area",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400002",
        "addressType": "Home"
      }
    ],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 2. Get All Contacts
**GET** `/api/contacts`

Retrieves all contacts for the current broker.

### 3. Get Contacts by Section
**GET** `/api/contacts/section/{sectionId}`

Retrieves all contacts in a specific section.

### 4. Get Contact by ID
**GET** `/api/contacts/{contactId}`

### 5. Update Contact
**PUT** `/api/contacts/{contactId}`

**Request Body:** Same as create contact

### 6. Delete Contact
**DELETE** `/api/contacts/{contactId}`

### 7. Add Contact to Section
**POST** `/api/contacts/{contactId}/sections/{sectionId}`

Adds an existing contact to an additional section.

### 8. Remove Contact from Section
**DELETE** `/api/contacts/{contactId}/sections/{sectionId}`

Removes a contact from a specific section (contact remains in other sections).

---

## Search and Pagination

### 1. Search Contacts
**GET** `/api/contacts/search?query={searchTerm}&page={page}&size={size}`

Searches contacts by name, firm name, or GST number.

**Parameters:**
- `query` (required): Search term
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size

**Response:**
```json
{
  "success": true,
  "message": "Search completed successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "firmName": "ABC Grain Traders",
        "userName": "John Doe",
        // ... other contact fields
      }
    ],
    "pageable": {
      "sort": {
        "sorted": false,
        "unsorted": true
      },
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1
  }
}
```

### 2. Get Paginated Contacts
**GET** `/api/contacts/paginated?page={page}&size={size}`

**Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size

---

## Data Models

### ContactSectionDTO
```typescript
interface ContactSectionDTO {
  id?: number;
  sectionName: string;
  description?: string;
  brokerId?: number;
  parentSectionId?: number;
  parentSectionName?: string;
  childSections?: ContactSectionDTO[];
  createdAt?: string;
  updatedAt?: string;
}
```

### ContactDTO
```typescript
interface ContactDTO {
  id?: number;
  firmName?: string;
  userName: string;
  gstNumber?: string;
  additionalInfo?: string;
  brokerId?: number;
  sectionIds: number[];
  sectionNames?: string[];
  phoneNumbers?: ContactPhoneDTO[];
  addresses?: ContactAddressDTO[];
  createdAt?: string;
  updatedAt?: string;
}
```

### ContactPhoneDTO
```typescript
interface ContactPhoneDTO {
  id?: number;
  phoneNumber: string;
  phoneType?: string; // "Primary", "Secondary", "WhatsApp", etc.
}
```

### ContactAddressDTO
```typescript
interface ContactAddressDTO {
  id?: number;
  address: string;
  city?: string;
  state?: string;
  pincode?: string;
  addressType?: string; // "Shop", "Home", "Service Area", etc.
}
```

---

## Default Sections

The system automatically creates these default sections for new brokers:

### Root Sections:
- **Buyers** - All grain buyers
- **Sellers** - Grain sellers and suppliers  
- **Transport** - Transportation services
- **Workers** - Labor and workers
- **Local Authorities** - Government and local officials

### Example Nested Sections:
- **Buyers**
  - **Gram Dal Buyers** - Buyers specializing in gram dal
  - **Moong Dal Buyers** - Buyers specializing in moong dal

**Use Case Example:**
Merchant "ABC Traders" can be assigned to both "Gram Dal Buyers" and "Moong Dal Buyers" sections without duplicating contact data.

---

## Error Handling

### Common Error Responses

**400 Bad Request:**
```json
{
  "success": false,
  "message": "Section with this name already exists",
  "data": null
}
```

**404 Not Found:**
```json
{
  "success": false,
  "message": "Contact not found",
  "data": null
}
```

**500 Internal Server Error:**
```json
{
  "success": false,
  "message": "Failed to retrieve contacts",
  "data": null
}
```

---

## Frontend Implementation Guidelines

### 1. Contact Form Fields

**Required Fields:**
- User Name (always required)
- At least one Section (multi-select dropdown with hierarchy)

**Optional Fields:**
- Firm Name (hide for individuals)
- GST Number (show only for firms)
- Phone Numbers (allow multiple)
- Addresses (allow multiple)
- Additional Info (textarea)

### 2. Form Validation

```javascript
const validateContact = (contact) => {
  const errors = {};
  
  if (!contact.userName?.trim()) {
    errors.userName = 'User name is required';
  }
  
  if (!contact.sectionIds || contact.sectionIds.length === 0) {
    errors.sectionIds = 'Please select at least one section';
  }
  
  if (contact.firmName && !contact.gstNumber) {
    errors.gstNumber = 'GST number is required for firms';
  }
  
  if (contact.phoneNumbers?.length === 0) {
    errors.phoneNumbers = 'At least one phone number is required';
  }
  
  return errors;
};
```

### 3. Dynamic Form Behavior

```javascript
// Show/hide firm-specific fields
const showFirmFields = contact.firmName?.trim().length > 0;

// Section management with hierarchy
const [rootSections, setRootSections] = useState([]);
const [childSections, setChildSections] = useState({});

const loadChildSections = async (parentId) => {
  const children = await fetch(`/api/contacts/sections/${parentId}/children`);
  setChildSections(prev => ({ ...prev, [parentId]: children.data }));
};

// Multi-select section handling
const toggleSection = (sectionId) => {
  setContact(prev => ({
    ...prev,
    sectionIds: prev.sectionIds.includes(sectionId)
      ? prev.sectionIds.filter(id => id !== sectionId)
      : [...prev.sectionIds, sectionId]
  }));
};

// Phone number management
const addPhoneNumber = () => {
  setContact(prev => ({
    ...prev,
    phoneNumbers: [...prev.phoneNumbers, { phoneNumber: '', phoneType: 'Primary' }]
  }));
};

// Address management
const addAddress = () => {
  setContact(prev => ({
    ...prev,
    addresses: [...prev.addresses, { address: '', addressType: 'Shop' }]
  }));
};
```

### 4. Search Implementation

```javascript
const searchContacts = async (query, page = 0, size = 10) => {
  try {
    const response = await fetch(
      `/api/contacts/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    const result = await response.json();
    return result.data;
  } catch (error) {
    console.error('Search failed:', error);
    throw error;
  }
};
```

### 5. Section Management

```javascript
const createSection = async (sectionData) => {
  try {
    const response = await fetch('/api/contacts/sections', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(sectionData)
    });
    
    const result = await response.json();
    if (result.success) {
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Failed to create section:', error);
    throw error;
  }
};
```

---

## Testing

### Sample Test Data

```json
{
  "sections": [
    {
      "sectionName": "Buyers",
      "description": "All grain buyers",
      "parentSectionId": null
    },
    {
      "sectionName": "Gram Dal Buyers", 
      "description": "Buyers specializing in gram dal",
      "parentSectionId": 1
    },
    {
      "sectionName": "Moong Dal Buyers",
      "description": "Buyers specializing in moong dal", 
      "parentSectionId": 1
    }
  ],
  "contacts": [
    {
      "firmName": "ABC Multi Traders",
      "userName": "John Doe",
      "gstNumber": "27ABCDE1234F1Z5",
      "sectionIds": [2, 3],
      "phoneNumbers": [
        {
          "phoneNumber": "9876543210",
          "phoneType": "Primary"
        }
      ],
      "addresses": [
        {
          "address": "Market Street, Grain Market",
          "city": "Mumbai",
          "state": "Maharashtra", 
          "pincode": "400001",
          "addressType": "Shop"
        }
      ]
    }
  ]
}
```

---

## Migration

Run the migration script `contacts_system_migration.sql` to create the required database tables and default sections.

```sql
-- Execute this script in your database
source contacts_system_migration.sql;
```

This will create all necessary tables with:
- Hierarchical section support
- Many-to-many contact-section relationships
- Default root sections and example nested sections
- Proper foreign key constraints and indexes

## Key Benefits

✅ **No Data Duplication**: Same contact can be in multiple sections without copying data
✅ **Hierarchical Organization**: Create nested categories for better organization
✅ **Flexible Relationships**: Add/remove contacts from sections dynamically
✅ **Scalable Design**: Supports unlimited nesting levels and section assignments