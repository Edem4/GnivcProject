package com.service.logistservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sadikov.myLibrary.dto.CarDTO;
import com.sadikov.myLibrary.dto.DriverDTO;
import com.sadikov.myLibrary.exceptions.TaskNotCreatedException;
import com.sadikov.myLibrary.exceptions.TaskNotFoundException;
import com.sadikov.myLibrary.exceptions.TaskOfAnotherCompanyException;
import com.sadikov.myLibrary.mapper.Mappers;
import com.sadikov.myLibrary.model.User;
import com.service.logistservice.dto.CompanyDTO;
import com.service.logistservice.dto.TasksDTO;
import com.service.logistservice.mapper.Mapper;
import com.service.logistservice.model.Tasks;
import com.service.logistservice.portal.PortalClient;
import com.service.logistservice.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final PortalClient portalClient;

    @Autowired
    public TaskController(TaskService taskService, PortalClient portalClient) {
        this.taskService = taskService;
        this.portalClient = portalClient;
    }

    //СОЗДАНИЕ НОВОГО ЗАДАНИЯ
    @PostMapping("/create")
    public ResponseEntity<String> createTask(@RequestBody @Valid TasksDTO tasksDTO,
                                             @RequestHeader HttpHeaders headers,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(Objects.requireNonNull(bindingResult.getFieldError()).getField(), HttpStatus.BAD_REQUEST);
        }
        try {

            DriverDTO driverDTO = portalClient.findDriverByName(tasksDTO.getDriverName(),
                    headers.toSingleValueMap(),
                    new CompanyDTO(tasksDTO.getNameCompany()));

            CarDTO carDTO =
                    portalClient.findCarByNumber(tasksDTO.getCarNumber(),
                            headers.toSingleValueMap());

            taskService.createTask(tasksDTO, driverDTO, carDTO, Mappers.getUserFromHeaders(headers));

            return new ResponseEntity<>("Task created!", HttpStatus.CREATED);

        } catch (ForbiddenException | JsonProcessingException | TaskNotCreatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getTask(@PathVariable("id") Long tasksId,
                                     @RequestHeader HttpHeaders headers) {

        try {
            User user = Mappers.getUserFromHeaders(headers);
            return new ResponseEntity<>(taskService.getTask(tasksId, user), HttpStatus.OK);
        } catch (TaskNotFoundException | JsonProcessingException | TaskOfAnotherCompanyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/get/all")
    public List<Tasks> getAll(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(100) Integer limit,
            @RequestParam("companyName") String companyName,
            @RequestHeader HttpHeaders headers
    ) {
        try {
            User user = Mappers.getUserFromHeaders(headers);
            return taskService.getAllTasksCompany(offset, limit, user, companyName);
        } catch (JsonProcessingException | TaskNotFoundException | TaskOfAnotherCompanyException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/driver")
    public ResponseEntity<?> getAllTasksDriver(@RequestHeader HttpHeaders headers) {
        try {
            User user = Mappers.getUserFromHeaders(headers);
            return new ResponseEntity<>(taskService.
                    getAllTasksDriver(user)
                    .stream().map(Mapper::convertTaskDTO).
                    collect(Collectors.toList()),HttpStatus.OK);

        } catch (JsonProcessingException | TaskNotFoundException | TaskOfAnotherCompanyException e) {
            throw new RuntimeException(e);
        }
    }
}
