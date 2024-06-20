package com.service.logistservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tasks")
public class Tasks {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column()
    private String companyName;
    @Column()
    private String companyInn;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "startPoint", referencedColumnName = "id")
    private Points startPoint;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endPoint", referencedColumnName = "id")
    private Points endPoint;

    @Column
    private String firstNameDriver;

    @Column
    private String lastNameDriver;

    @Column
    private String descriptionCargo;

    @Column
    private String numberAuto;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tasks_id")
    private List<Flight> flights = new ArrayList<>();

    public void setFlights(Flight flight){
        flights.add(flight);
    }

}
