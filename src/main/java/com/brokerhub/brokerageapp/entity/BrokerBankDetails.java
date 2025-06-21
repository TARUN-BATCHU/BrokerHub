package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrokerBankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brokerBankDetailsId;

    /**
     * which broker is the owner of this bank.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    @NotNull
    private Broker broker;

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
