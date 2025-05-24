package com.brokerhub.brokerageapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotNull
    private String productName;

    @PositiveOrZero
    @NotNull
    private Float productBrokerage;


    @PositiveOrZero
    private  Integer quantity;

    @PositiveOrZero
    private Integer price;

    private String quality;

    private String imgLink;

}
