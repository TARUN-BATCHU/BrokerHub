package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.CurrentFinancialYear;
import com.brokerhub.brokerageapp.repository.CurrentFinancialYearRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CurrentFinancialYearServiceImpl implements CurrentFinancialYearService {

    @Autowired
    private CurrentFinancialYearRepository currentFinancialYearRepository;

    @Override
    public ResponseEntity<String> setCurrentFinancialYear(Long brokerId, Long financialYearId) {
        log.info("Setting current financial year {} for broker {}", financialYearId, brokerId);
        
        try {
            Optional<CurrentFinancialYear> existingOptional = currentFinancialYearRepository.findByBrokerId(brokerId);
            
            CurrentFinancialYear currentFinancialYear;
            if (existingOptional.isPresent()) {
                currentFinancialYear = existingOptional.get();
                currentFinancialYear.setFinancialYearId(financialYearId);
                log.info("Updated existing financial year preference for broker {}", brokerId);
            } else {
                currentFinancialYear = CurrentFinancialYear.builder()
                        .brokerId(brokerId)
                        .financialYearId(financialYearId)
                        .build();
                log.info("Created new financial year preference for broker {}", brokerId);
            }
            
            currentFinancialYearRepository.save(currentFinancialYear);
            return ResponseEntity.ok("Current financial year set successfully");
            
        } catch (Exception e) {
            log.error("Error setting current financial year for broker {}: {}", brokerId, e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to set current financial year");
        }
    }

    @Override
    public Long getCurrentFinancialYearId(Long brokerId) {
        log.debug("Getting current financial year for broker {}", brokerId);
        
        Optional<CurrentFinancialYear> currentFinancialYearOptional = currentFinancialYearRepository.findByBrokerId(brokerId);
        
        if (currentFinancialYearOptional.isPresent()) {
            Long financialYearId = currentFinancialYearOptional.get().getFinancialYearId();
            log.debug("Found current financial year {} for broker {}", financialYearId, brokerId);
            return financialYearId;
        } else {
            log.warn("No current financial year set for broker {}", brokerId);
            return null;
        }
    }
}