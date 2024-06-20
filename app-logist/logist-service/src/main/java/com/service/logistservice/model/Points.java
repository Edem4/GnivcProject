package com.service.logistservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "points")
public class Points {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "lat")
    @NotNull
    private double latitude;
    @NotNull
    @Column(name = "lon")
    private double longitude;

    public Points(String coordinates) {
        latitude = Double.parseDouble(coordinates.split(",")[0]);
        longitude = Double.parseDouble(coordinates.split(",")[1]);
    }

    public Points() {
    }

}
