package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.CurrentFinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrentFinancialYearRepository extends JpaRepository<CurrentFinancialYear, Long> {
    
    Optional<CurrentFinancialYear> findByBrokerId(Long brokerId);
    
    void deleteByBrokerId(Long brokerId);
}