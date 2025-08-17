package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String productName;
    private Float productBrokerage;
    private Integer quantity;
    private Integer price;
    private String quality;
    private String imgLink;
}