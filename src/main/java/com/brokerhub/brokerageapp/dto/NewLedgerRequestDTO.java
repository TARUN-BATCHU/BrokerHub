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
public class NewLedgerRequestDTO {
    
    private Long brokerId;
    private Long financialYearId;
    private Long sellerBrokerage;
    private String seller_name;
    private String order_date;
    private List<ProductListDTO> product_list;
    private List<BuyerDTO> buyers;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductListDTO {
        private String product_name;
        private Long total_quantity;
        private Long price;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BuyerDTO {
        private String buyer_name;
        private Long buyerBrokerage;
        private List<BuyerProductDTO> products;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BuyerProductDTO {
        private String product_name;
        private Long quantity;
        private Long price;
    }
}