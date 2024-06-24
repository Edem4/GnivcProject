package com.service.logistservice.mapper;

import com.sadikov.myLibrary.dto.CarDTO;
import com.sadikov.myLibrary.dto.DriverDTO;
import com.sadikov.myLibrary.dto.GetTaskDTO;
import com.service.logistservice.dto.TasksDTO;
import com.service.logistservice.model.Tasks;

import java.time.LocalDateTime;

public class Mapper {
    public static GetTaskDTO convertTaskDTO(Tasks task){
        GetTaskDTO taskDTO = new GetTaskDTO();
        taskDTO.setNameCompany(task.getCompanyName());
        taskDTO.setEndPoint(task.getEndPoint().toString());
        taskDTO.setStartPoint(task.getStartPoint().toString());
        taskDTO.setDriverName(task.getFirstNameDriver());
        taskDTO.setDescriptionCargo(task.getDescriptionCargo());
        taskDTO.setCarNumber(task.getNumberAuto());

        return taskDTO;
    }

    public static Tasks buildToTask(TasksDTO tasksDTO, DriverDTO driverDTO, CarDTO carDTO) {
        Tasks tasks = new Tasks();

        tasks.setCreateTime(LocalDateTime.now());
        tasks.setCompanyName(tasksDTO.getNameCompany());
        tasks.setCompanyInn(driverDTO.getCompanyInn());
        tasks.setStartPoint(tasksDTO.getStartPoint());
        tasks.setEndPoint(tasksDTO.getEndPoint());
        tasks.setDescriptionCargo(tasksDTO.getDescriptionCargo());
        tasks.setNumberAuto(carDTO.getNumberAuto());
        tasks.setFirstNameDriver(driverDTO.getDriverFirstName());
        tasks.setLastNameDriver(driverDTO.getDriverLastName());
        tasks.setDriverId(driverDTO.getDriverId());

        return tasks;
    }
}
