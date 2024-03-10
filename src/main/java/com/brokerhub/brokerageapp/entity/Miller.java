package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MILLER")
public class Miller extends User{

    private String byProduct;

    public String getByProduct() {
        return byProduct;
    }

    public void setByProduct(String byProduct) {
        this.byProduct = byProduct;
    }
}
