package com.brokerhub.brokerageapp.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for all payment-related endpoints.
 * Provides consistent response structure across all APIs.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseDTO<T> {
    
    /**
     * Status of the API call: "success" or "error"
     */
    private String status;
    
    /**
     * Human-readable message describing the result
     */
    private String message;
    
    /**
     * The actual data payload (can be any type)
     */
    private T data;
    
    /**
     * Error details (only present when status is "error")
     */
    private String error;

    /**
     * Create a successful response with data
     */
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a successful response without data
     */
    public static <T> ApiResponseDTO<T> success(String message) {
        return ApiResponseDTO.<T>builder()
                .status("success")
                .message(message)
                .build();
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponseDTO<T> error(String message, String error) {
        return ApiResponseDTO.<T>builder()
                .status("error")
                .message(message)
                .error(error)
                .build();
    }

    /**
     * Create an error response with just message
     */
    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .status("error")
                .message(message)
                .build();
    }

    /**
     * Check if the response indicates success
     */
    public boolean isSuccess() {
        return "success".equals(status);
    }

    /**
     * Check if the response indicates error
     */
    public boolean isError() {
        return "error".equals(status);
    }
}
