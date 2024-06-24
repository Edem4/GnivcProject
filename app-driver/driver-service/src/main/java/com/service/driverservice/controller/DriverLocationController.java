package com.service.driverservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sadikov.myLibrary.dto.PointDTO;
import com.sadikov.myLibrary.mapper.Mappers;
import com.service.driverservice.kafka.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
public class DriverLocationController {
    @Autowired
    private Producer producer;

    @PostMapping("")
    public ResponseEntity<?> locationMessage(@RequestBody PointDTO point,
                                             @RequestHeader HttpHeaders headers){
        try {
            point.setDriverId(Mappers.getUserFromHeaders(headers).getUserId());
            producer.pointMessage(point);
            return new ResponseEntity<>("Geolocation sent!", HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
