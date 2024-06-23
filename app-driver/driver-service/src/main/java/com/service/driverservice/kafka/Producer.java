package com.service.driverservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadikov.myLibrary.dto.EventDTO;
import com.sadikov.myLibrary.dto.PointDTO;
import com.sadikov.myLibrary.mapper.Mappers;
import com.sadikov.myLibrary.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    public void eventMessage(EventDTO eventDTO) throws JsonProcessingException {
         kafkaTemplate.send("event", objectMapper.writeValueAsString(eventDTO));
    }

    public void pointMessage(PointDTO pointDTO) throws JsonProcessingException {
        kafkaTemplate.send("point", objectMapper.writeValueAsString(pointDTO));
    }
}
