package com.service.logistservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadikov.myLibrary.dto.PointDTO;
import com.sadikov.myLibrary.exceptions.FlightNotFoundException;
import com.service.logistservice.service.FlightService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DriverPointsConsumer {
    private final ObjectMapper objectMapper;
    private final FlightService flightService;

    @Autowired
    public DriverPointsConsumer(ObjectMapper objectMapper, FlightService flightService) {
        this.objectMapper = objectMapper;
        this.flightService = flightService;
    }

    @KafkaListener(topics = "point", groupId = "logist")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            PointDTO pointDTO = objectMapper.readValue(record.value(), PointDTO.class);
            flightService.addDriverPoints(pointDTO);

        } catch (JsonProcessingException | FlightNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}
