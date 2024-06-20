package com.service.logistservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.logistservice.dto.PointDTO;
import com.service.logistservice.service.FlightService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DriverPointsConsumer {
    private ObjectMapper objectMapper;
    private FlightService flightService;

    @KafkaListener(topics = "point", groupId = "logist")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            PointDTO pointDTO = objectMapper.readValue(record.value(), PointDTO.class);
            flightService.addDriverPoints(pointDTO);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
