package com.brokerhub.brokerageapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductBasicInfoDTO implements Serializable {
    
    private Long productId;
    private String productName;
    private Float productBrokerage;
    private String quality;
    private Integer price;
    
    // Constructor for Object[] mapping from repository queries
    public ProductBasicInfoDTO(Object[] data) {
        if (data.length >= 5) {
            this.productId = (Long) data[0];
            this.productName = (String) data[1];
            this.productBrokerage = (Float) data[2];
            this.quality = (String) data[3];
            this.price = (Integer) data[4];
        }
    }
    
    // Constructor for productId and productName only
    public ProductBasicInfoDTO(Long productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }
    
    // Constructor for productName and quality
    public ProductBasicInfoDTO(String productName, String quality) {
        this.productName = productName;
        this.quality = quality;
    }
}
