package com.service.logistservice.service;

import com.service.logistservice.dto.FlightDTO;
import com.service.logistservice.dto.GetFlightDTO;
import com.service.logistservice.dto.PointDTO;
import com.service.logistservice.exceptions.FlightNotFoundException;
import com.service.logistservice.exceptions.PointsNotFoundException;
import com.service.logistservice.exceptions.TaskNotFoundException;
import com.service.logistservice.exceptions.TaskOfAnotherCompanyException;
import com.service.logistservice.model.*;
import com.service.logistservice.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private TaskService taskService;

    public void createFlight(FlightDTO flightDTO, User user) throws TaskOfAnotherCompanyException, TaskNotFoundException {

        Tasks task = taskService.getTask(flightDTO.getTaskId(), user);

        Events event = new Events();
        Flight flight = new Flight();

        event.setTimeEvent(flight.getCreateTime());
        event.setStatus(Status.CREATED.name());

        flight.setEvents(event);
        task.setFlights(flight);

        taskService.saveTask(task);
    }

    public void addDriverPoints(PointDTO pointDTO) {
        try {
            Points points = new Points(pointDTO.getCoordinates());
            Flight flight = getFlight(pointDTO.getFlightId());

            flight.setDriverLocation(points);
            flightRepository.save(flight);
        } catch (FlightNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public GetFlightDTO getFlight(Long flightId, Long tasksId, User user) throws TaskNotFoundException,
            TaskOfAnotherCompanyException,
            FlightNotFoundException {

        Tasks tasks = taskService.getTask(tasksId, user);
        Flight flight = getFlight(flightId);
        GetFlightDTO getFlightDTO = new GetFlightDTO();

        if (!tasks.getFlights().contains(flight)) {
            throw new TaskOfAnotherCompanyException("Данный рейс не из этого задания!");
        }

        getFlightDTO.setId(flight.getId());
        getFlightDTO.setStart(flight.getStartTime());
        getFlightDTO.setCreate(flight.getCreateTime());
        getFlightDTO.setEnd(flight.getEndTime());
        getFlightDTO.setStatusNow(Status.valueOf(flight.getEvents().stream().reduce((a, b) -> b).get().getStatus()));
        return getFlightDTO;
    }

    public Flight getFlight(long flightId) throws FlightNotFoundException {

        Optional<Flight> flight = flightRepository.findById(flightId);

        if (flight.isEmpty()) {
            throw new FlightNotFoundException("Рейс не был найден!");
        }
        return flight.get();
    }

    public List<Flight> getAllFlight(User user, Long tasksId) throws TaskOfAnotherCompanyException,
            FlightNotFoundException, TaskNotFoundException {

        if (taskService.getTask(tasksId, user).getFlights().isEmpty()) {
            throw new FlightNotFoundException("У данного задания нет рейсов!");
        }
        return taskService.getTask(tasksId).getFlights();

    }

    public List<Points> getPointDriver(Long flightId, Long taskId, User user) throws FlightNotFoundException,
            PointsNotFoundException, TaskNotFoundException, TaskOfAnotherCompanyException {

        Tasks tasks = taskService.getTask(taskId, user);
        Flight flight = getFlight(flightId);

        if (!tasks.getFlights().contains(flight)) {
            throw new TaskOfAnotherCompanyException("Данный рейс не из этого задания!");
        }

        if (flight.getDriverLocation().isEmpty()) {
            throw new PointsNotFoundException("Точки Геопозиции не были найденны!");
        }
        return flight.getDriverLocation();
    }
}
