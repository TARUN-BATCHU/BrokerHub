package com.brokerhub.brokerageapp.dto.market;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private String status;
    private String code;
    private String message;
    private Map<String, String> details;
}