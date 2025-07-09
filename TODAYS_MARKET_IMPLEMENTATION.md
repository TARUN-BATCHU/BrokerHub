# Today's Market Implementation Plan

## Database Changes
1. Update table names in SQL to match entity names:
   - `market_products` -> `market_product`
   - `seller_requests` -> `seller_request`
   - `buyer_requests` -> `buyer_request`

## Entity Changes

### MarketProduct
1. Add seller information fields:
   - `firmName`
   - `location`
2. Add status field with values:
   - ACTIVE
   - SOLD
   - EXPIRED
   - REMOVED

### SellerRequest
1. Add seller information fields:
   - `firmName`
   - `location`
2. Status values:
   - PENDING
   - ACCEPTED
   - REJECTED
   - EXPIRED

### BuyerRequest
1. Add buyer information fields:
   - `firmName`
   - `location`
2. Status values:
   - PENDING
   - ACCEPTED
   - REJECTED

## API Implementation

### Controllers
1. MarketController
   - GET /api/market/products
   - POST /api/market/products
   - GET /api/market/seller-requests
   - POST /api/market/seller-requests
   - PUT /api/market/seller-requests/{requestId}/accept

### DTOs
1. MarketProductDTO
2. SellerRequestDTO
3. BuyerRequestDTO
4. ErrorResponseDTO

### Services
1. MarketService
   - getMarketProducts()
   - addMarketProduct()
   - getSellerRequests()
   - submitSellerRequest()
   - acceptSellerRequest()

### Repositories
1. MarketProductRepository
2. SellerRequestRepository
3. BuyerRequestRepository

## Testing
1. Unit tests for services
2. Integration tests for controllers
3. Repository tests

## Security
1. Basic authentication implementation
2. Role-based access control:
   - Brokers can:
     - Add market products
     - View/accept seller requests
   - Sellers can:
     - Submit seller requests
     - View market products
   - Buyers can:
     - View market products
     - Submit buy requests

## Error Handling
1. Global exception handler
2. Custom exceptions:
   - UnauthorizedException
   - ValidationException
   - NotFoundException
   - BadRequestException

## UI Implementation
1. Loading states
2. Error handling
3. Responsive design
4. Accessibility
5. Performance optimizations