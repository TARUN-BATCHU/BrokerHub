package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptimizedLedgerDetailsDTO {

    private Long ledgerDetailsId;
    private Long brokerTransactionNumber;
    private LocalDate transactionDate;
    private OptimizedUserDTO fromSeller;
    private List<OptimizedLedgerRecordDTO> records;

    // Transaction-specific summary (only for this ledger)
    private TransactionSummaryDTO transactionSummary;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TransactionSummaryDTO {
        private Long totalBagsSoldInTransaction;
        private BigDecimal totalBrokerageInTransaction;
        private Long totalReceivableAmountInTransaction;
        private BigDecimal averageBrokeragePerBag;
        private Integer numberOfProducts;
        private Integer numberOfBuyers;
    }


}

