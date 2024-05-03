package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialYearServiceImpl implements FinancialYearService{

    @Autowired
    FinancialYearRepository financialYearRepository;

    public ResponseEntity<String> createFinancialYear(LocalDate start, LocalDate end) {
        FinancialYear existingFinancialYear = financialYearRepository.findOverlappingYears(start,end);
        if(null == financialYearRepository.findByStartAndEnd(start,end) && null==financialYearRepository.findOverlappingYears(start,end)){
            FinancialYear financialYear = new FinancialYear();
            financialYear.setStart(start);
            financialYear.setEnd(end);
            financialYearRepository.save(financialYear);
            return ResponseEntity.status(HttpStatus.CREATED).body("Financial year created");
        }
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Already under financial year from :"+existingFinancialYear.getStart()+" to "+existingFinancialYear.getEnd());
    }

    public List<Long> getAllFinancialYearIds() {
        ArrayList<Long> financialYearIds = new ArrayList<>();
        List<FinancialYear> AllFinancialYears = financialYearRepository.findAll();
        if(null != AllFinancialYears) {
            for (int i = 0; i < AllFinancialYears.size(); i++) {
                financialYearIds.add(AllFinancialYears.get(i).getYearId());
            }
            if(financialYearIds.size()>0) {
                return financialYearIds;
            }
        }
        return null;
    }
}
