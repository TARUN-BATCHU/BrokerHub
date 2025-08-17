package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.GrainCostRequestDTO;
import com.brokerhub.brokerageapp.dto.GrainCostResponseDTO;
import com.brokerhub.brokerageapp.dto.payments.ApiResponseDTO;
import com.brokerhub.brokerageapp.service.GrainCostService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for grain cost tracking operations.
 * Provides endpoints for managing grain costs and price history.
 * 
 * Base URL: /BrokerHub/grain-costs
 * 
 * Authentication: Basic Authentication required
 * Username: tarun
 * Password: securePassword123
 */
@RestController
@RequestMapping("/BrokerHub/grain-costs")
@Slf4j
public class GrainCostController {

    @Autowired
    private GrainCostService grainCostService;

    /**
     * Add Grain Cost Entry
     * 
     * POST /BrokerHub/grain-costs/{brokerId}
     * 
     * @param brokerId Broker ID
     * @param request Grain cost details (productName, cost, optional dateTime)
     * @return Created grain cost entry
     */
    @PostMapping("/{brokerId}")
    public ResponseEntity<ApiResponseDTO<GrainCostResponseDTO>> addGrainCost(
            @PathVariable Long brokerId,
            @Valid @RequestBody GrainCostRequestDTO request) {
        try {
            log.info("Request received: POST /BrokerHub/grain-costs/{} with product: {}, cost: {}", 
                    brokerId, request.getProductName(), request.getCost());
            
            return grainCostService.addGrainCost(brokerId, request);
            
        } catch (Exception e) {
            log.error("Error in addGrainCost for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to add grain cost", "Internal server error"));
        }
    }

    /**
     * Get All Grain Costs
     * 
     * GET /BrokerHub/grain-costs/{brokerId}
     * 
     * @param brokerId Broker ID
     * @return List of all grain cost entries for the broker (ordered by date desc)
     */
    @GetMapping("/{brokerId}")
    public ResponseEntity<ApiResponseDTO<List<GrainCostResponseDTO>>> getAllGrainCosts(
            @PathVariable Long brokerId) {
        try {
            log.info("Request received: GET /BrokerHub/grain-costs/{}", brokerId);
            
            return grainCostService.getAllGrainCosts(brokerId);
            
        } catch (Exception e) {
            log.error("Error in getAllGrainCosts for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve grain costs", "Internal server error"));
        }
    }

    /**
     * Delete Grain Cost Entry
     * 
     * DELETE /BrokerHub/grain-costs/{brokerId}/{grainCostId}
     * 
     * @param brokerId Broker ID
     * @param grainCostId Grain cost entry ID to delete
     * @return Success message
     */
    @DeleteMapping("/{brokerId}/{grainCostId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteGrainCost(
            @PathVariable Long brokerId,
            @PathVariable Long grainCostId) {
        try {
            log.info("Request received: DELETE /BrokerHub/grain-costs/{}/{}", brokerId, grainCostId);
            
            return grainCostService.deleteGrainCost(brokerId, grainCostId);
            
        } catch (Exception e) {
            log.error("Error in deleteGrainCost for broker: {}, grainCostId: {}", brokerId, grainCostId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to delete grain cost", "Internal server error"));
        }
    }
}