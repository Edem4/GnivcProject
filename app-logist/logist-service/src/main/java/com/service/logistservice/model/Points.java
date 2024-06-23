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

    public Points(double lat, double lon) {
        this.longitude=lon;
        this.latitude=lat;
    }
    public Points(String coordinates) {
        this.longitude= Double.parseDouble(coordinates.split(",")[1]);
        this.latitude=Double.parseDouble(coordinates.split(",")[0]);
    }

    public Points() {
    }

    @Override
    public String toString() {
        return "lat = " + latitude +
                ", lon = " + longitude;
    }
}
