package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateLedgerDetailDTO {

    private Long ledgerDetailId;

    private String brokerName;

    private Long brokerage;

    private String sellerName;

    private LocalDate date;

    private List<DateLedgerRecordDTO> dateLedgerRecordDTOList;
}
