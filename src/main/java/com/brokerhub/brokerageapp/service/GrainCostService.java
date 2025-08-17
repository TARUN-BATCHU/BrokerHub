package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.GrainCostRequestDTO;
import com.brokerhub.brokerageapp.dto.GrainCostResponseDTO;
import com.brokerhub.brokerageapp.dto.payments.ApiResponseDTO;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.entity.GrainCost;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.GrainCostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GrainCostService {

    @Autowired
    private GrainCostRepository grainCostRepository;

    @Autowired
    private BrokerRepository brokerRepository;

    public ResponseEntity<ApiResponseDTO<GrainCostResponseDTO>> addGrainCost(Long brokerId, GrainCostRequestDTO request) {
        try {
            Broker broker = brokerRepository.findById(brokerId)
                    .orElse(null);
            
            if (broker == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Broker not found", "Invalid broker ID"));
            }

            LocalDateTime dateTime = LocalDateTime.now();
            if (request.getDate() != null && !request.getDate().trim().isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    dateTime = java.time.LocalDate.parse(request.getDate(), formatter).atStartOfDay();
                } catch (Exception e) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponseDTO.error("Invalid date format", "Date must be in dd-MM-yyyy format"));
                }
            }

            GrainCost grainCost = GrainCost.builder()
                    .broker(broker)
                    .productName(request.getProductName())
                    .cost(request.getCost())
                    .createdAt(dateTime)
                    .build();

            GrainCost saved = grainCostRepository.save(grainCost);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            GrainCostResponseDTO response = new GrainCostResponseDTO(
                    saved.getId(),
                    saved.getProductName(),
                    saved.getCost(),
                    saved.getCreatedAt().format(formatter)
            );

            return ResponseEntity.ok(ApiResponseDTO.success("Grain cost added successfully", response));

        } catch (Exception e) {
            log.error("Error adding grain cost for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to add grain cost", "Internal server error"));
        }
    }

    public ResponseEntity<ApiResponseDTO<List<GrainCostResponseDTO>>> getAllGrainCosts(Long brokerId) {
        try {
            List<GrainCost> grainCosts = grainCostRepository.findByBrokerIdOrderByCreatedAtDesc(brokerId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            List<GrainCostResponseDTO> response = grainCosts.stream()
                    .map(gc -> new GrainCostResponseDTO(
                            gc.getId(),
                            gc.getProductName(),
                            gc.getCost(),
                            gc.getCreatedAt().format(formatter)
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponseDTO.success("Grain costs retrieved successfully", response));

        } catch (Exception e) {
            log.error("Error retrieving grain costs for broker: {}", brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to retrieve grain costs", "Internal server error"));
        }
    }

    public ResponseEntity<ApiResponseDTO<String>> deleteGrainCost(Long brokerId, Long grainCostId) {
        try {
            Optional<GrainCost> grainCost = grainCostRepository.findByIdAndBrokerId(grainCostId, brokerId);
            
            if (grainCost.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Grain cost not found", "Invalid grain cost ID or access denied"));
            }

            grainCostRepository.delete(grainCost.get());
            
            return ResponseEntity.ok(ApiResponseDTO.success("Grain cost deleted successfully"));

        } catch (Exception e) {
            log.error("Error deleting grain cost {} for broker: {}", grainCostId, brokerId, e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDTO.error("Failed to delete grain cost", "Internal server error"));
        }
    }
}