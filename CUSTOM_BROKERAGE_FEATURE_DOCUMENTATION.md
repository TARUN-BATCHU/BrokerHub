# Custom Brokerage Feature Documentation

## Overview
This feature allows users to optionally specify a custom brokerage rate when downloading bills (Excel or HTML/PDF). The custom brokerage rate is applied to all transactions in the bill without making any changes to the database.

## Key Features
- **Optional Parameter**: Custom brokerage is completely optional
- **No Database Changes**: All calculations are done in-memory during bill generation
- **Per-Bag Calculation**: Custom brokerage is applied per bag for each transaction
- **Multiple Formats**: Supports both Excel and HTML/PDF bill formats

## API Endpoints

### 1. Generate User Brokerage Bill (HTML/PDF) with Custom Brokerage
```
GET /BrokerHub/Brokerage/bill/{userId}/{financialYearId}?customBrokerage={amount}
```

**Parameters:**
- `userId` (Path): ID of the user
- `financialYearId` (Path): Financial year ID
- `customBrokerage` (Query, Optional): Custom brokerage rate per bag in rupees

**Example:**
```
GET /BrokerHub/Brokerage/bill/123/2024?customBrokerage=2.5
```

### 2. Generate User Brokerage Excel with Custom Brokerage
```
GET /BrokerHub/Brokerage/excel/user/{userId}/{financialYearId}?customBrokerage={amount}
```

**Parameters:**
- `userId` (Path): ID of the user
- `financialYearId` (Path): Financial year ID
- `customBrokerage` (Query, Optional): Custom brokerage rate per bag in rupees

**Example:**
```
GET /BrokerHub/Brokerage/excel/user/123/2024?customBrokerage=2.0
```

## How It Works

### Without Custom Brokerage
When no custom brokerage is provided, the system uses the original brokerage amounts from the database:
- Transaction brokerage = Original brokerage from database
- Total brokerage = Sum of all original transaction brokerages

### With Custom Brokerage
When custom brokerage is provided (e.g., ₹2 per bag):
- Transaction brokerage = Custom brokerage × Quantity
- Total brokerage = Sum of (Custom brokerage × Quantity) for all transactions

### Example Calculation
**Scenario**: User has transactions:
- Buyer A: 10 bags
- Buyer B: 20 bags
- Custom brokerage: ₹2 per bag

**Calculations**:
- Buyer A brokerage: 10 × ₹2 = ₹20
- Buyer B brokerage: 20 × ₹2 = ₹40
- Total brokerage: ₹20 + ₹40 = ₹60

## Implementation Details

### Modified Components

1. **BrokerageController.java**
   - Added `customBrokerage` parameter to bill and Excel endpoints
   - Parameter is optional using `@RequestParam(required = false)`

2. **BrokerageService.java & BrokerageServiceImpl.java**
   - Added overloaded methods with custom brokerage parameter
   - Maintains backward compatibility with existing methods

3. **ExcelGenerationService.java & ExcelGenerationServiceImpl.java**
   - Added custom brokerage calculation logic
   - Updates Excel generation to show custom rates and recalculated amounts

4. **PdfGenerationService.java & PdfGenerationServiceImpl.java**
   - Added custom brokerage calculation logic
   - Updates HTML/PDF generation to show custom rates and recalculated amounts

### Key Methods Added

```java
// Service interfaces
byte[] generateUserBrokerageBill(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage);
byte[] generateUserBrokerageExcel(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage);

// Helper methods
private BigDecimal calculateTotalBrokerage(UserBrokerageDetailDTO userDetail, BigDecimal customBrokerage);
```

## Usage Examples

### 1. Download Excel with Custom Brokerage
```bash
curl -X GET "http://localhost:8080/BrokerHub/Brokerage/excel/user/123/2024?customBrokerage=2.5" \
  -H "Authorization: Bearer your-token" \
  -o "custom-brokerage-bill.xlsx"
```

### 2. Download HTML Bill with Custom Brokerage
```bash
curl -X GET "http://localhost:8080/BrokerHub/Brokerage/bill/123/2024?customBrokerage=3.0" \
  -H "Authorization: Bearer your-token" \
  -o "custom-brokerage-bill.html"
```

### 3. Download with Original Brokerage (No Custom Rate)
```bash
curl -X GET "http://localhost:8080/BrokerHub/Brokerage/excel/user/123/2024" \
  -H "Authorization: Bearer your-token" \
  -o "original-brokerage-bill.xlsx"
```

## Bill Format Changes

### Excel Format
When custom brokerage is applied:
- Shows "Custom Brokerage Rate: ₹X per bag" in the summary section
- Each transaction shows recalculated brokerage amount
- Total brokerage reflects the new calculations

### HTML/PDF Format
When custom brokerage is applied:
- Shows "Custom Brokerage Rate: ₹X per bag" in the summary table
- Each transaction row shows recalculated brokerage amount
- Total brokerage payable reflects the new calculations

## Important Notes

1. **No Database Impact**: Custom brokerage calculations are done only during bill generation and do not affect stored data

2. **Backward Compatibility**: All existing endpoints continue to work without any changes

3. **Validation**: The system accepts any positive BigDecimal value for custom brokerage

4. **Precision**: All calculations maintain BigDecimal precision for financial accuracy

5. **Per-Transaction Calculation**: Custom brokerage is applied individually to each transaction based on its quantity

## Testing

### Test Cases
1. **Normal Bill Generation**: Verify existing functionality works without custom brokerage
2. **Custom Brokerage Excel**: Test Excel generation with various custom brokerage rates
3. **Custom Brokerage HTML**: Test HTML/PDF generation with custom brokerage rates
4. **Zero Custom Brokerage**: Test with ₹0 custom brokerage
5. **High Precision**: Test with decimal values like ₹2.75 per bag
6. **Large Quantities**: Test with high quantity transactions

### Sample Test Data
```json
{
  "userId": 123,
  "financialYearId": 2024,
  "customBrokerage": 2.5,
  "expectedTransactions": [
    {
      "quantity": 10,
      "expectedBrokerage": 25.0
    },
    {
      "quantity": 20,
      "expectedBrokerage": 50.0
    }
  ],
  "expectedTotalBrokerage": 75.0
}
```

## Future Enhancements

1. **Bulk Operations**: Extend custom brokerage to bulk bill generation
2. **Rate Validation**: Add minimum/maximum brokerage rate validation
3. **Rate History**: Option to save frequently used custom rates
4. **City-wise Custom Rates**: Apply different rates for different cities
5. **Product-wise Custom Rates**: Apply different rates for different products