# City Brokerage Analytics API Documentation

## Overview
The City Brokerage Analytics API provides comprehensive city-wise brokerage analysis including current year data and year-over-year business growth comparison.

## Endpoint

### Get City Brokerage Analytics
**GET** `/api/brokerage-dashboard/{brokerId}/city/{city}/analytics`

Returns detailed brokerage analytics for a specific city.

**Parameters:**
- `brokerId` (path): ID of the broker
- `city` (path): Name of the city to analyze

**Response:**
```json
{
  "success": true,
  "message": "City analytics retrieved successfully",
  "data": {
    "city": "Mumbai",
    "totalMerchants": 25,
    "totalBagsSold": 1500,
    "totalBagsBought": 800,
    "totalBags": 2300,
    "totalActualBrokerage": 46000.00,
    "totalBrokeragePending": 15000.00,
    "totalBrokerageReceived": 31000.00,
    "totalPayments": 20,
    "totalPartialPayments": 8,
    "totalSuccessPayments": 12,
    "merchantsBusinessIncreased": 15,
    "merchantsBusinessDecreased": 5,
    "totalBrokerageChange": 5000.00
  }
}
```

## Response Fields

### Current Year Data
- `city`: Name of the city
- `totalMerchants`: Total number of merchants in the city
- `totalBagsSold`: Total bags sold by all merchants in the city
- `totalBagsBought`: Total bags bought by all merchants in the city
- `totalBags`: Total bags (sold + bought) in the city
- `totalActualBrokerage`: Total brokerage amount from the city
- `totalBrokeragePending`: Total pending brokerage amount from the city
- `totalBrokerageReceived`: Total brokerage amount received from the city
- `totalPayments`: Count of merchants with brokerage > 0 in the city
- `totalPartialPayments`: Count of merchants who made partial payments in the city
- `totalSuccessPayments`: Count of merchants who fully paid in the city

### Year-over-Year Comparison
- `merchantsBusinessIncreased`: Count of merchants whose business increased compared to last year
- `merchantsBusinessDecreased`: Count of merchants whose business decreased compared to last year
- `totalBrokerageChange`: Total change in brokerage amount compared to last year (positive = increase, negative = decrease)

## Business Logic

### Current Year Calculations
1. **Total Merchants**: Count of unique merchants in the city
2. **Bag Counts**: Sum of sold/bought bags from all merchants in the city
3. **Brokerage Amounts**: Sum of net brokerage, pending, and received amounts
4. **Payment Counts**: Count based on payment status

### Historical Comparison
1. **Business Growth**: Compares current year brokerage with previous financial year
2. **Merchant Classification**: 
   - Increased: Current year brokerage > Previous year brokerage
   - Decreased: Current year brokerage < Previous year brokerage
3. **Total Change**: Sum of all individual merchant changes

## Data Sources

### Current Year Data
- Source: `brokerage_payment` table joined with merchant addresses
- Filters: Broker ID and city name

### Historical Data
- Source: `brokerage_history` table
- Contains: Previous financial year brokerage data for comparison

## Usage Examples

### Get Mumbai Analytics
```bash
GET /api/brokerage-dashboard/1/city/Mumbai/analytics
```

### Get Delhi Analytics
```bash
GET /api/brokerage-dashboard/1/city/Delhi/analytics
```

## Prerequisites

1. **Brokerage Calculation**: Run brokerage calculation first to populate current year data
2. **Historical Data**: Historical data is automatically saved during brokerage calculation
3. **Address Data**: Merchants must have valid city information in their addresses

## Performance Considerations

- City analytics queries are optimized with proper indexes
- Historical comparison only runs when previous year data exists
- Results are calculated in real-time (consider caching for high-traffic scenarios)

## Error Handling

- Returns empty analytics if no merchants found in the city
- Gracefully handles missing historical data (shows zero for comparison fields)
- Validates broker existence before processing

## Integration Notes

- Automatically saves historical data during brokerage calculation
- Historical data enables year-over-year business growth tracking
- City-wise analytics support business expansion planning