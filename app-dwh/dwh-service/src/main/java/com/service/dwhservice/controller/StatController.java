package com.service.dwhservice.controller;

import com.sadikov.myLibrary.dto.GetStatisticDTO;
import com.service.dwhservice.logist.LogistClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stat")
public class StatController {
    @Autowired
    private LogistClient logistClient;

    @PostMapping("")
    public ResponseEntity<?> getStatistic(@RequestBody GetStatisticDTO getStatisticDTO,
                                          @RequestHeader HttpHeaders headers){
        return new ResponseEntity<>(logistClient.getCompanyStatistic(getStatisticDTO,headers.toSingleValueMap()),
                HttpStatus.OK);
    }
}
