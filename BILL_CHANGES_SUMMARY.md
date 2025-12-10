# Bill Changes Summary

## Changes Made to Print Bill API (/BrokerHub/Brokerage/print-bill/{userId}/{financialYearId})

### 1. Database Layer Changes
- **UserBrokerageDetailDTO.java**: Added `counterPartyCity` field to `TransactionDetail` class
- **UserBrokerageRepository.java**: Updated `getUserTransactionDetails` query to include city information by joining with address tables

### 2. Service Layer Changes  
- **BrokerageServiceImpl.java**: Updated transaction detail mapping to include the new city field from query results

### 3. PDF Generation Changes
- **PdfGenerationServiceImpl.java**: 
  - Added "City" column to transaction table header (after "Merchant Firm Name")
  - Updated transaction table data to include city information
  - Adjusted column widths:
    - **Decreased**: Product (15% → 12%), Rate/Amount (14% → 12%), Brokerage (14% → 12%)
    - **Added**: City column (10%)
    - **Adjusted**: Merchant Firm Name (35% → 25% for print, 25% → 20% for regular bill)

### 4. Column Width Changes Summary
#### Print Bill:
- S.No: 4% (unchanged)
- Date: 8% (unchanged) 
- Merchant Firm Name: 35% → 25%
- **City: 10% (new)**
- Product: 15% → 12% (decreased)
- Qty: 6% (unchanged)
- Rate: 14% → 12% (decreased)
- Brokerage: 14% → 12% (decreased)

#### Regular Bill:
- S.No: 6% (unchanged)
- Date: 12% (unchanged)
- Merchant Firm Name: 25% → 20%
- **City: 10% (new)**
- Product: 15% → 12% (decreased)
- Quantity: 8% (unchanged)
- Amount: 12% → 10% (decreased)
- Brokerage: 12% → 10% (decreased)
- Type: 10% → 12% (slightly increased)

### 5. Data Population
The City column is populated with the address of the merchant involved in each transaction:
- For transactions where the user is the buyer: Shows seller's city
- For transactions where the user is the seller: Shows buyer's city
- Shows "N/A" if city information is not available

## Testing
To test the changes:
1. Hit the API: `GET /BrokerHub/Brokerage/print-bill/{userId}/{financialYearId}`
2. Verify the downloaded HTML bill contains:
   - New "City" column after "Merchant Firm Name"
   - Reduced width for Product, Rate, and Brokerage columns
   - City information populated for each transaction

## Files Modified
1. `UserBrokerageDetailDTO.java` - Added city field
2. `UserBrokerageRepository.java` - Updated query to fetch city
3. `BrokerageServiceImpl.java` - Updated mapping logic
4. `PdfGenerationServiceImpl.java` - Updated bill generation and CSS