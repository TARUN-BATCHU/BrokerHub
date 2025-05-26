# Dashboard Analytics Implementation

## Overview
This implementation provides comprehensive analytics for the brokerage application with month-wise breakdown, product analytics, city analytics, and merchant type analytics for any financial year.

## Features

### 1. Comprehensive Analytics API
- **Endpoint**: `GET /BrokerHub/Dashboard/{brokerId}/getFinancialYearAnalytics/{financialYearId}`
- **Description**: Single API that provides all analytics data for a financial year
- **Response**: Complete analytics with month-wise breakdown and overall totals

### 2. Redis Caching
- **Cache Duration**: 24 hours
- **Auto Refresh**: Daily at 2 AM via scheduled task
- **Manual Refresh**: Available via API endpoints

### 3. Performance Optimization
- **Optimized Queries**: Single queries with JOINs to minimize database hits
- **Caching Strategy**: Redis-based caching for frequently accessed data
- **Scheduled Updates**: Daily cache refresh to ensure data freshness

## API Endpoints

### Main Analytics Endpoint
```
GET /BrokerHub/Dashboard/{brokerId}/getFinancialYearAnalytics/{financialYearId}
```

**Response Structure:**
```json
{
  "financialYearId": 1,
  "financialYearName": "2025-2026",
  "startDate": "2025-04-01",
  "endDate": "2026-03-31",
  "totalBrokerage": 150000.00,
  "totalQuantity": 5000,
  "totalTransactionValue": 2500000.00,
  "totalTransactions": 250,
  "monthlyAnalytics": [
    {
      "month": "2025-04",
      "monthName": "April 2025",
      "totalBrokerage": 12500.00,
      "totalQuantity": 400,
      "totalTransactionValue": 200000.00,
      "totalTransactions": 20,
      "productAnalytics": [...],
      "cityAnalytics": [...],
      "merchantTypeAnalytics": [...]
    }
  ],
  "overallProductTotals": [...],
  "overallCityTotals": [...],
  "overallMerchantTypeTotals": [...]
}
```

### Cache Management Endpoints
```
POST /BrokerHub/Dashboard/refreshCache/{financialYearId}
POST /BrokerHub/Dashboard/refreshAllCache
```

### Test Endpoints
```
GET /BrokerHub/Test/health
GET /BrokerHub/Test/financialYears
```

## Data Structure

### Monthly Analytics
- Month-wise breakdown of all transactions
- Product analytics per month
- City analytics per month with product breakdown
- Merchant type analytics per month

### Product Analytics
- Total quantity per product
- Total brokerage per product
- Average price per product
- Average brokerage per unit

### City Analytics
- Total transactions per city
- Total sellers and buyers per city
- Product breakdown per city
- Total brokerage per city

### Merchant Type Analytics
- Separate analytics for MILLER and TRADER
- Quantity sold vs quantity bought
- Total brokerage paid
- Number of merchants per type

## Setup Instructions

### 1. Redis Installation
```bash
# Install Redis (Windows)
# Download and install Redis from https://redis.io/download

# Start Redis server
redis-server

# Verify Redis is running
redis-cli ping
```

### 2. Application Configuration
The application is already configured with Redis settings in `application.properties`:
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
spring.cache.redis.time-to-live=86400000
```

### 3. Database Requirements
Ensure your database has the following tables with data:
- `daily_ledger`
- `ledger_details`
- `ledger_record`
- `product`
- `user`
- `address`
- `financial_year`

## Usage Examples

### 1. Get Analytics for Financial Year
```bash
curl -X GET "http://localhost:8080/BrokerHub/Dashboard/1/getFinancialYearAnalytics/1"
```

### 2. Refresh Cache
```bash
curl -X POST "http://localhost:8080/BrokerHub/Dashboard/refreshCache/1"
```

### 3. Health Check
```bash
curl -X GET "http://localhost:8080/BrokerHub/Test/health"
```

## Performance Considerations

### 1. Database Optimization
- Ensure proper indexing on date columns
- Index foreign key relationships
- Consider partitioning for large datasets

### 2. Caching Strategy
- Data is cached for 24 hours
- Cache is refreshed daily at 2 AM
- Manual refresh available for immediate updates

### 3. Query Optimization
- Single queries with JOINs instead of multiple queries
- Aggregation done at database level
- Minimal data transfer between database and application

## Monitoring and Maintenance

### 1. Cache Monitoring
- Monitor Redis memory usage
- Check cache hit rates
- Monitor cache refresh performance

### 2. Database Performance
- Monitor query execution times
- Check for slow queries
- Optimize indexes as needed

### 3. Application Logs
- Analytics generation logs
- Cache refresh logs
- Error handling logs

## Future Enhancements

1. **Real-time Analytics**: WebSocket-based real-time updates
2. **Export Functionality**: PDF/Excel export of analytics
3. **Custom Date Ranges**: Analytics for custom date ranges
4. **Comparative Analytics**: Year-over-year comparisons
5. **Predictive Analytics**: Trend analysis and forecasting

## Troubleshooting

### Common Issues

1. **Redis Connection Issues**
   - Verify Redis server is running
   - Check connection settings in application.properties

2. **Cache Not Working**
   - Check Redis logs
   - Verify cache configuration
   - Test manual cache refresh

3. **Slow Query Performance**
   - Check database indexes
   - Monitor query execution plans
   - Consider data archiving for old records

4. **Memory Issues**
   - Monitor Redis memory usage
   - Adjust cache TTL if needed
   - Consider cache eviction policies
