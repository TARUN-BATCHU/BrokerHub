# User & Product Service Optimization with Redis Caching

## Overview
This document describes the optimization implemented for user and product-related methods in the brokerage application, specifically targeting the performance issues in data retrieval methods that were fetching full entities when only specific fields were needed.

## Problem Statement
The original implementation had performance issues:
- **User Methods**: `getUserNamesAndIds()` and `getUserNames()` methods were fetching all User entities from database
- **Product Methods**: Similar issues existed with product data retrieval methods
- These methods were called frequently (e.g., every time user clicks search bar, product dropdowns)
- Full entity fetching was inefficient when only specific fields were needed
- No caching mechanism was in place for frequently accessed data

## Solution Implemented

### 1. Database Query Optimization

**New User Repository Methods** (`UserRepository.java`):
```java
@Query("SELECT u.userId, u.firmName FROM User u")
List<Object[]> findUserIdsAndFirmNames();

@Query("SELECT u.firmName FROM User u")
List<String> findAllFirmNames();

@Query("SELECT u.userId, u.firmName, u.userType, u.gstNumber, u.ownerName FROM User u")
List<Object[]> findBasicUserInfo();
```

**New Product Repository Methods** (`ProductRepository.java`):
```java
@Query("SELECT p.productId, p.productName FROM Product p")
List<Object[]> findProductIdsAndNames();

@Query("SELECT p.productName FROM Product p")
List<String> findAllProductNames();

@Query("SELECT DISTINCT p.productName FROM Product p")
List<String> findDistinctProductNames();

@Query("SELECT p.productId, p.productName, p.productBrokerage, p.quality, p.price FROM Product p")
List<Object[]> findBasicProductInfo();

@Query("SELECT p.productName, p.quality FROM Product p")
List<Object[]> findProductNamesAndQualities();
```

### 2. Redis Caching Implementation

**UserCacheService** with 1-hour TTL:
- `getAllUserNames()` - Cached firm names only
- `getUserNamesAndIds()` - Cached user ID and firm name mappings
- `getAllBasicUserInfo()` - Cached basic user information
- Automatic cache invalidation on user create/update/delete operations

**ProductCacheService** with 1-hour TTL:
- `getAllProductNames()` - Cached product names only
- `getDistinctProductNames()` - Cached distinct product names
- `getProductNamesAndIds()` - Cached product ID and name mappings
- `getAllBasicProductInfo()` - Cached basic product information
- `getProductNamesAndQualities()` - Cached product names with qualities
- Automatic cache invalidation on product create/update/delete operations

### 3. Optimized Service Methods
**Before**:
```java
public List<String> getUserNames() {
    List<User> allUsers = userRepository.findAll(); // Fetches all fields
    // Manual iteration and extraction
}
```

**After**:
```java
public List<String> getUserNames() {
    return userCacheService.getAllUserNames(); // Cached, optimized query
}
```

### 4. Cache Invalidation Strategy

**User Cache Invalidation**:
- New user is created (`createUser()`)
- User is updated (`updateUser()`)
- User is deleted (`deleteUser()`)
- Bulk user upload is performed (`bulkUploadUsers()`)

**Product Cache Invalidation**:
- New product is created (`createProduct()`)
- Product is updated (`updateProduct()`)
- Product is deleted (`deleteProduct()`)

## Configuration

### Redis Configuration
```properties
# Redis configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000  # 1 hour TTL
```

### Fallback Mechanism
The application includes a fallback to in-memory caching when Redis is not available, ensuring the application continues to work without Redis.

## Performance Benefits

1. **Database Load Reduction**: 
   - Fetches only required fields instead of full entities
   - Reduces data transfer from database

2. **Response Time Improvement**:
   - First call: Database query + cache storage
   - Subsequent calls: Direct cache retrieval (much faster)

3. **Memory Efficiency**:
   - Stores only essential user data in cache
   - Uses optimized DTOs instead of full entities

4. **Scalability**:
   - Redis can be scaled independently
   - Supports distributed caching for multiple application instances

## Usage Examples

### Getting User Names (Optimized)
```java
@Autowired
private UserService userService;

// This now uses Redis cache with 1-hour TTL
List<String> userNames = userService.getUserNames();
```

### Getting User Names and IDs (Optimized)
```java
// This now uses optimized query + Redis cache
List<HashMap<String, Long>> userNamesAndIds = userService.getUserNamesAndIds();
```

### Getting Product Names (Optimized)
```java
@Autowired
private ProductService productService;

// This now uses Redis cache with 1-hour TTL
List<String> productNames = productService.getProductNames();
List<String> distinctProductNames = productService.getDistinctProductNames();
```

### Getting Product Names and IDs (Optimized)
```java
// This now uses optimized query + Redis cache
List<HashMap<String, Long>> productNamesAndIds = productService.getProductNamesAndIds();
```

### Getting Basic Product Info (Optimized)
```java
// This now uses cached basic product information
List<ProductBasicInfoDTO> basicProductInfo = productService.getBasicProductInfo();
List<ProductBasicInfoDTO> productNamesAndQualities = productService.getProductNamesAndQualities();
```

## Testing
Run the test suite to verify the optimization:
```bash
# Test user cache service
mvn test -Dtest=UserCacheServiceTest

# Test product cache service
mvn test -Dtest=ProductCacheServiceTest

# Run all cache-related tests
mvn test -Dtest="*CacheServiceTest"
```

## Monitoring
- Cache hit/miss rates can be monitored through Redis logs
- Application logs include cache operation information
- Spring Boot Actuator can provide cache metrics

## Future Enhancements
1. Implement cache warming strategies
2. Add cache metrics and monitoring dashboards
3. Consider implementing distributed cache invalidation for multi-instance deployments
4. Add cache compression for large datasets
