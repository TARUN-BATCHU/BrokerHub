package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialYearServiceImpl implements FinancialYearService{

    @Autowired
    FinancialYearRepository financialYearRepository;

    public void createFinancialYear(LocalDate start, LocalDate end) {
        if(null == financialYearRepository.findByStartAndEnd(start,end) && null!=financialYearRepository.findOverlappingYears(start,end)){
            FinancialYear financialYear = new FinancialYear();
            financialYear.setStart(start);
            financialYear.setEnd(end);
            financialYearRepository.save(financialYear);
        }
    }

    public List<Long> getAllFinancialYearIds() {
        List<Long> financialYearIds = null;
        List<FinancialYear> AllFinancialYears = financialYearRepository.findAll();
        if(null != AllFinancialYears) {
            for (int i = 0; i < AllFinancialYears.size(); i++) {
                financialYearIds.add(AllFinancialYears.get(i).getYearId());
            }
            return financialYearIds;
        }
        return null;
    }
}
