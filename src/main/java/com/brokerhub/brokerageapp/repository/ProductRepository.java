package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByProductName(String productName);

    List<Product> findByProductNameAndQualityAndQuantity(String productName,String productQuality, Integer quantity);
}
