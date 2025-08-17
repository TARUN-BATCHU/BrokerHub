# Custom Brokerage Feature Testing Guide

## Quick Test Commands

### 1. Test Excel Generation with Custom Brokerage
```bash
# Test with ₹2 per bag custom brokerage
curl -X GET "http://localhost:8080/BrokerHub/Brokerage/excel/user/1/1?customBrokerage=2" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -o "test-custom-brokerage-2rs.xlsx"

# Test with ₹2.5 per bag custom brokerage
curl -X GET "http://localhost:8080/BrokerHub/Brokerage/excel/user/1/1?customBrokerage=2.5" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -o "test-custom-brokerage-2.5rs.xlsx"
```

### 2. Test HTML Bill Generation with Custom Brokerage
```bash
# Test with ₹3 per bag custom brokerage
curl -X GET "http://localhost:8080/BrokerHub/Brokerage/bill/1/1?customBrokerage=3" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -o "test-custom-brokerage-3rs.html"
```

### 3. Test Without Custom Brokerage (Original Functionality)
```bash
# Test original Excel generation
curl -X GET "http://localhost:8080/BrokerHub/Brokerage/excel/user/1/1" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -o "test-original-brokerage.xlsx"

# Test original HTML bill generation
curl -X GET "http://localhost:8080/BrokerHub/Brokerage/bill/1/1" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -o "test-original-bill.html"
```

## Expected Results

### Example Scenario
**User has following transactions:**
- Transaction 1: Buyer A, 10 bags, Original brokerage: ₹15
- Transaction 2: Buyer B, 20 bags, Original brokerage: ₹30
- **Total Original Brokerage: ₹45**

### With Custom Brokerage ₹2 per bag:
- Transaction 1: 10 × ₹2 = ₹20
- Transaction 2: 20 × ₹2 = ₹40
- **Total Custom Brokerage: ₹60**

### What to Verify in Generated Files:

#### Excel File Should Show:
1. **Summary Section:**
   - "Custom Brokerage Rate: ₹2.00 per bag"
   - "Total Brokerage Payable: ₹60"

2. **Transaction Details:**
   - Transaction 1 brokerage column: ₹20
   - Transaction 2 brokerage column: ₹40

#### HTML File Should Show:
1. **Summary Table:**
   - Row showing "Custom Brokerage Rate: ₹2 per bag"
   - "Total Brokerage Payable: ₹60"

2. **Transaction Table:**
   - Transaction 1 brokerage: ₹20
   - Transaction 2 brokerage: ₹40

## Postman Collection

### Request 1: Excel with Custom Brokerage
```json
{
  "name": "Generate Excel with Custom Brokerage",
  "request": {
    "method": "GET",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{jwt_token}}"
      }
    ],
    "url": {
      "raw": "{{base_url}}/BrokerHub/Brokerage/excel/user/{{user_id}}/{{financial_year_id}}?customBrokerage=2.5",
      "host": ["{{base_url}}"],
      "path": ["BrokerHub", "Brokerage", "excel", "user", "{{user_id}}", "{{financial_year_id}}"],
      "query": [
        {
          "key": "customBrokerage",
          "value": "2.5"
        }
      ]
    }
  }
}
```

### Request 2: HTML Bill with Custom Brokerage
```json
{
  "name": "Generate HTML Bill with Custom Brokerage",
  "request": {
    "method": "GET",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{jwt_token}}"
      }
    ],
    "url": {
      "raw": "{{base_url}}/BrokerHub/Brokerage/bill/{{user_id}}/{{financial_year_id}}?customBrokerage=3",
      "host": ["{{base_url}}"],
      "path": ["BrokerHub", "Brokerage", "bill", "{{user_id}}", "{{financial_year_id}}"],
      "query": [
        {
          "key": "customBrokerage",
          "value": "3"
        }
      ]
    }
  }
}
```

## Test Validation Checklist

### ✅ Functional Tests
- [ ] Excel generation works with custom brokerage
- [ ] HTML bill generation works with custom brokerage
- [ ] Original functionality works without custom brokerage parameter
- [ ] Custom brokerage calculations are correct
- [ ] Total brokerage is recalculated properly

### ✅ Edge Cases
- [ ] Custom brokerage = 0
- [ ] Custom brokerage with decimal values (e.g., 2.75)
- [ ] Very high custom brokerage values
- [ ] User with no transactions
- [ ] User with single transaction

### ✅ File Content Verification
- [ ] Excel shows custom brokerage rate in summary
- [ ] Excel shows recalculated transaction brokerages
- [ ] HTML shows custom brokerage rate in summary
- [ ] HTML shows recalculated transaction brokerages
- [ ] File downloads successfully
- [ ] File opens without errors

### ✅ Database Integrity
- [ ] No changes made to database during custom brokerage calculation
- [ ] Original brokerage data remains unchanged
- [ ] No new records created

## Common Issues and Solutions

### Issue 1: File Not Downloading
**Solution:** Check Authorization header and ensure user has access to the specified userId

### Issue 2: Incorrect Calculations
**Solution:** Verify that custom brokerage is being multiplied by quantity for each transaction

### Issue 3: Custom Brokerage Not Showing
**Solution:** Ensure the customBrokerage parameter is being passed correctly in the URL

### Issue 4: Excel File Corrupted
**Solution:** Check that the response Content-Type is set to `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`

## Performance Testing

### Load Test Scenarios
1. **Concurrent Downloads:** Multiple users downloading bills with custom brokerage simultaneously
2. **Large Data Sets:** Users with 1000+ transactions using custom brokerage
3. **Memory Usage:** Monitor memory consumption during custom brokerage calculations

### Expected Performance
- Custom brokerage calculation should add minimal overhead
- File generation time should be similar to original functionality
- Memory usage should not increase significantly

## Integration Testing

### Test with Frontend
1. Add custom brokerage input field to bill download forms
2. Test form submission with various brokerage values
3. Verify downloaded files contain correct calculations
4. Test error handling for invalid brokerage values

### API Integration
1. Test with different authentication methods
2. Verify response headers are correct
3. Test with different user roles and permissions
4. Validate error responses for invalid parameters