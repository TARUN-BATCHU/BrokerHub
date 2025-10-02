package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FinancialYearServiceImpl implements FinancialYearService{

    @Autowired
    FinancialYearRepository financialYearRepository;
    
    @Autowired
    BrokerRepository brokerRepository;

    public ResponseEntity<String> createFinancialYear(FinancialYear financialYear, Long brokerId) {
        Broker broker = brokerRepository.findById(brokerId)
                .orElseThrow(() -> new RuntimeException("Broker not found with ID: " + brokerId));
        List<FinancialYear> overlappingFinancialYear = financialYearRepository.findOverlappingYears(financialYear.getStart(),financialYear.getEnd());
        List<FinancialYear> existingFinancialYear = financialYearRepository.findByStartAndEnd(financialYear.getStart(),financialYear.getEnd());

        // Make sure lists are initialized and not null
        if (overlappingFinancialYear == null) {
            overlappingFinancialYear = new ArrayList<>();
        }
        if (existingFinancialYear == null) {
            existingFinancialYear = new ArrayList<>();
        }

        if(existingFinancialYear.isEmpty() || overlappingFinancialYear.isEmpty()) {
            if (financialYear.getForBills() != null && !financialYear.getForBills()) {
                boolean existingYearForBills = !existingFinancialYear.isEmpty() && existingFinancialYear.get(0).getForBills() != null
                    && existingFinancialYear.get(0).getForBills();
                boolean overlappingYearForBills = !overlappingFinancialYear.isEmpty() && overlappingFinancialYear.get(0).getForBills() != null
                    && overlappingFinancialYear.get(0).getForBills();

                if (existingFinancialYear.isEmpty() || existingYearForBills) {
                    if (overlappingFinancialYear.isEmpty() || overlappingYearForBills) {
                        FinancialYear newFinancialYear = new FinancialYear();
                        newFinancialYear.setBroker(broker);
                        newFinancialYear.setStart(financialYear.getStart());
                        newFinancialYear.setEnd(financialYear.getEnd());
                        newFinancialYear.setFinancialYearName(financialYear.getFinancialYearName());
                        newFinancialYear.setForBills(financialYear.getForBills());
                        financialYearRepository.save(newFinancialYear);
                        return ResponseEntity.status(HttpStatus.CREATED).body("Financial year created : " + newFinancialYear.getFinancialYearName());
                    }
                }

                // Only access list elements if the list is not empty
                if (!existingFinancialYear.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Already under financial year from :" +
                        existingFinancialYear.get(0).getStart() + " to " + existingFinancialYear.get(0).getEnd());
                } else {
                    return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Financial year already exists");
                }
            } else {
                FinancialYear newFinancialYear = new FinancialYear();
                newFinancialYear.setBroker(broker);
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

    @Transactional(readOnly = true)
    public List<FinancialYear> getAllFinancialYears(){
        List<FinancialYear> financialYears = financialYearRepository.findAll();
        if(!financialYears.isEmpty()){
            return financialYears;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<FinancialYear> getAllFinancialYearsByBrokerId(Long brokerId){
        List<FinancialYear> financialYears = financialYearRepository.findByBrokerBrokerId(brokerId);
        if(!financialYears.isEmpty()){
            return financialYears;
        }
        return null;
    }

    public Optional<FinancialYear> getFinancialYear(Long financialYearId){
        Optional<FinancialYear> financialYear = financialYearRepository.findById(financialYearId);
        return financialYear;
    }
}
