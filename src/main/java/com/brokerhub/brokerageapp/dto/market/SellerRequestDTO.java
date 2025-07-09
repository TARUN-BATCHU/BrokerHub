package com.brokerhub.brokerageapp.dto.market;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRequestDTO {
    private String requestId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Quality is required")
    private String quality;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    private String description;

    @NotNull(message = "Available until date is required")
    private LocalDateTime availableUntil;

    private String status;

    @NotNull(message = "Seller information is required")
    private SellerInfoDTO seller;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerInfoDTO {
        @NotBlank(message = "Firm name is required")
        private String firmName;

        @NotBlank(message = "Location is required")
        private String location;
    }
}