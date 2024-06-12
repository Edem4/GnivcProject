package com.service.driverservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/driver")
    public String seyHello(){
        return "Hello driver!";
    }
}
