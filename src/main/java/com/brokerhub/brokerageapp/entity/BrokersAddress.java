package com.brokerhub.brokerageapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrokersAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotNull
    @Size(min = 2, max = 50)
    private String state;

    @NotNull
    @Size(min = 2, max = 50)
    private String city;

    private String area;

    private String pincode;

    @OneToMany(mappedBy = "address")
    @JsonIgnore
    private List<Broker> brokers;
}
