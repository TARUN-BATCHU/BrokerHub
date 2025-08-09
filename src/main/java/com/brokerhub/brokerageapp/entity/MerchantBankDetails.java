package com.brokerhub.brokerageapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantBankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchantBankDetailsId;

    /**
     * The broker who owns this bank details.
     * This enables multi-tenant isolation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Broker broker;

    /**
     * which merchant is the owner of this bank
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_id", nullable = false)
    @NotNull
    @JsonIgnore
    private User user;

    private String accountNumber;

    private String ifscCode;

    private String bankName;

    private String branch;

    private String BankContact;

    private String BankAddress;

    private String BankCode;

    private String MICR;

    private Boolean RTGS;

    private Boolean IMPS;

    private Boolean UPI;

    private Boolean NEFT;

}
