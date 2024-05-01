package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.DailyLedger;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.DailyLedgerRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DailyLedgerServiceImpl implements DailyLedgerService{

    @Autowired
    DailyLedgerRepository dailyLedgerRepository;

    @Autowired
    FinancialYearRepository financialYearRepository;

    public Long createDailyLedger(Long financialYearId){
        if(null == dailyLedgerRepository.findByDate(LocalDate.now())){
            DailyLedger dailyLedger = new DailyLedger();
            dailyLedger.setDate(LocalDate.now());
            Optional<FinancialYear> financialYear = financialYearRepository.findById(financialYearId);
            if(financialYear.isPresent()) {
                dailyLedger.setFinancialYear(financialYear.get());
            }
            dailyLedgerRepository.save(dailyLedger);
        }
        return dailyLedgerRepository.findByDate(LocalDate.now()).getLedgerDetailsId();
    }

    public Long getDailyLedgerId(LocalDate date) {
        if(null != dailyLedgerRepository.findByDate(date)){
            return dailyLedgerRepository.findByDate(date).getLedgerDetailsId();
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
