package com.brokerhub.brokerageapp.dto;

import com.brokerhub.brokerageapp.entity.LedgerRecord;

import java.time.LocalDate;
import java.util.List;

public class LedgerDetailsDTO {

    private Long fromSeller;

    private LocalDate date;

    private List<LedgerRecordDTO> ledgerRecordDTOList;

    public Long getFromSeller() {
        return fromSeller;
    }

    public void setFromSeller(Long fromSeller) {
        this.fromSeller = fromSeller;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<LedgerRecordDTO> getLedgerRecordDTOList() {
        return ledgerRecordDTOList;
    }

    public void setLedgerRecordDTOList(List<LedgerRecordDTO> ledgerRecordListDTO) {
        this.ledgerRecordDTOList = ledgerRecordListDTO;
    }
}
