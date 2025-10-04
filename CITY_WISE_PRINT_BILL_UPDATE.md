# City-wise Print Bill Update

## 🔄 Changes Made

### 1. **City-wise Bag Distribution Display**
- **Before**: City names and bags shown in colored boxes
- **After**: Professional table format with columns:
  - S.No (Serial Number)
  - City Name 
  - Bags (Total bags for that city)

### 2. **Payment Information Added**
- **Bank Details**: Bank name, account number, IFSC code
- **UPI Details**: Phone number for UPI payments, supported apps
- **QR Code**: Payment QR code with total amount
- **Contact Information**: Phone number for queries

## 📋 Updated Features

### Table Format
```
┌──────┬─────────────┬──────┐
│ S.No │ City Name   │ Bags │
├──────┼─────────────┼──────┤
│  1   │ Vijayawada  │  25  │
│  2   │ Hyderabad   │  18  │
│  3   │ Guntur      │  12  │
└──────┴─────────────┴──────┘
```

### Payment Section
- **Bank Transfer**: Complete bank details for NEFT/RTGS
- **UPI Payments**: Phone number for Paytm, PhonePe, GooglePay
- **QR Code**: Scan to pay the exact brokerage amount
- **Contact**: Phone number for payment queries

## 🔧 Technical Implementation

### New Repository Method
```java
@Query("SELECT " +
       "CASE WHEN lr.toBuyer.userId = :userId THEN sellerAddr.city ELSE buyerAddr.city END as cityName, " +
       "COALESCE(SUM(lr.quantity), 0) as totalBags " +
       "FROM LedgerRecord lr " +
       "JOIN lr.ledgerDetails ld " +
       "JOIN ld.fromSeller fs " +
       "JOIN fs.address sellerAddr " +
       "JOIN lr.toBuyer tb " +
       "JOIN tb.address buyerAddr " +
       "WHERE lr.broker.brokerId = :brokerId AND ld.financialYearId = :financialYearId " +
       "AND (lr.toBuyer.userId = :userId OR ld.fromSeller.userId = :userId) " +
       "GROUP BY CASE WHEN lr.toBuyer.userId = :userId THEN sellerAddr.city ELSE buyerAddr.city END " +
       "ORDER BY cityName")
List<Object[]> getUserCityWiseBagDistribution(@Param("brokerId") Long brokerId, 
                                            @Param("financialYearId") Long financialYearId, 
                                            @Param("userId") Long userId);
```

### Service Layer Updates
- Added city-wise bag distribution data fetching
- Integrated payment details from broker's bank information
- QR code generation with total brokerage amount

### PDF Generation Updates
- Replaced city boxes with professional table layout
- Added comprehensive payment information section
- Maintained existing print optimization and styling

## 🧪 Testing

### API Endpoint
```bash
curl "http://localhost:8080/BrokerHub/Brokerage/city-wise-print-bill/22/8?paperSize=a4&orientation=portrait"
```

### Expected Output
- Clean table showing city-wise bag distribution
- Complete payment information with bank details
- QR code for easy mobile payments
- Professional print-ready format

## ✅ Benefits

1. **Better Organization**: Table format is more professional and easier to read
2. **Complete Payment Info**: All payment options available in one place
3. **Mobile-Friendly**: QR code enables quick payments
4. **Print Optimized**: Maintains excellent print quality and layout
5. **Consistent Styling**: Matches existing bill design patterns

The city-wise print bill now provides a comprehensive view of transactions with professional presentation and complete payment facilitation.