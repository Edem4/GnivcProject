package com.service.portalservice.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String vin;
    @Column
    private int year;
    @Column(unique = true)
    private String number;
    @Column
    private String company;
}