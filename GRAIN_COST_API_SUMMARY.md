# Grain Cost API - Quick Reference

## Updated API Endpoints (3 Total)

### 1. POST - Add Grain Cost
```
POST /BrokerHub/grain-costs/{brokerId}
```
**CURL:**
```bash
curl -X POST "http://localhost:8080/BrokerHub/grain-costs/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM=" \
  -d '{"productName": "Wheat", "cost": 2500.50, "date": "15-01-2024"}'
```

### 2. GET - Retrieve All Grain Costs
```
GET /BrokerHub/grain-costs/{brokerId}
```
**CURL:**
```bash
curl -X GET "http://localhost:8080/BrokerHub/grain-costs/1" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM="
```

### 3. DELETE - Remove Grain Cost (NEW)
```
DELETE /BrokerHub/grain-costs/{brokerId}/{grainCostId}
```
**CURL:**
```bash
curl -X DELETE "http://localhost:8080/BrokerHub/grain-costs/1/3" \
  -H "Authorization: Basic dGFydW46c2VjdXJlUGFzc3dvcmQxMjM="
```

## Key Security Features
- **Multi-tenant isolation**: Each broker sees only their own data
- **Secure deletion**: Users can only delete their own records
- **Access control**: All operations verify broker ownership

## Sample Response Format
```json
{
    "success": true,
    "message": "Operation completed successfully",
    "data": { /* response data */ },
    "error": null
}
```

## Files Created/Updated
- ✅ **Entity**: GrainCost.java
- ✅ **DTOs**: GrainCostRequestDTO.java, GrainCostResponseDTO.java  
- ✅ **Repository**: GrainCostRepository.java (with secure queries)
- ✅ **Service**: GrainCostService.java (with delete method)
- ✅ **Controller**: GrainCostController.java (3 endpoints)
- ✅ **Database**: grain_costs_table_migration.sql
- ✅ **Documentation**: Complete API guide with CURL commands