package com.service.logistservice.service;

import com.sadikov.myLibrary.dto.FlightDTO;
import com.sadikov.myLibrary.dto.PointDTO;
import com.sadikov.myLibrary.exceptions.FlightNotFoundException;
import com.sadikov.myLibrary.exceptions.PointsNotFoundException;
import com.sadikov.myLibrary.exceptions.TaskNotFoundException;
import com.sadikov.myLibrary.exceptions.TaskOfAnotherCompanyException;
import com.sadikov.myLibrary.model.Status;
import com.sadikov.myLibrary.model.User;
import com.service.logistservice.dto.GetFlightDTO;
import com.service.logistservice.model.*;
import com.service.logistservice.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private TaskService taskService;
    @Transactional
    public void createFlight(FlightDTO flightDTO, User user) throws TaskOfAnotherCompanyException, TaskNotFoundException, FlightNotFoundException {

        Tasks task = taskService.getTask(flightDTO.getTaskId(), user);
        if(!task.getDriverId().equals(user.getUserId())){
            throw new FlightNotFoundException("This task of another driver!");
        }

        Events event = new Events();
        Flight flight = new Flight();

        event.setTimeEvent(flight.getCreateTime());
        event.setStatus(Status.CREATED.name());
        event.setCompanyName(task.getCompanyName());

        flight.setDriverId(task.getDriverId());
        flight.setCompanyName(task.getCompanyName());
        flight.setEvents(event);
        task.setFlights(flight);

        taskService.saveTask(task);
    }
    @Transactional
    public void addDriverPoints(PointDTO pointDTO) throws FlightNotFoundException {
            Points points = new Points(pointDTO.getLat(),pointDTO.getLon());
            Flight flight = getFlight(pointDTO.getFlightId());

            if(!flight.getDriverId().equals(pointDTO.getDriverId())){
                throw new FlightNotFoundException("You do not have access to this flight!");
            }

            flight.setDriverLocation(points);
            flightRepository.save(flight);
    }
    @Transactional(readOnly = true)
    public GetFlightDTO getFlight(Long flightId, Long tasksId, User user) throws TaskNotFoundException,
            TaskOfAnotherCompanyException,
            FlightNotFoundException {

        Tasks tasks = taskService.getTask(tasksId, user);
        Flight flight = getFlight(flightId);
        GetFlightDTO getFlightDTO = new GetFlightDTO();

        if (!tasks.getFlights().contains(flight)) {
            throw new TaskOfAnotherCompanyException("This flight is not from this task!");
        }

        getFlightDTO.setId(flight.getId());
        getFlightDTO.setStart(flight.getStartTime());
        getFlightDTO.setCreate(flight.getCreateTime());
        getFlightDTO.setEnd(flight.getEndTime());
        getFlightDTO.setStatusNow(getLastStatus(flight));
        return getFlightDTO;
    }

    public Status getLastStatus(Flight flight){
       return Status.valueOf(flight.getEvents().stream().reduce((a, b) -> b).get().getStatus());
    }

    @Transactional(readOnly = true)
    public Flight getFlight(long flightId) throws FlightNotFoundException {

        Optional<Flight> flight = flightRepository.findById(flightId);

        if (flight.isEmpty()) {
            throw new FlightNotFoundException("Flight not found!");
        }
        return flight.get();
    }

    public List<Flight> getAllFlight(User user, Long tasksId) throws TaskOfAnotherCompanyException,
            FlightNotFoundException, TaskNotFoundException {

        if (taskService.getTask(tasksId, user).getFlights().isEmpty()) {
            throw new FlightNotFoundException("This task has no flights!");
        }
        return taskService.getTask(tasksId).getFlights();

    }

    public List<Points> getPointDriver(Long flightId, Long taskId, User user) throws FlightNotFoundException,
            PointsNotFoundException, TaskNotFoundException, TaskOfAnotherCompanyException {

        Tasks tasks = taskService.getTask(taskId, user);
        Flight flight = getFlight(flightId);

        if (!tasks.getFlights().contains(flight)) {
            throw new TaskOfAnotherCompanyException("This flight is not from this task!");
        }

        if (flight.getDriverLocation().isEmpty()) {
            throw new PointsNotFoundException("Geolocation points not found!");
        }
        return flight.getDriverLocation();
    }
}
