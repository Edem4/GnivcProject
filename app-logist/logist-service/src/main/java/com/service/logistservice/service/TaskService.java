package com.service.logistservice.service;

import com.service.logistservice.dto.CarDTO;
import com.service.logistservice.dto.DriverDTO;
import com.service.logistservice.dto.TasksDTO;
import com.service.logistservice.exceptions.TaskNotCreatedException;
import com.service.logistservice.exceptions.TaskNotFoundException;
import com.service.logistservice.exceptions.TaskOfAnotherCompanyException;
import com.service.logistservice.mapper.Mapper;
import com.service.logistservice.model.Tasks;
import com.service.logistservice.model.User;
import com.service.logistservice.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    public TasksRepository tasksRepository;

    public void createTask(TasksDTO tasksDTO, DriverDTO driverDTO, CarDTO carDTO, User user) throws TaskNotCreatedException {
        if(!Mapper.checkLogistForCompany(user, driverDTO.getCompanyInn())){
            throw new TaskNotCreatedException("Нет прав на создания задания в этой компании!");
        }
        Tasks tasks = Mapper.buildToTask(tasksDTO, driverDTO, carDTO);
        tasksRepository.save(tasks);
    }

    public void saveTask(Tasks tasks){
        tasksRepository.save(tasks);
    }


    public Tasks getTask(Long taskId, User user) throws TaskNotFoundException, TaskOfAnotherCompanyException {

        Optional<Tasks> tasks = tasksRepository.findById(taskId);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("Задание не найдено!");
        }
        if (!Mapper.checkLogistForCompany(user, tasks.get().getCompanyInn())) {
            throw new TaskOfAnotherCompanyException("Данное задание другой компании!");
        }
        return tasks.get();
    }

    public Tasks getTask(Long taskId) throws TaskNotFoundException {
        Optional<Tasks> tasks = tasksRepository.findById(taskId);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("Такого задания не существует!");
        }
        return tasks.get();
    }

    public List<Tasks> getAllTasks(Integer offset, Integer limit, User user, String companyName) throws TaskNotFoundException,
            TaskOfAnotherCompanyException {
        if(tasksRepository.findByCompanyName(companyName).isEmpty()){
            throw new TaskNotFoundException("У данной компании нет заданий!");
        }

         if(!Mapper.checkLogistForCompany(user,tasksRepository.findByCompanyName(companyName).get(0).getCompanyInn())){
             throw new TaskOfAnotherCompanyException("Вы не логист данной компании!");
         }
        Pageable pageable = PageRequest.of(offset,limit);
        return tasksRepository.findBySomeCompanyName(companyName,pageable).getContent();
    }

}
