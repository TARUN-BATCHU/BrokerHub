package com.brokerhub.brokerageapp.dto.dashboard;

import com.brokerhub.brokerageapp.entity.PaymentMethod;
import com.brokerhub.brokerageapp.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentStatusRequestDTO {
    
    @NotNull
    private Long merchantId;
    
    @NotNull
    private PaymentStatus status;
    
    @PositiveOrZero
    private BigDecimal paidAmount;
    
    private LocalDate paymentDate;
    
    private PaymentMethod paymentMethod;
    
    private String transactionReference;
    
    private String notes;
}