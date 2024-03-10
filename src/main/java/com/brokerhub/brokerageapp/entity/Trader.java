package com.brokerhub.brokerageapp.entity;

import jakarta.persistence.*;
@Entity
@DiscriminatorValue("TRADER")
public class Trader extends User{

}
