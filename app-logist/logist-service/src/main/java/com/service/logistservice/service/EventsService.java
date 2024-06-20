package com.service.logistservice.service;

import com.service.logistservice.exceptions.EventNotCreateException;
import com.service.logistservice.model.Events;
import com.service.logistservice.model.Flight;
import com.service.logistservice.model.Status;
import com.service.logistservice.repository.EventsRepository;
import com.service.logistservice.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventsService {
    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private FlightRepository flightRepository;

    public void addNewEvent(Flight flight, Status status) throws EventNotCreateException {
        if(flight.getEndTime() != null){
            throw new EventNotCreateException("Данный рейс завершен!");
        }
        Events event = new Events();
        event.setTimeEvent(LocalDateTime.now());
        event.setStatus(status.name());
        switch (status){
            case START -> flight.setStartTime(event.getTimeEvent());
            case END -> flight.setEndTime(event.getTimeEvent());
        }
        flight.setEvents(event);
        flightRepository.save(flight);
    }
}
