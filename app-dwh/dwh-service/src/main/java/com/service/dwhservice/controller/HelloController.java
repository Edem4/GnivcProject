package com.service.dwhservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/dwh")
    public String seyHello(){
        return "Hello dwh!";
    }
}
