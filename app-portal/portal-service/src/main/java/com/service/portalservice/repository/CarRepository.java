package com.service.portalservice.repository;

import com.service.portalservice.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, String> {
    public Car findByVin(String vin);
    public Car findByCompany(String company);
}