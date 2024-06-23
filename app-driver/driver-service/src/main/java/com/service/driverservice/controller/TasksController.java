package com.service.driverservice.controller;

import com.service.driverservice.logist.LogistClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TasksController {
    @Autowired
    private LogistClient logistClient;

    @GetMapping("")
    public ResponseEntity<?> getTasks(@RequestHeader HttpHeaders headers){
            return new ResponseEntity<>(logistClient.getTasksDriver(headers.toSingleValueMap()), HttpStatus.OK);
    }
}
