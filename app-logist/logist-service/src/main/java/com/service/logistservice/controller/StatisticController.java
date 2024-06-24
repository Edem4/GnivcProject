package com.service.logistservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sadikov.myLibrary.dto.GetStatisticDTO;
import com.sadikov.myLibrary.exceptions.ForbiddenException;
import com.sadikov.myLibrary.exceptions.TaskNotFoundException;
import com.sadikov.myLibrary.mapper.Mappers;
import com.service.logistservice.service.StatisticService;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stat")
public class StatisticController {
    @Autowired
    private StatisticService statisticService;

    @PostMapping("")
    public ResponseEntity<?> getStatisticForCompany(@RequestBody GetStatisticDTO statisticDTO,
                                                    @RequestHeader HttpHeaders headers){
        try {
            return  new ResponseEntity<>(
                    statisticService.getStatistic(statisticDTO.getCompanyName(),Mappers.getUserFromHeaders(headers)),
                    HttpStatus.OK);

        } catch (JsonProcessingException | ForbiddenException | TaskNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
