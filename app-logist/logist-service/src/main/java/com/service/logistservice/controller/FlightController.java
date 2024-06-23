package com.service.logistservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sadikov.myLibrary.dto.FlightDTO;
import com.sadikov.myLibrary.exceptions.FlightNotFoundException;
import com.sadikov.myLibrary.exceptions.PointsNotFoundException;
import com.sadikov.myLibrary.exceptions.TaskNotFoundException;
import com.sadikov.myLibrary.exceptions.TaskOfAnotherCompanyException;
import com.sadikov.myLibrary.mapper.Mappers;
import com.sadikov.myLibrary.model.User;
import com.service.logistservice.service.FlightService;
import jakarta.validation.Valid;
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
public class FlightController {

    private final FlightService flightService;


    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }


    //СОЗДАНИЕ НОВГО РЕЙСА
    @PostMapping("/create")
    public ResponseEntity<?> createFlight(@RequestBody @Valid FlightDTO flightDTO,
                                          @RequestHeader HttpHeaders headers,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(Objects.requireNonNull(bindingResult.getFieldError()).getField(), HttpStatus.BAD_REQUEST);
        }
        try {

            flightService.createFlight(flightDTO, Mappers.getUserFromHeaders(headers));
            return new ResponseEntity<>("Рейс успешно создан!", HttpStatus.CREATED);

        } catch (ForbiddenException | TaskNotFoundException | JsonProcessingException |
                 TaskOfAnotherCompanyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    //ДАННЫЕ ПО РЕЙСУ
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getFlight(@PathVariable("id") Long flightId,
                                       @RequestParam("taskId") Long taskId,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User user = Mappers.getUserFromHeaders(headers);
            return new ResponseEntity<>(flightService.getFlight(flightId, taskId, user).toString(), HttpStatus.OK);

        } catch (TaskNotFoundException | JsonProcessingException | TaskOfAnotherCompanyException |
                 FlightNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //ДАННЫЕ ПО ВСЕМ РЕЙСАМ
    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(@RequestParam("tasksId") long tasksId,
                                    @RequestHeader HttpHeaders headers) {
        try {
            User user = Mappers.getUserFromHeaders(headers);
            return new ResponseEntity<>(flightService.getAllFlight(user, tasksId), HttpStatus.OK);
        } catch (JsonProcessingException | TaskOfAnotherCompanyException | FlightNotFoundException |
                 TaskNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //ТОЧКИ ГЕОПОЗИЦИИ ВОДИТЕЛЯ
    @GetMapping("/get/location/{id}")
    public ResponseEntity<?> getPointDriver(@PathVariable("id") Long flightId,
                                            @RequestParam("taskId") Long taskId,
                                            @RequestHeader HttpHeaders headers) {
        try {
            User user = Mappers.getUserFromHeaders(headers);
            return new ResponseEntity<>(flightService.getPointDriver(flightId, taskId, user).toString(), HttpStatus.OK);

        } catch (JsonProcessingException | FlightNotFoundException | PointsNotFoundException | TaskNotFoundException |
                 TaskOfAnotherCompanyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
