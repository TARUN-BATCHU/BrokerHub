package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialYearRepository extends JpaRepository<FinancialYear, Long> {
}
