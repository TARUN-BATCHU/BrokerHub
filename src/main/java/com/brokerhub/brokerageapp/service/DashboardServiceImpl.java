package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.analytics.*;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.DashboardRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private FinancialYearRepository financialYearRepository;

    @Override
    @Cacheable(value = "financialYearAnalytics", key = "#financialYearId")
    public FinancialYearAnalyticsDTO getFinancialYearAnalytics(Long brokerId, Long financialYearId) {
        log.info("Generating analytics for financial year: {} and broker: {}", financialYearId, brokerId);

        // Get financial year details
        FinancialYear financialYear = financialYearRepository.findById(financialYearId)
                .orElseThrow(() -> new RuntimeException("Financial year not found"));

        // Get overall totals
        Object[] overallTotals = dashboardRepository.getOverallTotals(financialYearId);

        // Debug log to understand the structure
        if (overallTotals != null) {
            log.debug("Overall totals array length: {}", overallTotals.length);
            for (int i = 0; i < overallTotals.length; i++) {
                log.debug("overallTotals[{}]: {} (type: {})", i, overallTotals[i],
                    overallTotals[i] != null ? overallTotals[i].getClass().getSimpleName() : "null");
            }
        }

        // Get monthly analytics
        List<MonthlyAnalyticsDTO> monthlyAnalytics = buildMonthlyAnalytics(financialYearId);

        // Get overall product totals
        List<ProductAnalyticsDTO> overallProductTotals = buildOverallProductTotals(financialYearId);

        // Get overall city totals
        List<CityAnalyticsDTO> overallCityTotals = buildOverallCityTotals(financialYearId);

        // Get overall merchant type totals
        List<MerchantTypeAnalyticsDTO> overallMerchantTypeTotals = buildOverallMerchantTypeTotals(financialYearId);

        return FinancialYearAnalyticsDTO.builder()
                .financialYearId(financialYear.getYearId())
                .financialYearName(financialYear.getFinancialYearName())
                .startDate(financialYear.getStart())
                .endDate(financialYear.getEnd())
                .totalBrokerage(safeArrayAccess(overallTotals, 0, this::safeBigDecimalConvert, BigDecimal.ZERO))
                .totalQuantity(safeArrayAccess(overallTotals, 1, this::safeLongConvert, 0L))
                .totalTransactionValue(safeArrayAccess(overallTotals, 2, this::safeBigDecimalConvert, BigDecimal.ZERO))
                .totalTransactions(safeArrayAccess(overallTotals, 3, this::safeIntegerConvert, 0))
                .monthlyAnalytics(monthlyAnalytics)
                .overallProductTotals(overallProductTotals)
                .overallCityTotals(overallCityTotals)
                .overallMerchantTypeTotals(overallMerchantTypeTotals)
                .build();
    }

    private List<MonthlyAnalyticsDTO> buildMonthlyAnalytics(Long financialYearId) {
        List<Object[]> monthlyData = dashboardRepository.getMonthlyAnalytics(financialYearId);
        List<Object[]> productData = dashboardRepository.getProductAnalyticsByMonth(financialYearId);
        List<Object[]> cityData = dashboardRepository.getCityAnalyticsByMonth(financialYearId);
        List<Object[]> merchantTypeData = dashboardRepository.getMerchantTypeAnalyticsByMonth(financialYearId);

        Map<YearMonth, MonthlyAnalyticsDTO> monthlyMap = new HashMap<>();

        // Process monthly totals
        for (Object[] row : monthlyData) {
            Integer year = row[0] != null ? Integer.valueOf(row[0].toString()) : null;
            Integer month = row[1] != null ? Integer.valueOf(row[1].toString()) : null;
            if (year == null || month == null) continue;
            YearMonth yearMonth = YearMonth.of(year, month);

            MonthlyAnalyticsDTO monthlyDTO = MonthlyAnalyticsDTO.builder()
                    .month(yearMonth)
                    .monthName(yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year)
                    .totalBrokerage(safeBigDecimalConvert(row[2]))
                    .totalQuantity(safeLongConvert(row[3]))
                    .totalTransactionValue(safeBigDecimalConvert(row[4]))
                    .totalTransactions(safeIntegerConvert(row[5]))
                    .productAnalytics(new ArrayList<>())
                    .cityAnalytics(new ArrayList<>())
                    .merchantTypeAnalytics(new ArrayList<>())
                    .build();

            monthlyMap.put(yearMonth, monthlyDTO);
        }

        // Process product analytics by month
        Map<YearMonth, List<ProductAnalyticsDTO>> productByMonth = productData.stream()
                .filter(row -> row[0] != null && row[1] != null)
                .collect(Collectors.groupingBy(
                    row -> YearMonth.of(Integer.valueOf(row[0].toString()), Integer.valueOf(row[1].toString())),
                    Collectors.mapping(row -> ProductAnalyticsDTO.builder()
                            .productId(row[2] != null ? Long.valueOf(row[2].toString()) : null)
                            .productName(row[3] != null ? row[3].toString() : "")
                            .totalQuantity(row[4] != null ? Long.valueOf(row[4].toString()) : 0L)
                            .totalBrokerage(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                            .totalTransactionValue(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO)
                            .totalTransactions(row[7] != null ? Integer.valueOf(row[7].toString()) : 0)
                            .averagePrice(row[8] != null ? new BigDecimal(row[8].toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                            .averageBrokeragePerUnit(row[9] != null ? new BigDecimal(row[9].toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                            .build(), Collectors.toList())
                ));

        // Process city analytics by month
        Map<YearMonth, List<CityAnalyticsDTO>> cityByMonth = cityData.stream()
                .filter(row -> row[0] != null && row[1] != null)
                .collect(Collectors.groupingBy(
                    row -> YearMonth.of(Integer.valueOf(row[0].toString()), Integer.valueOf(row[1].toString())),
                    Collectors.mapping(row -> CityAnalyticsDTO.builder()
                            .cityName(row[2] != null ? row[2].toString() : "")
                            .totalQuantity(row[3] != null ? Long.valueOf(row[3].toString()) : 0L)
                            .totalBrokerage(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO)
                            .totalTransactionValue(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                            .totalTransactions(row[6] != null ? Integer.valueOf(row[6].toString()) : 0)
                            .totalSellers(row[7] != null ? Integer.valueOf(row[7].toString()) : 0)
                            .totalBuyers(row[8] != null ? Integer.valueOf(row[8].toString()) : 0)
                            .productBreakdown(new ArrayList<>())
                            .build(), Collectors.toList())
                ));

        // Process merchant type analytics by month
        Map<YearMonth, Map<String, MerchantTypeAnalyticsDTO>> merchantTypeByMonth = new HashMap<>();
        for (Object[] row : merchantTypeData) {
            if (row[0] == null || row[1] == null) continue;
            YearMonth yearMonth = YearMonth.of(Integer.valueOf(row[0].toString()), Integer.valueOf(row[1].toString()));
            String merchantType = row[2] != null ? row[2].toString() : "";

            merchantTypeByMonth.computeIfAbsent(yearMonth, k -> new HashMap<>())
                    .merge(merchantType, MerchantTypeAnalyticsDTO.builder()
                            .merchantType(merchantType)
                            .totalQuantitySold(row[3] != null ? Long.valueOf(row[3].toString()) : 0L)
                            .totalQuantityBought(row[4] != null ? Long.valueOf(row[4].toString()) : 0L)
                            .totalBrokeragePaid(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                            .totalTransactionValue(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO)
                            .totalTransactions(row[7] != null ? Integer.valueOf(row[7].toString()) : 0)
                            .totalMerchants(row[8] != null ? Integer.valueOf(row[8].toString()) : 0)
                            .build(),
                    (existing, newData) -> MerchantTypeAnalyticsDTO.builder()
                            .merchantType(existing.getMerchantType())
                            .totalQuantitySold(existing.getTotalQuantitySold() + newData.getTotalQuantitySold())
                            .totalQuantityBought(existing.getTotalQuantityBought() + newData.getTotalQuantityBought())
                            .totalBrokeragePaid(existing.getTotalBrokeragePaid().add(newData.getTotalBrokeragePaid()))
                            .totalTransactionValue(existing.getTotalTransactionValue().add(newData.getTotalTransactionValue()))
                            .totalTransactions(existing.getTotalTransactions() + newData.getTotalTransactions())
                            .totalMerchants(Math.max(existing.getTotalMerchants(), newData.getTotalMerchants()))
                            .build());
        }

        // Combine all data
        for (Map.Entry<YearMonth, MonthlyAnalyticsDTO> entry : monthlyMap.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            MonthlyAnalyticsDTO monthlyDTO = entry.getValue();

            monthlyDTO.setProductAnalytics(productByMonth.getOrDefault(yearMonth, new ArrayList<>()));
            monthlyDTO.setCityAnalytics(cityByMonth.getOrDefault(yearMonth, new ArrayList<>()));
            monthlyDTO.setMerchantTypeAnalytics(new ArrayList<>(merchantTypeByMonth.getOrDefault(yearMonth, new HashMap<>()).values()));

            // Add product breakdown for each city
            for (CityAnalyticsDTO cityDTO : monthlyDTO.getCityAnalytics()) {
                List<Object[]> cityProductData = dashboardRepository.getProductBreakdownForCityAndMonth(
                        financialYearId, yearMonth.getYear(), yearMonth.getMonthValue(), cityDTO.getCityName());

                List<ProductAnalyticsDTO> cityProducts = cityProductData.stream()
                        .map(row -> ProductAnalyticsDTO.builder()
                                .productId(row[0] != null ? Long.valueOf(row[0].toString()) : null)
                                .productName(row[1] != null ? row[1].toString() : "")
                                .totalQuantity(row[2] != null ? Long.valueOf(row[2].toString()) : 0L)
                                .totalBrokerage(row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO)
                                .totalTransactionValue(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO)
                                .totalTransactions(row[5] != null ? Integer.valueOf(row[5].toString()) : 0)
                                .averagePrice(row[6] != null ? new BigDecimal(row[6].toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                                .averageBrokeragePerUnit(row[7] != null ? new BigDecimal(row[7].toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                                .build())
                        .collect(Collectors.toList());

                cityDTO.setProductBreakdown(cityProducts);
            }
        }

        return monthlyMap.values().stream()
                .sorted(Comparator.comparing(MonthlyAnalyticsDTO::getMonth))
                .collect(Collectors.toList());
    }

    private List<ProductAnalyticsDTO> buildOverallProductTotals(Long financialYearId) {
        List<Object[]> productData = dashboardRepository.getProductAnalyticsByMonth(financialYearId);

        Map<Long, ProductAnalyticsDTO> productMap = new HashMap<>();

        for (Object[] row : productData) {
            Long productId = row[2] != null ? Long.valueOf(row[2].toString()) : null;
            String productName = row[3] != null ? row[3].toString() : "";

            productMap.merge(productId, ProductAnalyticsDTO.builder()
                    .productId(productId)
                    .productName(productName)
                    .totalQuantity(row[4] != null ? Long.valueOf(row[4].toString()) : 0L)
                    .totalBrokerage(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                    .totalTransactionValue(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO)
                    .totalTransactions(row[7] != null ? Integer.valueOf(row[7].toString()) : 0)
                    .averagePrice(row[8] != null ? new BigDecimal(row[8].toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .averageBrokeragePerUnit(row[9] != null ? new BigDecimal(row[9].toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .build(),
                (existing, newData) -> ProductAnalyticsDTO.builder()
                    .productId(existing.getProductId())
                    .productName(existing.getProductName())
                    .totalQuantity(existing.getTotalQuantity() + newData.getTotalQuantity())
                    .totalBrokerage(existing.getTotalBrokerage().add(newData.getTotalBrokerage()))
                    .totalTransactionValue(existing.getTotalTransactionValue().add(newData.getTotalTransactionValue()))
                    .totalTransactions(existing.getTotalTransactions() + newData.getTotalTransactions())
                    .averagePrice(calculateAveragePrice(existing.getTotalTransactionValue().add(newData.getTotalTransactionValue()),
                        existing.getTotalQuantity() + newData.getTotalQuantity()))
                    .averageBrokeragePerUnit(calculateAverageBrokerage(existing.getTotalBrokerage().add(newData.getTotalBrokerage()),
                        existing.getTotalQuantity() + newData.getTotalQuantity()))
                    .build());
        }

        return productMap.values().stream()
                .sorted(Comparator.comparing(ProductAnalyticsDTO::getProductName))
                .collect(Collectors.toList());
    }

    private List<CityAnalyticsDTO> buildOverallCityTotals(Long financialYearId) {
        List<Object[]> cityData = dashboardRepository.getCityAnalyticsByMonth(financialYearId);

        Map<String, CityAnalyticsDTO> cityMap = new HashMap<>();

        for (Object[] row : cityData) {
            String cityName = row[2] != null ? row[2].toString() : "";

            cityMap.merge(cityName, CityAnalyticsDTO.builder()
                    .cityName(cityName)
                    .totalQuantity(row[3] != null ? Long.valueOf(row[3].toString()) : 0L)
                    .totalBrokerage(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO)
                    .totalTransactionValue(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                    .totalTransactions(row[6] != null ? Integer.valueOf(row[6].toString()) : 0)
                    .totalSellers(row[7] != null ? Integer.valueOf(row[7].toString()) : 0)
                    .totalBuyers(row[8] != null ? Integer.valueOf(row[8].toString()) : 0)
                    .productBreakdown(new ArrayList<>())
                    .build(),
                (existing, newData) -> CityAnalyticsDTO.builder()
                    .cityName(existing.getCityName())
                    .totalQuantity(existing.getTotalQuantity() + newData.getTotalQuantity())
                    .totalBrokerage(existing.getTotalBrokerage().add(newData.getTotalBrokerage()))
                    .totalTransactionValue(existing.getTotalTransactionValue().add(newData.getTotalTransactionValue()))
                    .totalTransactions(existing.getTotalTransactions() + newData.getTotalTransactions())
                    .totalSellers(Math.max(existing.getTotalSellers(), newData.getTotalSellers()))
                    .totalBuyers(Math.max(existing.getTotalBuyers(), newData.getTotalBuyers()))
                    .productBreakdown(existing.getProductBreakdown())
                    .build());
        }

        return cityMap.values().stream()
                .sorted(Comparator.comparing(CityAnalyticsDTO::getCityName))
                .collect(Collectors.toList());
    }

    private List<MerchantTypeAnalyticsDTO> buildOverallMerchantTypeTotals(Long financialYearId) {
        List<Object[]> merchantTypeData = dashboardRepository.getMerchantTypeAnalyticsByMonth(financialYearId);

        Map<String, MerchantTypeAnalyticsDTO> merchantTypeMap = new HashMap<>();

        for (Object[] row : merchantTypeData) {
            String merchantType = row[2] != null ? row[2].toString() : "";

            merchantTypeMap.merge(merchantType, MerchantTypeAnalyticsDTO.builder()
                    .merchantType(merchantType)
                    .totalQuantitySold(row[3] != null ? Long.valueOf(row[3].toString()) : 0L)
                    .totalQuantityBought(row[4] != null ? Long.valueOf(row[4].toString()) : 0L)
                    .totalBrokeragePaid(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                    .totalTransactionValue(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO)
                    .totalTransactions(row[7] != null ? Integer.valueOf(row[7].toString()) : 0)
                    .totalMerchants(row[8] != null ? Integer.valueOf(row[8].toString()) : 0)
                    .build(),
                (existing, newData) -> MerchantTypeAnalyticsDTO.builder()
                    .merchantType(existing.getMerchantType())
                    .totalQuantitySold(existing.getTotalQuantitySold() + newData.getTotalQuantitySold())
                    .totalQuantityBought(existing.getTotalQuantityBought() + newData.getTotalQuantityBought())
                    .totalBrokeragePaid(existing.getTotalBrokeragePaid().add(newData.getTotalBrokeragePaid()))
                    .totalTransactionValue(existing.getTotalTransactionValue().add(newData.getTotalTransactionValue()))
                    .totalTransactions(existing.getTotalTransactions() + newData.getTotalTransactions())
                    .totalMerchants(Math.max(existing.getTotalMerchants(), newData.getTotalMerchants()))
                    .build());
        }

        return merchantTypeMap.values().stream()
                .sorted(Comparator.comparing(MerchantTypeAnalyticsDTO::getMerchantType))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"financialYearAnalytics", "topPerformers", "topBuyers", "topSellers", "topMerchants"}, key = "#financialYearId")
    public void refreshAnalyticsCache(Long financialYearId) {
        log.info("Refreshing analytics cache for financial year: {}", financialYearId);
    }

    @Override
    @CacheEvict(value = {"financialYearAnalytics", "topPerformers", "topBuyers", "topSellers", "topMerchants"}, allEntries = true)
    public void refreshAllAnalyticsCache() {
        log.info("Refreshing all analytics cache");
    }

    private BigDecimal calculateAveragePrice(BigDecimal totalValue, Long totalQuantity) {
        if (totalQuantity == null || totalQuantity == 0) {
            return BigDecimal.ZERO;
        }
        return totalValue.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAverageBrokerage(BigDecimal totalBrokerage, Long totalQuantity) {
        if (totalQuantity == null || totalQuantity == 0) {
            return BigDecimal.ZERO;
        }
        return totalBrokerage.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Cacheable(value = "topPerformers", key = "#financialYearId")
    public TopPerformersDTO getTopPerformers(Long brokerId, Long financialYearId) {
        log.info("Generating top performers for financial year: {} and broker: {}", financialYearId, brokerId);

        try {
            // Get financial year details
            FinancialYear financialYear = financialYearRepository.findById(financialYearId)
                    .orElseThrow(() -> new RuntimeException("Financial year not found"));

            // Get top performers
            List<TopBuyerDTO> topBuyers = getTop5BuyersByQuantity(brokerId, financialYearId);
            List<TopSellerDTO> topSellers = getTop5SellersByQuantity(brokerId, financialYearId);
            List<TopMerchantByBrokerageDTO> topMerchants = getTop5MerchantsByBrokerage(brokerId, financialYearId);

            return TopPerformersDTO.builder()
                    .financialYearId(financialYear.getYearId())
                    .financialYearName(financialYear.getFinancialYearName())
                    .topBuyersByQuantity(topBuyers)
                    .topSellersByQuantity(topSellers)
                    .topMerchantsByBrokerage(topMerchants)
                    .build();
        } catch (Exception e) {
            log.error("Error in getTopPerformers", e);
            throw e;
        }
    }

    @Override
    @Cacheable(value = "topBuyers", key = "#financialYearId")
    public List<TopBuyerDTO> getTop5BuyersByQuantity(Long brokerId, Long financialYearId) {
        log.info("Getting top 5 buyers by quantity for financial year: {}", financialYearId);

        List<Object[]> buyersData = dashboardRepository.getTop5BuyersByQuantity(financialYearId);

        return buyersData.stream()
                .map(row -> TopBuyerDTO.builder()
                        .buyerId(row[0] != null ? safeLongConvert(row[0]) : null)
                        .buyerName(row[1] != null ? row[1].toString() : "")
                        .firmName(row[2] != null ? row[2].toString() : "")
                        .city(row[3] != null ? row[3].toString() : "")
                        .userType(row[4] != null ? row[4].toString() : "")
                        .totalQuantityBought(safeLongConvert(row[5]))
                        .totalAmountSpent(safeBigDecimalConvert(row[6]))
                        .totalBrokeragePaid(safeBigDecimalConvert(row[7]))
                        .totalTransactions(safeIntegerConvert(row[8]))
                        .averageTransactionSize(safeBigDecimalConvert(row[9]).setScale(2, RoundingMode.HALF_UP))
                        .phoneNumber(row[10] != null ? row[10].toString() : "")
                        .email(row[11] != null ? row[11].toString() : "")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "topSellers", key = "#financialYearId")
    public List<TopSellerDTO> getTop5SellersByQuantity(Long brokerId, Long financialYearId) {
        log.info("Getting top 5 sellers by quantity for financial year: {}", financialYearId);

        List<Object[]> sellersData = dashboardRepository.getTop5SellersByQuantity(financialYearId);

        return sellersData.stream()
                .map(row -> TopSellerDTO.builder()
                        .sellerId(row[0] != null ? safeLongConvert(row[0]) : null)
                        .sellerName(row[1] != null ? row[1].toString() : "")
                        .firmName(row[2] != null ? row[2].toString() : "")
                        .city(row[3] != null ? row[3].toString() : "")
                        .userType(row[4] != null ? row[4].toString() : "")
                        .totalQuantitySold(safeLongConvert(row[5]))
                        .totalAmountReceived(safeBigDecimalConvert(row[6]))
                        .totalBrokerageGenerated(safeBigDecimalConvert(row[7]))
                        .totalTransactions(safeIntegerConvert(row[8]))
                        .averageTransactionSize(safeBigDecimalConvert(row[9]).setScale(2, RoundingMode.HALF_UP))
                        .phoneNumber(row[10] != null ? row[10].toString() : "")
                        .email(row[11] != null ? row[11].toString() : "")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "topMerchants", key = "#financialYearId")
    public List<TopMerchantByBrokerageDTO> getTop5MerchantsByBrokerage(Long brokerId, Long financialYearId) {
        log.info("Getting top 5 merchants by brokerage for financial year: {}", financialYearId);

        List<Object[]> merchantsData = dashboardRepository.getTop5MerchantsByBrokerage(financialYearId);

        return merchantsData.stream()
                .map(row -> {
                    Long totalQuantityTraded = safeLongConvert(row[6]);
                    Integer totalTransactions = safeIntegerConvert(row[10]);
                    BigDecimal totalBrokeragePaid = safeBigDecimalConvert(row[5]);

                    return TopMerchantByBrokerageDTO.builder()
                            .merchantId(row[0] != null ? safeLongConvert(row[0]) : null)
                            .merchantName(row[1] != null ? row[1].toString() : "")
                            .firmName(row[2] != null ? row[2].toString() : "")
                            .city(row[3] != null ? row[3].toString() : "")
                            .userType(row[4] != null ? row[4].toString() : "")
                            .totalBrokeragePaid(totalBrokeragePaid)
                            .totalQuantityTraded(totalQuantityTraded)
                            .totalQuantityBought(safeLongConvert(row[7]))
                            .totalQuantitySold(safeLongConvert(row[8]))
                            .totalAmountTraded(safeBigDecimalConvert(row[9]))
                            .totalTransactions(totalTransactions)
                            .averageBrokeragePerTransaction(calculateAverageBrokeragePerTransaction(totalBrokeragePaid, totalTransactions))
                            .averageBrokeragePerUnit(calculateAverageBrokerage(totalBrokeragePaid, totalQuantityTraded))
                            .phoneNumber(row[11] != null ? row[11].toString() : "")
                            .email(row[12] != null ? row[12].toString() : "")
                            .build();
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateAverageBrokeragePerTransaction(BigDecimal totalBrokerage, Integer totalTransactions) {
        if (totalTransactions == null || totalTransactions == 0) {
            return BigDecimal.ZERO;
        }
        return totalBrokerage.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP);
    }

    // Helper method to safely convert database values
    private Integer safeIntegerConvert(Object value) {
        if (value == null) return 0;
        try {
            String stringValue = value.toString().trim();
            // Handle arrays
            if (stringValue.startsWith("[") && stringValue.endsWith("]")) {
                log.warn("Received array instead of single value for Integer conversion: {}", value);
                return 0;
            }
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException e) {
            log.warn("Failed to convert value to Integer: {}, returning 0", value);
            return 0;
        }
    }

    private Long safeLongConvert(Object value) {
        if (value == null) return 0L;
        try {
            String stringValue = value.toString().trim();
            // Handle arrays
            if (stringValue.startsWith("[") && stringValue.endsWith("]")) {
                log.warn("Received array instead of single value for Long conversion: {}", value);
                return 0L;
            }
            return Long.valueOf(stringValue);
        } catch (NumberFormatException e) {
            log.warn("Failed to convert value to Long: {}, returning 0", value);
            return 0L;
        }
    }

    private BigDecimal safeBigDecimalConvert(Object value) {
        if (value == null) return BigDecimal.ZERO;
        try {
            String stringValue = value.toString().trim();
            // Handle empty strings
            if (stringValue.isEmpty()) return BigDecimal.ZERO;
            // Handle arrays (in case the value is an array)
            if (stringValue.startsWith("[") && stringValue.endsWith("]")) {
                log.warn("Received array instead of single value for BigDecimal conversion: {}", value);
                return BigDecimal.ZERO;
            }
            // Handle scientific notation by converting to double first, then to BigDecimal
            if (stringValue.toLowerCase().contains("e")) {
                return BigDecimal.valueOf(Double.parseDouble(stringValue));
            }
            return new BigDecimal(stringValue);
        } catch (NumberFormatException e) {
            log.warn("Failed to convert value to BigDecimal: {}, returning ZERO", value);
            return BigDecimal.ZERO;
        }
    }

    // Safe array access method with function converter
    private <T> T safeArrayAccess(Object[] array, int index, java.util.function.Function<Object, T> converter, T defaultValue) {
        if (array == null || index >= array.length || index < 0) {
            log.debug("Array access out of bounds or null array. Array length: {}, requested index: {}",
                array != null ? array.length : "null", index);
            return defaultValue;
        }
        try {
            return converter.apply(array[index]);
        } catch (Exception e) {
            log.warn("Error converting array element at index {}: {}", index, array[index], e);
            return defaultValue;
        }
    }
}