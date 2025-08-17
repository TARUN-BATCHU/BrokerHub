package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrainCostResponseDTO {

    private Long id;
    private String productName;
    private BigDecimal cost;
    private String date; // Format: dd-MM-yyyy
}