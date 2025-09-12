# Bulk Bill Download System

## Overview
The BrokerHub application provides a comprehensive bulk bill generation system that allows brokers to generate multiple brokerage bills simultaneously in both PDF and Excel formats. The system operates asynchronously to handle large volumes of bill generation without blocking the main application thread.

## Features
- **Bulk PDF Bill Generation**: Generate HTML/PDF bills for multiple users
- **Bulk Excel Bill Generation**: Generate Excel bills using the enhanced template format
- **City-wise Generation**: Generate bills for all users in a specific city
- **User-specific Generation**: Generate bills for selected users
- **Asynchronous Processing**: Non-blocking background processing
- **Progress Tracking**: Real-time status monitoring
- **File Organization**: Structured file storage system

## API Endpoints

### 1. Bulk PDF Bills

#### Generate Bulk Bills for City
```http
POST /BrokerHub/Brokerage/bulk-bills/city/{city}/{financialYearId}
```
**Parameters:**
- `city` (path): City name for which to generate bills
- `financialYearId` (path): Financial year ID

**Response:**
```json
{
  "success": true,
  "data": "Bulk bill generation started for city: Mumbai",
  "message": "Request processed successfully"
}
```

#### Generate Bulk Bills for Selected Users
```http
POST /BrokerHub/Brokerage/bulk-bills/users/{financialYearId}
```
**Parameters:**
- `financialYearId` (path): Financial year ID
- **Request Body:** Array of user IDs
```json
[1, 2, 3, 4, 5]
```

**Response:**
```json
{
  "success": true,
  "data": "Bulk bill generation started for 5 users",
  "message": "Request processed successfully"
}
```

### 2. Bulk Excel Bills

#### Generate Bulk Excel for City
```http
POST /BrokerHub/Brokerage/bulk-excel/city/{city}/{financialYearId}
```
**Parameters:**
- `city` (path): City name for which to generate Excel bills
- `financialYearId` (path): Financial year ID

**Response:**
```json
{
  "success": true,
  "data": "Bulk Excel generation started for city: Mumbai",
  "message": "Request processed successfully"
}
```

#### Generate Bulk Excel for Selected Users
```http
POST /BrokerHub/Brokerage/bulk-excel/users/{financialYearId}
```
**Parameters:**
- `financialYearId` (path): Financial year ID
- **Request Body:** Array of user IDs
```json
[1, 2, 3, 4, 5]
```

**Response:**
```json
{
  "success": true,
  "data": "Bulk Excel generation started for 5 users",
  "message": "Request processed successfully"
}
```

## How It Works

### 1. Asynchronous Processing Architecture

The bulk bill generation system uses Spring's `@Async` annotation to process requests asynchronously:

```java
@Async
public void generateBulkExcelForCity(String city, Long brokerId, Long financialYearId)
```

**Benefits:**
- **Non-blocking**: API responds immediately while processing continues in background
- **Scalable**: Multiple bulk operations can run simultaneously
- **Resource Efficient**: Doesn't tie up web server threads
- **User-friendly**: Users don't have to wait for completion

### 2. Processing Flow

#### Step 1: Request Initiation
1. Client sends bulk generation request
2. System validates parameters
3. Immediate response sent to client
4. Background processing starts

#### Step 2: Document Tracking
```java
GeneratedDocument document = GeneratedDocument.builder()
    .broker(broker)
    .financialYearId(financialYearId)
    .documentType("BULK_CITY_EXCEL")
    .status("GENERATING")
    .city(city)
    .createdAt(LocalDateTime.now())
    .build();
```

#### Step 3: File Generation
- Creates organized directory structure
- Generates individual bills/Excel files
- Uses enhanced Excel template format
- Handles errors gracefully

#### Step 4: Status Updates
- Updates document status to "COMPLETED" or "FAILED"
- Records completion time and file paths
- Logs progress for monitoring

### 3. File Organization Structure

```
Project Root/
├── bills/
│   └── {brokerId}/
│       └── {financialYearId}/
│           ├── {city}/
│           │   ├── bill_1_FirmName1.html
│           │   ├── bill_2_FirmName2.html
│           │   └── ...
│           └── selected/
│               ├── bill_3_FirmName3.html
│               └── ...
└── excel/
    └── {brokerId}/
        └── {financialYearId}/
            ├── {city}/
            │   ├── FirmName1-brokerage-bill-FY2023.xlsx
            │   ├── FirmName2-brokerage-bill-FY2023.xlsx
            │   └── ...
            └── selected/
                ├── FirmName3-brokerage-bill-FY2023.xlsx
                └── ...
```

### 4. Excel Template Features

The bulk Excel generation uses the same enhanced template as individual downloads:

- **Professional Styling**: Light, official colors
- **Comprehensive Sections**:
  - Broker Details (firm name, contact info, bank details)
  - Merchant Details (client information)
  - Transaction Details with S.No column
  - Summary with totals
- **Enhanced Formatting**:
  - No grid lines for clean appearance
  - Proper column sizing
  - Merged headers
  - Color-coded sections

### 5. Error Handling

```java
try {
    generateUserExcel(user, broker, financialYearId, outputDir);
} catch (Exception e) {
    log.error("Failed to generate Excel for user: {}", user.getUserId(), e);
    // Continue with next user - doesn't stop entire process
}
```

**Error Recovery:**
- Individual failures don't stop bulk process
- Failed documents are logged
- Overall status updated appropriately
- Detailed error logging for debugging

## Monitoring and Status Tracking

### Database Tracking
The system uses `GeneratedDocument` entity to track:
- Document type (BULK_CITY_EXCEL, BULK_USER_EXCEL, etc.)
- Status (GENERATING, COMPLETED, FAILED)
- Creation and completion timestamps
- File paths
- Associated broker and financial year

### Status Values
- **GENERATING**: Process is currently running
- **COMPLETED**: All files generated successfully
- **FAILED**: Process encountered critical error

### Logging
Comprehensive logging at multiple levels:
```java
log.info("Starting bulk Excel generation for {} users in city: {}", cityUsers.size(), city);
log.info("Generated Excel for user: {} at {}", user.getFirmName(), fileName);
log.error("Failed to generate Excel for user: {}", user.getUserId(), e);
```

## Performance Considerations

### 1. Memory Management
- Files are generated one at a time to avoid memory issues
- Streams are properly closed after use
- Large datasets are processed in batches

### 2. Concurrency
- Multiple bulk operations can run simultaneously
- Thread pool configuration can be adjusted based on server capacity
- Database connections are managed efficiently

### 3. Storage
- Files are organized in structured directories
- Old files can be cleaned up periodically
- Disk space monitoring recommended for production

## Usage Examples

### Example 1: Generate Excel Bills for Mumbai
```bash
curl -X POST "http://localhost:8080/BrokerHub/Brokerage/bulk-excel/city/Mumbai/2023" \
  -H "Content-Type: application/json"
```

### Example 2: Generate Excel Bills for Specific Users
```bash
curl -X POST "http://localhost:8080/BrokerHub/Brokerage/bulk-excel/users/2023" \
  -H "Content-Type: application/json" \
  -d "[1, 2, 3, 4, 5]"
```

## Configuration

### Async Configuration
Ensure proper async configuration in your Spring application:

```java
@EnableAsync
@Configuration
public class AsyncConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("bulk-generation-");
        executor.initialize();
        return executor;
    }
}
```

### File Storage Configuration
Configure appropriate file storage paths and permissions:
- Ensure write permissions for application user
- Configure adequate disk space
- Set up backup strategies for generated files

## Best Practices

1. **Monitor Resource Usage**: Keep track of CPU, memory, and disk usage during bulk operations
2. **Implement Cleanup**: Regularly clean up old generated files
3. **Error Notification**: Consider implementing notification system for failed bulk operations
4. **Progress Updates**: Implement progress tracking for large bulk operations
5. **Rate Limiting**: Consider implementing rate limiting for bulk operations
6. **Backup Strategy**: Implement backup for critical generated documents

## Troubleshooting

### Common Issues

1. **Out of Memory**: Reduce batch size or increase heap memory
2. **File Permission Errors**: Check write permissions on output directories
3. **Database Connection Issues**: Monitor connection pool settings
4. **Slow Performance**: Check server resources and optimize queries

### Debug Logging
Enable debug logging for detailed troubleshooting:
```properties
logging.level.com.brokerhub.brokerageapp.service.BulkBillGenerationServiceImpl=DEBUG
```

## Future Enhancements

1. **Progress API**: Real-time progress tracking endpoint
2. **Email Notifications**: Automatic email when bulk generation completes
3. **ZIP Download**: Compress generated files for easier download
4. **Scheduled Generation**: Automatic bulk generation at specified intervals
5. **Template Customization**: Allow brokers to customize Excel templates