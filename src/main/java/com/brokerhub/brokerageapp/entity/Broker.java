package com.brokerhub.brokerageapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brokerId;

    @NotNull
    @NotBlank(message = "please enter the username")
    @Column(unique = true)
    private String userName;

    @NotNull
    private String password;

    @NotNull
    private String brokerName;

    @NotNull
    private String brokerageFirmName;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Email
    private String email;

    @Size(min = 10)
    private String phoneNumber;

    @PositiveOrZero
    private BigDecimal totalBrokerage;

    @OneToOne
    @PrimaryKeyJoinColumn
    private BankDetails bankDetails;

    private Integer otp = null;

    private LocalDateTime otpGeneratedTime = null;

}
