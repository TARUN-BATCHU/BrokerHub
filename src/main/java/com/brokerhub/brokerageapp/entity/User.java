package com.brokerhub.brokerageapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_type", insertable = false, updatable = false)
    private String userType;

    private String gstNumber;

    @NotNull
    private String firmName;

    private String ownerName;


    @ManyToOne
    @JoinColumn(name = "address_id")
    @NotNull
    private Address address;

    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "bankDetails_id")
    private BankDetails bankDetails;

    private List<String> phoneNumbers;

    @PositiveOrZero
    private Integer brokerageRate;

    @PositiveOrZero
    private Long totalBagsSold;

    @PositiveOrZero
    private Long totalBagsBought;

    @PositiveOrZero
    private Long payableAmount;

    @PositiveOrZero
    private Long receivableAmount;

    private BigDecimal totalPayableBrokerage;

    private String shopNumber;

    private String addressHint;

    private String collectionRote;
}
