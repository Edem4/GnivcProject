package com.service.portalservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.portalservice.exceptions.CarAlreadyExistException;
import com.service.portalservice.exceptions.ForbiddenException;
import com.service.portalservice.exceptions.UserNotFoundException;
import com.service.portalservice.mappers.Mapper;
import com.service.portalservice.models.Car;
import com.service.portalservice.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping("/add")
    public ResponseEntity<String> addCar(@RequestBody Car car,
                                         @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        try {
            carService.addCar(car, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (CarAlreadyExistException | ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

//
//    @GetMapping("/get")
//    public ResponseEntity<?> getCar(@RequestHeader HttpHeaders headers,
//                                    @RequestBody GetCompanyDTO companyDTO){
//
//        try {
//            return new ResponseEntity<>(carService.getCar(Mapper.getUserFromHeaders(headers), companyDTO.getNameCompany()), HttpStatus.OK);
//        } catch (ForbiddenException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
//        } catch (CarNotFoundExceptions e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        } catch (JsonProcessingException | UserNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
