# BrokerHub: Comprehensive Brokerage Management Platform

BrokerHub is a robust Spring Boot application that streamlines brokerage operations by providing a centralized platform for managing transactions, payments, and market activities. The platform offers real-time analytics, automated ledger management, and secure user authentication to help brokers efficiently manage their business relationships and financial transactions.

The application features comprehensive payment tracking, market operations management, detailed analytics dashboards, and multi-user support with role-based access control. It enables brokers to manage their daily operations, track financial transactions, and analyze business performance through intuitive interfaces while maintaining secure and organized records of all brokerage activities.

## Repository Structure
```
src/main/java/com/brokerhub/brokerageapp/
└── controller/                 # REST API controllers for different functionalities
    ├── AddressController.java         # Manages address-related operations
    ├── BankDetailsController.java     # Handles bank account information
    ├── BrokerController.java         # Core broker management and authentication
    ├── DailyLedgerController.java    # Daily transaction record management
    ├── DashboardController.java      # Analytics and reporting endpoints
    ├── FinancialYearController.java  # Financial year management
    ├── LedgerDetailsController.java  # Detailed ledger entry management
    ├── MarketController.java        # Market operations and trade management
    ├── PaymentController.java       # Payment processing and tracking
    ├── ProductController.java       # Product catalog management
    └── UserController.java          # User account management
```

## Usage Instructions
### Prerequisites
- Java 17 or higher
- Spring Boot 3.x
- Maven 3.6+
- PostgreSQL 12+ (assumed based on JPA usage)
- Redis (for caching)

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd brokerhub
```

2. Configure database properties in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/brokerhub
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Build the application:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

### Quick Start

1. Create a broker account:
```bash
curl -X POST http://localhost:8080/BrokerHub/broker/createBroker \
-H "Content-Type: application/json" \
-d '{
  "username": "broker1",
  "password": "securepass",
  "firmName": "ABC Trading"
}'
```

2. Login to get authentication token:
```bash
curl -X POST http://localhost:8080/BrokerHub/broker/login \
-H "Content-Type: application/json" \
-d '{
  "username": "broker1",
  "password": "securepass"
}'
```

3. Access the dashboard:
```bash
curl -X GET http://localhost:8080/BrokerHub/Dashboard/1/getFinancialYearAnalytics/2023 \
-H "Authorization: Bearer <your_token>"
```

### More Detailed Examples

1. Managing Products:
```bash
# Add a new product
curl -X POST http://localhost:8080/BrokerHub/Product/createProduct \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <your_token>" \
-d '{
  "name": "Cotton",
  "quality": "Premium",
  "basePrice": 1000.00
}'
```

2. Recording Transactions:
```bash
# Create a daily ledger entry
curl -X POST http://localhost:8080/BrokerHub/DailyLedger/create \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <your_token>" \
-d '{
  "date": "2024-01-20",
  "financialYearId": 1
}'
```

### Troubleshooting

1. Authentication Issues
- Error: "Invalid credentials"
  - Verify username and password
  - Check if account is verified
  - Reset password if necessary using `/broker/forgot-password`

2. Database Connection Issues
- Error: "Could not connect to database"
  - Verify PostgreSQL is running
  - Check database credentials
  - Ensure database exists
  - Command to check database status:
    ```bash
    pg_isready -h localhost -p 5432
    ```

3. Cache Issues
- Error: "Cache not synchronized"
  - Refresh cache using:
    ```bash
    curl -X POST http://localhost:8080/BrokerHub/Dashboard/refreshAllCache
    ```

## Data Flow
The application processes brokerage transactions through a multi-step workflow that includes market matching, payment processing, and ledger recording.

```ascii
User/Broker -> Market Operations -> Payment Processing -> Ledger Recording -> Analytics
     |              |                      |                    |               |
     v              v                      v                    v               v
Authentication -> Market    ->         Payment      ->      Ledger     ->   Dashboard
                Matching           Verification          Generation       Aggregation
```

Key Component Interactions:
1. Market Controller handles buy/sell order matching
2. Payment Controller processes and tracks transaction payments
3. Ledger Controllers maintain daily and detailed transaction records
4. Dashboard Controller aggregates data for analytics and reporting
5. User and Broker Controllers manage authentication and authorization
6. Product Controller maintains the product catalog
7. Address and Bank Details Controllers handle auxiliary information