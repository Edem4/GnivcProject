package com.service.logistservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "flight")
public class Flight {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String driverId;
    @Column
    private String companyName;
    @Column()
    private LocalDateTime createTime;

    @Column()
    private LocalDateTime startTime;

    @Column()
    private LocalDateTime endTime;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "flight_id")
    private List<Points> driverLocation;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "flight_id")
    private List<Events> events;

    public void setEvents(Events events) {
        this.events.add(events);
    }

    public Flight() {
        this.createTime = LocalDateTime.now();
        this.events = new ArrayList<>();
        this.driverLocation = new ArrayList<>();
    }

    public void setDriverLocation(Points points) {
        this.driverLocation.add(points);
    }
}
