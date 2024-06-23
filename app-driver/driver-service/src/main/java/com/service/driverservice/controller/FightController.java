package com.service.driverservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sadikov.myLibrary.dto.EventDTO;
import com.sadikov.myLibrary.dto.FlightDTO;
import com.sadikov.myLibrary.mapper.Mappers;
import com.service.driverservice.kafka.Producer;
import com.service.driverservice.logist.LogistClient;
import jakarta.ws.rs.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/flight")
public class FightController {
    @Autowired
    private Producer producer;
    @Autowired
    private LogistClient logistClient;


    @PostMapping("/create")
    public ResponseEntity<?> createFlight(@RequestBody FlightDTO flightDTO,
                                          @RequestHeader HttpHeaders headers,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(Objects.requireNonNull(bindingResult.getFieldError()).getField(), HttpStatus.BAD_REQUEST);
        }
        try {
            logistClient.createFlight(flightDTO, headers.toSingleValueMap());
            return new ResponseEntity<>("Рейс успешно создан!", HttpStatus.CREATED);

        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/event/create")
    public ResponseEntity<String> eventMessage(@RequestBody EventDTO eventDTO,
                                               @RequestHeader HttpHeaders headers) throws JsonProcessingException {

        eventDTO.setDriverId(Mappers.getUserFromHeaders(headers).getUserId());
        producer.eventMessage(eventDTO);

        return new ResponseEntity<>("Событие отправленно!", HttpStatus.OK);
    }
}
