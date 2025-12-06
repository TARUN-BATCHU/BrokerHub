package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerageSummaryDTO;
import com.brokerhub.brokerageapp.dto.CityWiseBagDistributionDTO;
import com.brokerhub.brokerageapp.dto.UserBrokerageDetailDTO;
import com.brokerhub.brokerageapp.entity.GeneratedDocument;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.repository.BrokerageRepository;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.GeneratedDocumentRepository;
import com.brokerhub.brokerageapp.repository.UserBrokerageRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BrokerageServiceImpl implements BrokerageService {
    
    @Autowired
    private BrokerageRepository brokerageRepository;
    
    @Autowired
    private UserBrokerageRepository userBrokerageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TenantContextService tenantContextService;
    
    @Autowired
    private CurrentFinancialYearService currentFinancialYearService;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;
    
    @Autowired
    private BulkBillGenerationService bulkBillGenerationService;
    
    @Autowired
    private BrokerRepository brokerRepository;
    
    @Autowired
    private ExcelGenerationService excelGenerationService;
    
    @Autowired
    private GeneratedDocumentRepository documentRepository;
    
    @Override
    @Cacheable(value = "totalBrokerage", key = "#brokerId + '_' + #financialYearId")
    public BigDecimal getTotalBrokerageInFinancialYear(Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        Number totalBrokerage = brokerageRepository.getTotalBrokerageByBrokerAndFinancialYear(currentBrokerId, financialYearId);
        return BigDecimal.valueOf(totalBrokerage != null ? totalBrokerage.longValue() : 0L);
    }
    
    @Override
    @CacheEvict(value = {"brokerageSummary", "totalBrokerage", "brokerageQuery"}, allEntries = true)
    @Cacheable(value = "brokerageSummary", key = "#brokerId + '_' + #financialYearId")
    public BrokerageSummaryDTO getBrokerageSummaryInFinancialYear(Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        Number totalBrokerageAmount = brokerageRepository.getTotalBrokerageByBrokerAndFinancialYear(currentBrokerId, financialYearId);
        Number brokerageFromSellersAmount = brokerageRepository.getTotalBrokerageFromSellers(currentBrokerId, financialYearId);
        Number brokerageFromBuyersAmount = brokerageRepository.getTotalBrokerageFromBuyers(currentBrokerId, financialYearId);
        
        BigDecimal totalBrokerage = BigDecimal.valueOf(totalBrokerageAmount != null ? totalBrokerageAmount.longValue() : 0L);
        BigDecimal brokerageFromSellers = BigDecimal.valueOf(brokerageFromSellersAmount != null ? brokerageFromSellersAmount.longValue() : 0L);
        BigDecimal brokerageFromBuyers = BigDecimal.valueOf(brokerageFromBuyersAmount != null ? brokerageFromBuyersAmount.longValue() : 0L);
        
        List<Object[]> cityData = brokerageRepository.getCityWiseBrokerage(currentBrokerId, financialYearId);
        List<BrokerageSummaryDTO.CityBrokerageDTO> cityBrokerage = cityData.stream()
                .map(row -> BrokerageSummaryDTO.CityBrokerageDTO.builder()
                        .city((String) row[0])
                        .totalBrokerage(BigDecimal.valueOf(((Number) row[1]).longValue()))
                        .build())
                .collect(Collectors.toList());
        
        List<Object[]> productData = brokerageRepository.getProductWiseBrokerage(currentBrokerId, financialYearId);
        List<BrokerageSummaryDTO.ProductBrokerageDTO> productBrokerage = productData.stream()
                .map(row -> BrokerageSummaryDTO.ProductBrokerageDTO.builder()
                        .productName((String) row[0])
                        .totalBrokerage(BigDecimal.valueOf(((Number) row[1]).longValue()))
                        .build())
                .collect(Collectors.toList());
        
        return BrokerageSummaryDTO.builder()
                .totalBrokerageEarned(totalBrokerage)
                .totalBrokerageFromSellers(brokerageFromSellers)
                .totalBrokerageFromBuyers(brokerageFromBuyers)
                .cityWiseBrokerage(cityBrokerage)
                .productWiseBrokerage(productBrokerage)
                .build();
    }
    
    @Override
    @Cacheable(value = "userBrokerage", key = "#brokerId + '_' + #userId + '_' + #financialYearId")
    public BigDecimal getUserTotalBrokerageInFinancialYear(Long userId, Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        Number userBrokerage = brokerageRepository.getUserTotalBrokerage(currentBrokerId, financialYearId, userId);
        return BigDecimal.valueOf(userBrokerage != null ? userBrokerage.longValue() : 0L);
    }
    
    @Override
    @Cacheable(value = "cityBrokerage", key = "#brokerId + '_' + #city + '_' + #financialYearId")
    public BigDecimal getCityTotalBrokerageInFinancialYear(String city, Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        Number cityBrokerage = brokerageRepository.getCityTotalBrokerage(currentBrokerId, financialYearId, city);
        return BigDecimal.valueOf(cityBrokerage != null ? cityBrokerage.longValue() : 0L);
    }
    
    @Override
    @Cacheable(value = "userBrokerageDetail", key = "#brokerId + '_' + #userId + '_' + #financialYearId")
    public UserBrokerageDetailDTO getUserBrokerageDetailInFinancialYear(Long userId, Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();
        
        // Basic user info
        UserBrokerageDetailDTO.UserBasicInfo basicInfo = UserBrokerageDetailDTO.UserBasicInfo.builder()
                .firmName(user.getFirmName())
                .ownerName(user.getOwnerName())
                .city(user.getAddress() != null ? user.getAddress().getCity() : null)
                .build();
        
        // Brokerage summary
        Long totalBagsSold = userBrokerageRepository.getUserTotalBagsSold(currentBrokerId, financialYearId, userId);
        Long totalBagsBought = userBrokerageRepository.getUserTotalBagsBought(currentBrokerId, financialYearId, userId);
        
        List<Object[]> productsBoughtData = userBrokerageRepository.getUserProductsBought(currentBrokerId, financialYearId, userId);
        List<UserBrokerageDetailDTO.ProductSummary> productsBought = productsBoughtData.stream()
                .map(row -> UserBrokerageDetailDTO.ProductSummary.builder()
                        .productName((String) row[0])
                        .totalBags(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
        
        List<Object[]> productsSoldData = userBrokerageRepository.getUserProductsSold(currentBrokerId, financialYearId, userId);
        List<UserBrokerageDetailDTO.ProductSummary> productsSold = productsSoldData.stream()
                .map(row -> UserBrokerageDetailDTO.ProductSummary.builder()
                        .productName((String) row[0])
                        .totalBags(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
        
        List<Object[]> citiesSoldToData = userBrokerageRepository.getUserCitiesSoldTo(currentBrokerId, financialYearId, userId);
        List<UserBrokerageDetailDTO.CitySummary> citiesSoldTo = citiesSoldToData.stream()
                .map(row -> UserBrokerageDetailDTO.CitySummary.builder()
                        .city((String) row[0])
                        .totalBags(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
        
        List<Object[]> citiesBoughtFromData = userBrokerageRepository.getUserCitiesBoughtFrom(currentBrokerId, financialYearId, userId);
        List<UserBrokerageDetailDTO.CitySummary> citiesBoughtFrom = citiesBoughtFromData.stream()
                .map(row -> UserBrokerageDetailDTO.CitySummary.builder()
                        .city((String) row[0])
                        .totalBags(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
        
        Number totalBrokeragePayableAmount = brokerageRepository.getUserTotalBrokerage(currentBrokerId, financialYearId, userId);
        BigDecimal totalBrokeragePayable = BigDecimal.valueOf(totalBrokeragePayableAmount != null ? totalBrokeragePayableAmount.longValue() : 0L);
        Long totalAmountEarned = userBrokerageRepository.getUserTotalAmountEarned(currentBrokerId, financialYearId, userId);
        Long totalAmountPaid = userBrokerageRepository.getUserTotalAmountPaid(currentBrokerId, financialYearId, userId);
        
        UserBrokerageDetailDTO.BrokerageSummary brokerageSummary = UserBrokerageDetailDTO.BrokerageSummary.builder()
                .totalBagsSold(totalBagsSold)
                .totalBagsBought(totalBagsBought)
                .productsBought(productsBought)
                .productsSold(productsSold)
                .citiesSoldTo(citiesSoldTo)
                .citiesBoughtFrom(citiesBoughtFrom)
                .totalBrokeragePayable(totalBrokeragePayable)
                .totalAmountEarned(totalAmountEarned)
                .totalAmountPaid(totalAmountPaid)
                .build();
        
        // Transaction details
        List<Object[]> transactionData = userBrokerageRepository.getUserTransactionDetails(currentBrokerId, financialYearId, userId);
        List<UserBrokerageDetailDTO.TransactionDetail> transactionDetails = transactionData.stream()
                .map(row -> UserBrokerageDetailDTO.TransactionDetail.builder()
                        .transactionNumber(((Number) row[0]).longValue())
                        .transactionDate((LocalDate) row[1])
                        .counterPartyFirmName((String) row[2])
                        .productName((String) row[3])
                        .productCost(((Number) row[4]).longValue())
                        .quantity(((Number) row[5]).longValue())
                        .brokerage(BigDecimal.valueOf(((Number) row[6]).longValue()))
                        .transactionType((String) row[7])
                        .build())
                .collect(Collectors.toList());
        
        return UserBrokerageDetailDTO.builder()
                .userBasicInfo(basicInfo)
                .brokerageSummary(brokerageSummary)
                .transactionDetails(transactionDetails)
                .build();
    }
    
    @Override
    public byte[] generateUserBrokerageBill(Long userId, Long brokerId, Long financialYearId) {
        return generateUserBrokerageBill(userId, brokerId, financialYearId, null);
    }
    
    @Override
    public byte[] generateUserBrokerageBill(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        UserBrokerageDetailDTO userDetail = getUserBrokerageDetailInFinancialYear(userId, brokerId, financialYearId);
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found");
        }
        
        if (customBrokerage != null) {
            return pdfGenerationService.generateUserBrokerageBill(userDetail, brokerOpt.get(), financialYearId, customBrokerage, userId);
        }
        return pdfGenerationService.generateUserBrokerageBill(userDetail, brokerOpt.get(), financialYearId, userId);
    }
    
    @Override
    public byte[] generateUserBrokerageBillPdf(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        UserBrokerageDetailDTO userDetail = getUserBrokerageDetailInFinancialYear(userId, brokerId, financialYearId);
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found");
        }
        
        return pdfGenerationService.generateUserBrokerageBillPdf(userDetail, brokerOpt.get(), financialYearId, customBrokerage);
    }
    
    @Override
    public byte[] generatePrintOptimizedBill(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage, String paperSize, String orientation) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        UserBrokerageDetailDTO userDetail = getUserBrokerageDetailInFinancialYear(userId, brokerId, financialYearId);
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found");
        }
        
        return pdfGenerationService.generatePrintOptimizedBill(userDetail, brokerOpt.get(), financialYearId, customBrokerage, paperSize, orientation);
    }
    
    @Override
    public byte[] generateUserBrokerageExcel(Long userId, Long brokerId, Long financialYearId) {
        return generateUserBrokerageExcel(userId, brokerId, financialYearId, null);
    }
    
    @Override
    public byte[] generateUserBrokerageExcel(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        UserBrokerageDetailDTO userDetail = getUserBrokerageDetailInFinancialYear(userId, brokerId, financialYearId);
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found");
        }
        
        if (customBrokerage != null) {
            return excelGenerationService.generateUserBrokerageExcel(userDetail, brokerOpt.get(), financialYearId, customBrokerage);
        }
        return excelGenerationService.generateUserBrokerageExcel(userDetail, brokerOpt.get(), financialYearId);
    }
    
    @Override
    public byte[] generateBrokerageSummaryExcel(Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        BrokerageSummaryDTO summary = getBrokerageSummaryInFinancialYear(brokerId, financialYearId);
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found");
        }
        
        return excelGenerationService.generateBrokerageSummaryExcel(summary, brokerOpt.get(), financialYearId);
    }
    
    @Override
    public byte[] generateCityBrokerageExcel(String city, Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        final Long finalBrokerId = brokerId;
        final Long finalFinancialYearId = financialYearId;
        
        List<User> cityUsers = userRepository.findByBrokerBrokerIdAndAddressCity(currentBrokerId, city);
        List<UserBrokerageDetailDTO> cityUserDetails = cityUsers.stream()
                .map(user -> getUserBrokerageDetailInFinancialYear(user.getUserId(), finalBrokerId, finalFinancialYearId))
                .collect(Collectors.toList());
        
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found");
        }
        
        return excelGenerationService.generateCityBrokerageExcel(city, cityUserDetails, brokerOpt.get(), financialYearId);
    }
    
    @Override
    public byte[] generateBulkBillsHtml(List<Long> userIds, Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found: " + currentBrokerId);
        }
        
        return bulkBillGenerationService.generateBulkBillsHtmlSync(userIds, brokerOpt.get(), financialYearId);
    }
    
    @Override
    public byte[] generateBulkBillsExcel(List<Long> userIds, Long brokerId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found: " + currentBrokerId);
        }
        
        return bulkBillGenerationService.generateBulkBillsExcelSync(userIds, brokerOpt.get(), financialYearId);
    }
    

    @Override
    public String generateExcelFilename(Long userId, Long financialYearId) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return "brokerage-bill-" + userId + ".xlsx";
        }
        
        User user = userOpt.get();
        String firmName = user.getFirmName();
        
        // Clean the firm name for filename (remove special characters)
        String cleanFirmName = firmName != null ? 
            firmName.replaceAll("[^a-zA-Z0-9\\s-_]", "").replaceAll("\\s+", "-") : 
            "Unknown-Firm";
        
        return cleanFirmName + "-brokerage-bill-FY" + financialYearId + ".xlsx";
    }
    
    @Override
    public byte[] generateCityWisePrintBill(Long userId, Long brokerId, Long financialYearId, BigDecimal customBrokerage, String paperSize, String orientation) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
        }
        
        UserBrokerageDetailDTO userDetail = getUserBrokerageDetailInFinancialYear(userId, brokerId, financialYearId);
        Optional<com.brokerhub.brokerageapp.entity.Broker> brokerOpt = brokerRepository.findById(currentBrokerId);
        if (!brokerOpt.isPresent()) {
            throw new RuntimeException("Broker not found");
        }
        
        // Get city-wise bag distribution for the user
        List<Object[]> cityBagData = userBrokerageRepository.getUserCityWiseBagDistribution(currentBrokerId, financialYearId, userId);
        List<CityWiseBagDistributionDTO> cityDistribution = cityBagData.stream()
                .map(row -> new CityWiseBagDistributionDTO(
                        (String) row[0], // city name
                        ((Number) row[1]).longValue() // total bags
                ))
                .collect(Collectors.toList());
        
        return pdfGenerationService.generateCityWisePrintBill(userDetail, brokerOpt.get(), financialYearId, customBrokerage, paperSize, orientation, cityDistribution);
    }

}
