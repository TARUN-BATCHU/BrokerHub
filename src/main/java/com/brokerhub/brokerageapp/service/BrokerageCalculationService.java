package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BrokerageCalculationService {

    private final UserRepository userRepository;
    private final LedgerRecordRepository ledgerRecordRepository;
    private final BrokeragePaymentRepository brokeragePaymentRepository;
    private final CurrentFinancialYearRepository currentFinancialYearRepository;
    private final BrokerageHistoryRepository brokerageHistoryRepository;
    private final FinancialYearRepository financialYearRepository;

    /**
     * Calculate and create/update brokerage payments for all merchants of a broker
     */
    public void calculateBrokerageForBroker(Long brokerId) {
        log.info("Starting brokerage calculation for broker: {}", brokerId);
        
        CurrentFinancialYear currentFY = currentFinancialYearRepository.findByBrokerId(brokerId)
                .orElseThrow(() -> new RuntimeException("Current financial year not found for broker: " + brokerId));

        FinancialYear financialYear = financialYearRepository.findById(currentFY.getFinancialYearId())
                .orElseThrow(() -> new RuntimeException("Financial year not found"));

        List<User> merchants = userRepository.findByBrokerBrokerId(brokerId);
        
        for (User merchant : merchants) {
            calculateBrokerageForMerchant(merchant, financialYear);
        }
        
        log.info("Completed brokerage calculation for broker: {}", brokerId);
    }

    /**
     * Calculate brokerage for a specific merchant
     */
    public void calculateBrokerageForMerchant(User merchant, FinancialYear financialYear) {
        log.debug("Calculating brokerage for merchant: {} in FY: {}", merchant.getFirmName(), financialYear.getFinancialYearName());
        
        // Get existing brokerage payment or create new one
        BrokeragePayment brokeragePayment = brokeragePaymentRepository
                .findByMerchantAndBrokerAndFinancialYear(
                        merchant.getUserId(), 
                        merchant.getBroker().getBrokerId(), 
                        financialYear.getYearId())
                .orElse(createNewBrokeragePayment(merchant, financialYear));

        // Calculate bags from ledger records
        Map<String, Long> bagCounts = calculateBagCounts(merchant, financialYear);
        Long soldBags = bagCounts.get("sold");
        Long boughtBags = bagCounts.get("bought");
        Long totalBags = soldBags + boughtBags;

        // Update brokerage payment
        updateBrokeragePayment(brokeragePayment, merchant, soldBags, boughtBags, totalBags);
        
        brokeragePaymentRepository.save(brokeragePayment);
        
        // Save to history for year-over-year comparison
        saveBrokerageHistory(merchant, financialYear, soldBags, boughtBags, totalBags, brokeragePayment.getNetBrokerage());
        
        log.debug("Updated brokerage for merchant: {} - Total bags: {}, Net brokerage: {}", 
                merchant.getFirmName(), totalBags, brokeragePayment.getNetBrokerage());
    }
    
    private void saveBrokerageHistory(User merchant, FinancialYear financialYear, Long soldBags, Long boughtBags, Long totalBags, BigDecimal netBrokerage) {
        BrokerageHistory history = brokerageHistoryRepository
                .findByMerchantUserIdAndBrokerBrokerIdAndFinancialYearYearId(
                        merchant.getUserId(), 
                        merchant.getBroker().getBrokerId(), 
                        financialYear.getYearId())
                .orElse(BrokerageHistory.builder()
                        .merchant(merchant)
                        .broker(merchant.getBroker())
                        .financialYear(financialYear)
                        .build());
        
        history.setSoldBags(soldBags);
        history.setBoughtBags(boughtBags);
        history.setTotalBags(totalBags);
        history.setTotalBrokerage(netBrokerage);
        
        brokerageHistoryRepository.save(history);
    }

    private BrokeragePayment createNewBrokeragePayment(User merchant, FinancialYear financialYear) {
        return BrokeragePayment.builder()
                .merchant(merchant)
                .broker(merchant.getBroker())
                .financialYear(financialYear)
                .soldBags(0L)
                .boughtBags(0L)
                .totalBags(0L)
                .brokerageRate(BigDecimal.valueOf(merchant.getBrokerageRate() != null ? merchant.getBrokerageRate() : 0))
                .grossBrokerage(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .tds(BigDecimal.ZERO)
                .netBrokerage(BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .pendingAmount(BigDecimal.ZERO)
                .status(PaymentStatus.PENDING)
                .dueDate(LocalDate.now().plusDays(30)) // Default 30 days due date
                .build();
    }

    private Map<String, Long> calculateBagCounts(User merchant, FinancialYear financialYear) {
        // Get all ledger records for this merchant in the financial year
        List<LedgerRecord> soldRecords = ledgerRecordRepository
                .findByFromSellerAndFinancialYear(merchant.getUserId(), financialYear.getYearId());
        
        List<LedgerRecord> boughtRecords = ledgerRecordRepository
                .findByToBuyerAndFinancialYear(merchant.getUserId(), financialYear.getYearId());

        Long soldBags = soldRecords.stream()
                .mapToLong(record -> record.getQuantity() != null ? record.getQuantity() : 0L)
                .sum();

        Long boughtBags = boughtRecords.stream()
                .mapToLong(record -> record.getQuantity() != null ? record.getQuantity() : 0L)
                .sum();

        return Map.of(
                "sold", soldBags,
                "bought", boughtBags
        );
    }

    private void updateBrokeragePayment(BrokeragePayment payment, User merchant, Long soldBags, Long boughtBags, Long totalBags) {
        payment.setSoldBags(soldBags);
        payment.setBoughtBags(boughtBags);
        payment.setTotalBags(totalBags);
        
        BigDecimal brokerageRate = BigDecimal.valueOf(merchant.getBrokerageRate() != null ? merchant.getBrokerageRate() : 0);
        payment.setBrokerageRate(brokerageRate);
        
        // Calculate gross brokerage
        BigDecimal grossBrokerage = brokerageRate.multiply(BigDecimal.valueOf(totalBags));
        payment.setGrossBrokerage(grossBrokerage);
        
        // Calculate discount (10% of gross brokerage)
        BigDecimal discount = grossBrokerage.multiply(BigDecimal.valueOf(0.10));
        payment.setDiscount(discount);
        
        // Calculate TDS (5% of gross brokerage)
        BigDecimal tds = grossBrokerage.multiply(BigDecimal.valueOf(0.05));
        payment.setTds(tds);
        
        // Calculate net brokerage
        BigDecimal netBrokerage = grossBrokerage.subtract(discount).subtract(tds);
        payment.setNetBrokerage(netBrokerage);
        
        // Update pending amount and status
        payment.calculatePendingAmount();
        payment.updateStatus();
    }
}