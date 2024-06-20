package com.service.logistservice.repository;

import com.service.logistservice.model.Points;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Points,Long> {
}
