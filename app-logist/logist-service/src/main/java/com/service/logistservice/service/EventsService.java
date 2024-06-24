package com.service.logistservice.service;

import com.sadikov.myLibrary.dto.EventDTO;
import com.sadikov.myLibrary.exceptions.EventNotCreateException;
import com.sadikov.myLibrary.exceptions.FlightNotFoundException;
import com.sadikov.myLibrary.exceptions.TaskNotFoundException;
import com.sadikov.myLibrary.model.Status;
import com.service.logistservice.model.Events;
import com.service.logistservice.model.Flight;
import com.service.logistservice.repository.EventsRepository;
import com.service.logistservice.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EventsService {
    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private  FlightService flightService;
    @Autowired
    private FlightRepository flightRepository;

    @Transactional
    public void addNewEvent(EventDTO eventDTO) throws EventNotCreateException, FlightNotFoundException, TaskNotFoundException {
        Flight flight = flightService.getFlight(eventDTO.getFlightId());

        if(!flight.getDriverId().equals(eventDTO.getDriverId())){
            throw new EventNotCreateException("You do not have access to this flight!");
        }

        if(flightService.getLastStatus(flight).equals(Status.CANCEL)){
            throw new EventNotCreateException("This flight has been canceled!");
        }

        if(flight.getEndTime() != null){
            throw new EventNotCreateException("This flight has ended!");
        }

        Events event = new Events();
        event.setTimeEvent(LocalDateTime.now());
        event.setStatus(eventDTO.getStatus().name());
        event.setCompanyName(flight.getCompanyName());

        switch (eventDTO.getStatus()){
            case START -> flight.setStartTime(event.getTimeEvent());
            case END -> flight.setEndTime(event.getTimeEvent());
        }

        flight.setEvents(event);
        flightRepository.save(flight);
    }
    @Transactional(readOnly = true)
    public int countEventToday(Status status,String companyName){
        return eventsRepository.findLastEventsByStatus(status.name(),companyName).size();
    }

}
