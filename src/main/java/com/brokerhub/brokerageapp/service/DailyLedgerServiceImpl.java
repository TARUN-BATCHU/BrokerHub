package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.DailyLedgerRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class DailyLedgerServiceImpl implements DailyLedgerService{

    @Autowired
    DailyLedgerRepository dailyLedgerRepository;

    @Autowired
    FinancialYearRepository financialYearRepository;

    public Long createDailyLedger(Long financialYearId, LocalDate date){
        if(null != financialYearId && null!= date) {
            Optional<FinancialYear> financialYear = financialYearRepository.findById(financialYearId);
            if (financialYear.isPresent() && (date.isEqual(financialYear.get().getStart()) || date.isEqual(financialYear.get().getEnd()) || (date.isAfter(financialYear.get().getStart()) && date.isBefore(financialYear.get().getEnd())))) {
                if (null == dailyLedgerRepository.findByDate(date)) {
                    DailyLedger dailyLedger = new DailyLedger();
                    dailyLedger.setDate(date);
                    dailyLedger.setFinancialYear(financialYear.get());
                    dailyLedgerRepository.save(dailyLedger);
                }
                return dailyLedgerRepository.findByDate(date).getDailyLedgerId();
            }
        }
        return null;
    }


    public Long getDailyLedgerId(LocalDate date) {
        if(null != dailyLedgerRepository.findByDate(date)){
            return dailyLedgerRepository.findByDate(date).getDailyLedgerId();
        }
        else{
            //TODO if daily ledger not exists then create one.
            return null;
        }
    }

    public DailyLedger getDailyLedger(LocalDate date) {
        Long dailyLedgerId = getDailyLedgerId(date);
        if(null != dailyLedgerId){
            Optional<DailyLedger> dailyLedger =  dailyLedgerRepository.findById(dailyLedgerId);
            return dailyLedger.get();
        }
        else{
            //TODO if daily ledger not exists then create one.
            return null;
        }
    }
}
