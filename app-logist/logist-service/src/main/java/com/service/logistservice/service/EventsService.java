package com.service.logistservice.service;

import com.sadikov.myLibrary.dto.EventDTO;
import com.sadikov.myLibrary.exceptions.EventNotCreateException;
import com.sadikov.myLibrary.exceptions.FlightNotFoundException;
import com.sadikov.myLibrary.exceptions.TaskNotFoundException;
import com.service.logistservice.model.Events;
import com.service.logistservice.model.Flight;
import com.service.logistservice.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventsService {
    @Autowired
    private  FlightService flightService;
    @Autowired
    private FlightRepository flightRepository;

    @Transactional
    public void addNewEvent(EventDTO eventDTO) throws EventNotCreateException, FlightNotFoundException, TaskNotFoundException {
        Flight flight = flightService.getFlight(eventDTO.getFlightId());

        if(!flight.getDriverId().equals(eventDTO.getDriverId())){
            throw new EventNotCreateException("У вас нет доступа к этому рейсу!");
        }

        if(flight.getEndTime() != null){
            throw new EventNotCreateException("Данный рейс завершен!");
        }

        Events event = new Events();
        event.setTimeEvent(LocalDateTime.now());
        event.setStatus(eventDTO.getStatus().name());
        switch (eventDTO.getStatus()){
            case START -> flight.setStartTime(event.getTimeEvent());
            case END -> flight.setEndTime(event.getTimeEvent());
        }

        flight.setEvents(event);
        flightRepository.save(flight);
    }
}
