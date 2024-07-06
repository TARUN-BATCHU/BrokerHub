package com.brokerhub.brokerageapp.dto;

import com.brokerhub.brokerageapp.entity.LedgerRecord;
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
public class LedgerDetailsDTO {

    private Long brokerId;

    private Long brokerage;

    private Long fromSeller;

    private LocalDate date;

    private List<LedgerRecordDTO> ledgerRecordDTOList;
}
