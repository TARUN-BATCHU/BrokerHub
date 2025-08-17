package com.brokerhub.brokerageapp.dto.market;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerRequestDTO {
    private String requestId;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    private String status;

    @NotNull(message = "Buyer information is required")
    private BuyerInfoDTO buyer;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyerInfoDTO {
        @NotBlank(message = "Firm name is required")
        private String firmName;

        @NotBlank(message = "Location is required")
        private String location;
    }
}