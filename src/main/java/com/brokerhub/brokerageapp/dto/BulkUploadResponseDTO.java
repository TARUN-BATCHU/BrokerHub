package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkUploadResponseDTO {
    private int totalRecords;
    private int successfulRecords;
    private int failedRecords;
    private List<String> errorMessages;
    private String message;
}
