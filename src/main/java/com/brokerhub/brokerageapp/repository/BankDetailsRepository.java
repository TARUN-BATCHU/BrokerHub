package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankDetailsRepository extends JpaRepository<BankDetails , Long> {
}
