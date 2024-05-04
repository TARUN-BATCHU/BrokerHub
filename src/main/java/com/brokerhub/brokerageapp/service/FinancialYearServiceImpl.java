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

    public ResponseEntity<String> createFinancialYear(FinancialYear financialYear) {
        List<FinancialYear> overlappingFinancialYear = financialYearRepository.findOverlappingYears(financialYear.getStart(),financialYear.getEnd());
        List<FinancialYear> existingFinancialYear = financialYearRepository.findByStartAndEnd(financialYear.getStart(),financialYear.getEnd());
        if(existingFinancialYear.size()<1 || overlappingFinancialYear.size()<1) {
            if (false == financialYear.getForBills().booleanValue()) {
                if ((existingFinancialYear == null || true == existingFinancialYear.get(0).getForBills().booleanValue()) && (overlappingFinancialYear == null || true == overlappingFinancialYear.get(0).getForBills().booleanValue())) {
                    FinancialYear newFinancialYear = new FinancialYear();
                    newFinancialYear.setStart(financialYear.getStart());
                    newFinancialYear.setEnd(financialYear.getEnd());
                    newFinancialYear.setFinancialYearName(financialYear.getFinancialYearName());
                    newFinancialYear.setForBills(financialYear.getForBills());
                    financialYearRepository.save(newFinancialYear);
                    return ResponseEntity.status(HttpStatus.CREATED).body("Financial year created : " + newFinancialYear.getFinancialYearName());
                } else {
                    return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Already under financial year from :" + existingFinancialYear.get(0).getStart() + " to " + existingFinancialYear.get(0).getEnd());
                }
            } else {
                FinancialYear newFinancialYear = new FinancialYear();
                newFinancialYear.setStart(financialYear.getStart());
                newFinancialYear.setEnd(financialYear.getEnd());
                newFinancialYear.setFinancialYearName(financialYear.getFinancialYearName());
                newFinancialYear.setForBills(financialYear.getForBills());
                financialYearRepository.save(newFinancialYear);
                return ResponseEntity.status(HttpStatus.CREATED).body("Financial year created : " + newFinancialYear.getFinancialYearName() + " for bills");
            }
        }
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Already both with and withoutBill financial Years exists");
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
