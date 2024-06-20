package com.service.logistservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.logistservice.dto.EventDTO;
import com.service.logistservice.exceptions.EventNotCreateException;
import com.service.logistservice.exceptions.FlightNotFoundException;
import com.service.logistservice.service.EventsService;
import com.service.logistservice.service.FlightService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventsConsumer {
    private final FlightService flightService;
    private final EventsService eventsService;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventsConsumer(FlightService flightService, EventsService eventService, ObjectMapper objectMapper) {
        this.flightService = flightService;
        this.eventsService = eventService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "event", groupId = "logist")
    public void listen(ConsumerRecord<String, String> record){
        try {
            EventDTO eventDTO = objectMapper.readValue(record.value(), EventDTO.class);
            eventsService.addNewEvent(flightService.getFlight(eventDTO.getFlightId()),eventDTO.getStatus());

        } catch (FlightNotFoundException | JsonProcessingException | EventNotCreateException  e) {
            System.out.println(e.getMessage());
        }

    }
}
