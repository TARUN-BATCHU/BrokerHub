package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MILLER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Miller extends User{

    private String byProduct;

}
