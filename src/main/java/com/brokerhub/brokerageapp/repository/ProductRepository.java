package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
