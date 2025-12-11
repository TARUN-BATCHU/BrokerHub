package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.dashboard.*;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.exception.ResourceNotFoundException;
import com.brokerhub.brokerageapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BrokerageDashboardServiceImpl implements BrokerageDashboardService {

    private final BrokeragePaymentRepository brokeragePaymentRepository;
    private final PartPaymentRepository partPaymentRepository;
    private final UserRepository userRepository;
    private final BrokerRepository brokerRepository;
    private final BrokerageCalculationService brokerageCalculationService;
    private final BrokerageHistoryRepository brokerageHistoryRepository;
    private final FinancialYearRepository financialYearRepository;

    @Override
    @Transactional(readOnly = true)
    public BrokerageDashboardDTO getBrokerageDashboard(Long brokerId) {
        validateBroker(brokerId);
        
        List<BrokeragePayment> payments = brokeragePaymentRepository.findByBrokerBrokerId(brokerId);
        
        BrokerageDashboardDTO.BrokerageSummary summary = calculateSummary(payments);
        List<MerchantBrokerageDTO> merchants = convertToMerchantDTOs(payments);
        List<BrokerageDashboardDTO.BrokerageChartData> chartData = generateChartData(payments);
        
        return BrokerageDashboardDTO.builder()
                .summary(summary)
                .merchants(merchants)
                .chartData(chartData)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantBrokerageDTO> getMerchantsBrokerage(Long brokerId) {
        validateBroker(brokerId);
        
        List<BrokeragePayment> payments = brokeragePaymentRepository.findByBrokerBrokerId(brokerId);
        return convertToMerchantDTOs(payments);
    }

    @Override
    public void updatePaymentStatus(Long brokerId, UpdatePaymentStatusRequestDTO request) {
        validateBroker(brokerId);
        
        BrokeragePayment payment = brokeragePaymentRepository
                .findByBrokerIdAndMerchantUserId(brokerId, request.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Brokerage payment not found"));

        if (request.getStatus() == PaymentStatus.PARTIAL_PAID && request.getPaidAmount() != null) {
            addPartialPayment(payment, request);
        } else if (request.getStatus() == PaymentStatus.PAID) {
            markAsFullyPaid(payment, request);
        }
        
        payment.setStatus(request.getStatus());
        brokeragePaymentRepository.save(payment);
    }

    @Override
    public void updateBrokerageAmount(Long brokerId, UpdateBrokerageRequestDTO request) {
        validateBroker(brokerId);
        
        BrokeragePayment payment = brokeragePaymentRepository
                .findByBrokerIdAndMerchantUserId(brokerId, request.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Brokerage payment not found"));

        payment.setNetBrokerage(request.getNewBrokerageAmount());
        payment.setNotes(request.getReason());
        payment.calculatePendingAmount();
        payment.updateStatus();
        
        brokeragePaymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantBrokerageDTO.PaymentHistoryDTO> getPaymentHistory(Long brokerId, Long merchantId) {
        validateBroker(brokerId);
        
        BrokeragePayment payment = brokeragePaymentRepository
                .findByBrokerIdAndMerchantUserId(brokerId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Brokerage payment not found"));

        return payment.getPartPayments().stream()
                .map(this::convertToPaymentHistoryDTO)
                .collect(Collectors.toList());
    }

    private void validateBroker(Long brokerId) {
        if (!brokerRepository.existsById(brokerId)) {
            throw new ResourceNotFoundException("Broker not found with id: " + brokerId);
        }
    }

    private BrokerageDashboardDTO.BrokerageSummary calculateSummary(List<BrokeragePayment> payments) {
        BigDecimal totalReceivable = payments.stream()
                .map(BrokeragePayment::getNetBrokerage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReceived = payments.stream()
                .map(BrokeragePayment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPending = payments.stream()
                .map(BrokeragePayment::getPendingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long paidCount = payments.stream()
                .mapToLong(p -> p.getStatus() == PaymentStatus.PAID ? 1 : 0)
                .sum();

        long partialPaidCount = payments.stream()
                .mapToLong(p -> p.getStatus() == PaymentStatus.PARTIAL_PAID ? 1 : 0)
                .sum();

        long pendingCount = payments.size() - paidCount - partialPaidCount;

        return BrokerageDashboardDTO.BrokerageSummary.builder()
                .totalBrokerageReceivable(totalReceivable)
                .totalBrokerageReceived(totalReceived)
                .totalBrokeragePending(totalPending)
                .totalMerchants((long) payments.size())
                .paidMerchants(paidCount)
                .partialPaidMerchants(partialPaidCount)
                .pendingMerchants(pendingCount)
                .build();
    }

    private List<MerchantBrokerageDTO> convertToMerchantDTOs(List<BrokeragePayment> payments) {
        return payments.stream()
                .map(this::convertToMerchantDTO)
                .collect(Collectors.toList());
    }

    private MerchantBrokerageDTO convertToMerchantDTO(BrokeragePayment payment) {
        User merchant = payment.getMerchant();
        BigDecimal calculatedBrokerage = calculateBrokerage(payment.getTotalBags(), payment.getBrokerageRate());
        
        List<MerchantBrokerageDTO.PaymentHistoryDTO> history = payment.getPartPayments() != null ?
                payment.getPartPayments().stream()
                        .map(this::convertToPaymentHistoryDTO)
                        .collect(Collectors.toList()) : List.of();

        return MerchantBrokerageDTO.builder()
                .merchantId(merchant.getUserId())
                .firmName(merchant.getFirmName())
                .ownerName(merchant.getOwnerName())
                .phoneNumber(merchant.getPhoneNumbers() != null && !merchant.getPhoneNumbers().isEmpty() ? 
                        merchant.getPhoneNumbers().get(0) : null)
                .soldBags(payment.getSoldBags())
                .boughtBags(payment.getBoughtBags())
                .totalBags(payment.getTotalBags())
                .brokerageRate(payment.getBrokerageRate())
                .calculatedBrokerage(calculatedBrokerage)
                .actualBrokerage(payment.getNetBrokerage())
                .paidAmount(payment.getPaidAmount())
                .pendingAmount(payment.getPendingAmount())
                .status(payment.getStatus())
                .lastPaymentDate(payment.getLastPaymentDate())
                .dueDate(payment.getDueDate())
                .paymentHistory(history)
                .build();
    }

    private MerchantBrokerageDTO.PaymentHistoryDTO convertToPaymentHistoryDTO(PartPayment partPayment) {
        return MerchantBrokerageDTO.PaymentHistoryDTO.builder()
                .paymentId(partPayment.getId())
                .amount(partPayment.getAmount())
                .paymentDate(partPayment.getPaymentDate())
                .paymentMethod(partPayment.getMethod().getDisplayName())
                .transactionReference(partPayment.getTransactionReference())
                .notes(partPayment.getNotes())
                .build();
    }

    private List<BrokerageDashboardDTO.BrokerageChartData> generateChartData(List<BrokeragePayment> payments) {
        BigDecimal totalReceived = payments.stream()
                .map(BrokeragePayment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPending = payments.stream()
                .map(BrokeragePayment::getPendingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return List.of(
                BrokerageDashboardDTO.BrokerageChartData.builder()
                        .label("Received")
                        .value(totalReceived)
                        .color("#4CAF50")
                        .build(),
                BrokerageDashboardDTO.BrokerageChartData.builder()
                        .label("Pending")
                        .value(totalPending)
                        .color("#FF9800")
                        .build()
        );
    }

    private BigDecimal calculateBrokerage(Long totalBags, BigDecimal brokerageRate) {
        if (totalBags == null || brokerageRate == null) {
            return BigDecimal.ZERO;
        }
        return brokerageRate.multiply(BigDecimal.valueOf(totalBags));
    }

    private void addPartialPayment(BrokeragePayment payment, UpdatePaymentStatusRequestDTO request) {
        PartPayment partPayment = PartPayment.builder()
                .brokeragePayment(payment)
                .amount(request.getPaidAmount())
                .paymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now())
                .method(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.CASH)
                .transactionReference(request.getTransactionReference())
                .notes(request.getNotes())
                .build();

        partPaymentRepository.save(partPayment);
        payment.addPartPayment(partPayment);
    }

    private void markAsFullyPaid(BrokeragePayment payment, UpdatePaymentStatusRequestDTO request) {
        BigDecimal remainingAmount = payment.getPendingAmount();
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            addPartialPayment(payment, UpdatePaymentStatusRequestDTO.builder()
                    .paidAmount(remainingAmount)
                    .paymentDate(request.getPaymentDate())
                    .paymentMethod(request.getPaymentMethod())
                    .transactionReference(request.getTransactionReference())
                    .notes(request.getNotes())
                    .build());
        }
    }

    @Override
    public void calculateBrokerageForBroker(Long brokerId) {
        brokerageCalculationService.calculateBrokerageForBroker(brokerId);
    }

    @Override
    @Transactional(readOnly = true)
    public CityBrokerageAnalyticsDTO getCityBrokerageAnalytics(Long brokerId, String city) {
        validateBroker(brokerId);
        
        List<BrokeragePayment> cityPayments = brokeragePaymentRepository.findByBrokerBrokerIdAndMerchantAddressCity(brokerId, city);
        
        Long totalMerchants = (long) cityPayments.size();
        Long totalBagsSold = cityPayments.stream().mapToLong(bp -> bp.getSoldBags() != null ? bp.getSoldBags() : 0L).sum();
        Long totalBagsBought = cityPayments.stream().mapToLong(bp -> bp.getBoughtBags() != null ? bp.getBoughtBags() : 0L).sum();
        Long totalBags = totalBagsSold + totalBagsBought;
        
        BigDecimal totalActualBrokerage = cityPayments.stream()
                .map(bp -> bp.getNetBrokerage() != null ? bp.getNetBrokerage() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalBrokeragePending = cityPayments.stream()
                .map(bp -> bp.getPendingAmount() != null ? bp.getPendingAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalBrokerageReceived = cityPayments.stream()
                .map(bp -> bp.getPaidAmount() != null ? bp.getPaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Long totalPayments = cityPayments.stream()
                .mapToLong(bp -> bp.getNetBrokerage() != null && bp.getNetBrokerage().compareTo(BigDecimal.ZERO) > 0 ? 1 : 0)
                .sum();
        
        Long totalPartialPayments = cityPayments.stream()
                .mapToLong(bp -> bp.getStatus() == PaymentStatus.PARTIAL_PAID ? 1 : 0)
                .sum();
        
        Long totalSuccessPayments = cityPayments.stream()
                .mapToLong(bp -> bp.getStatus() == PaymentStatus.PAID ? 1 : 0)
                .sum();
        
        // Get previous year data for comparison
        FinancialYear previousYear = getPreviousFinancialYear(brokerId);
        BusinessChangeData changeData = calculateBusinessChange(brokerId, city, previousYear);
        
        return CityBrokerageAnalyticsDTO.builder()
                .city(city)
                .totalMerchants(totalMerchants)
                .totalBagsSold(totalBagsSold)
                .totalBagsBought(totalBagsBought)
                .totalBags(totalBags)
                .totalActualBrokerage(totalActualBrokerage)
                .totalBrokeragePending(totalBrokeragePending)
                .totalBrokerageReceived(totalBrokerageReceived)
                .totalPayments(totalPayments)
                .totalPartialPayments(totalPartialPayments)
                .totalSuccessPayments(totalSuccessPayments)
                .merchantsBusinessIncreased(changeData.increased)
                .merchantsBusinessDecreased(changeData.decreased)
                .totalBrokerageChange(changeData.totalChange)
                .build();
    }
    
    private FinancialYear getPreviousFinancialYear(Long brokerId) {
        return financialYearRepository.findPreviousFinancialYear(brokerId).orElse(null);
    }
    
    private BusinessChangeData calculateBusinessChange(Long brokerId, String city, FinancialYear previousYear) {
        if (previousYear == null) {
            return new BusinessChangeData(0L, 0L, BigDecimal.ZERO);
        }
        
        List<BrokerageHistory> previousYearData = brokerageHistoryRepository
                .findByBrokerIdAndFinancialYear(brokerId, previousYear.getYearId());
        
        List<BrokeragePayment> currentYearData = brokeragePaymentRepository.findByBrokerBrokerIdAndMerchantAddressCity(brokerId, city);
        
        long increased = 0, decreased = 0;
        BigDecimal totalChange = BigDecimal.ZERO;
        
        for (BrokeragePayment current : currentYearData) {
            BrokerageHistory previous = previousYearData.stream()
                    .filter(h -> h.getMerchant().getUserId().equals(current.getMerchant().getUserId()))
                    .findFirst().orElse(null);
            
            if (previous != null) {
                BigDecimal currentBrokerage = current.getNetBrokerage() != null ? current.getNetBrokerage() : BigDecimal.ZERO;
                BigDecimal previousBrokerage = previous.getTotalBrokerage() != null ? previous.getTotalBrokerage() : BigDecimal.ZERO;
                BigDecimal change = currentBrokerage.subtract(previousBrokerage);
                
                if (change.compareTo(BigDecimal.ZERO) > 0) {
                    increased++;
                } else if (change.compareTo(BigDecimal.ZERO) < 0) {
                    decreased++;
                }
                
                totalChange = totalChange.add(change);
            }
        }
        
        return new BusinessChangeData(increased, decreased, totalChange);
    }
    
    private static class BusinessChangeData {
        final Long increased;
        final Long decreased;
        final BigDecimal totalChange;
        
        BusinessChangeData(Long increased, Long decreased, BigDecimal totalChange) {
            this.increased = increased;
            this.decreased = decreased;
            this.totalChange = totalChange;
        }
    }
}