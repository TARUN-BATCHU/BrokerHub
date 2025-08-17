package com.brokerhub.brokerageapp.repository;

import com.brokerhub.brokerageapp.entity.GrainCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrainCostRepository extends JpaRepository<GrainCost, Long> {

    @Query("SELECT gc FROM GrainCost gc WHERE gc.broker.brokerId = :brokerId ORDER BY gc.createdAt DESC")
    List<GrainCost> findByBrokerIdOrderByCreatedAtDesc(@Param("brokerId") Long brokerId);

    @Query("SELECT gc FROM GrainCost gc WHERE gc.id = :id AND gc.broker.brokerId = :brokerId")
    Optional<GrainCost> findByIdAndBrokerId(@Param("id") Long id, @Param("brokerId") Long brokerId);
}