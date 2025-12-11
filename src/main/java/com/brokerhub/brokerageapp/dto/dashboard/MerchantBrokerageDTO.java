package com.brokerhub.brokerageapp.dto.dashboard;

import com.brokerhub.brokerageapp.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantBrokerageDTO {
    
    private Long merchantId;
    private String firmName;
    private String ownerName;
    private String phoneNumber;
    private Long soldBags;
    private Long boughtBags;
    private Long totalBags;
    private BigDecimal brokerageRate;
    private BigDecimal calculatedBrokerage;
    private BigDecimal actualBrokerage;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private PaymentStatus status;
    private LocalDate lastPaymentDate;
    private LocalDate dueDate;
    private List<PaymentHistoryDTO> paymentHistory;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentHistoryDTO {
        private Long paymentId;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String paymentMethod;
        private String transactionReference;
        private String notes;
    }
}