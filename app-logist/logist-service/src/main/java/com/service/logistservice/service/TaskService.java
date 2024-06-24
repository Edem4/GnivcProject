package com.service.logistservice.service;

import com.sadikov.myLibrary.dto.CarDTO;
import com.sadikov.myLibrary.dto.DriverDTO;
import com.sadikov.myLibrary.exceptions.TaskNotCreatedException;
import com.sadikov.myLibrary.exceptions.TaskNotFoundException;
import com.sadikov.myLibrary.exceptions.TaskOfAnotherCompanyException;
import com.sadikov.myLibrary.mapper.Mappers;
import com.sadikov.myLibrary.model.User;
import com.service.logistservice.dto.TasksDTO;
import com.service.logistservice.mapper.Mapper;
import com.service.logistservice.model.Tasks;
import com.service.logistservice.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    public TasksRepository tasksRepository;

    @Transactional
    public void createTask(TasksDTO tasksDTO, DriverDTO driverDTO, CarDTO carDTO, User user) throws TaskNotCreatedException {
        if(!Mappers.checkLogistForCompany(user, driverDTO.getCompanyInn())){
            throw new TaskNotCreatedException("You do not have permission to create a task in this company.!");
        }
        Tasks tasks = Mapper.buildToTask(tasksDTO, driverDTO, carDTO);
        tasksRepository.save(tasks);
    }
    @Transactional
    public void saveTask(Tasks tasks){
        tasksRepository.save(tasks);
    }

    @Transactional(readOnly = true)
    public Tasks getTask(Long taskId, User user) throws TaskNotFoundException, TaskOfAnotherCompanyException {

        Optional<Tasks> tasks = tasksRepository.findById(taskId);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("Task not found!");
        }

        if (!(Mappers.checkLogistForCompany(user, tasks.get().getCompanyInn()) ||
        Mappers.checkDriverForCompany(user, tasks.get().getCompanyInn()))) {
            throw new TaskOfAnotherCompanyException("This task is from another company!");
        }
        return tasks.get();
    }

    @Transactional(readOnly = true)
    public Tasks getTask(Long taskId) throws TaskNotFoundException {
        Optional<Tasks> tasks = tasksRepository.findById(taskId);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("There is no such task!");
        }
        return tasks.get();
    }
    @Transactional(readOnly = true)
    public List<Tasks> getAllTasksCompany(Integer offset, Integer limit, User user, String companyName) throws TaskNotFoundException,
            TaskOfAnotherCompanyException {
        List<Tasks> tasks = tasksRepository.findByCompanyName(companyName);
        if(tasks.isEmpty()){
            throw new TaskNotFoundException("The company has no tasks!");
        }
         if(!Mappers.checkLogistForCompany(user,tasks.get(0).getCompanyInn())){
             throw new TaskOfAnotherCompanyException("You are not a logist for this company!");
         }
        Pageable pageable = PageRequest.of(offset,limit);
        return tasksRepository.findBySomeCompanyName(companyName,pageable).getContent();
    }

    @Transactional(readOnly = true)
    public List<Tasks> getAllTasksDriver(User user) throws TaskNotFoundException,
            TaskOfAnotherCompanyException {

        List<Tasks> tasks = tasksRepository.findByDriverId(user.getUserId());
        if(tasks.isEmpty()){
            throw new TaskNotFoundException("The driver has no tasks!");
        }
        return tasks;
    }

    @Transactional(readOnly = true)
    public String getInnCompany(String companyName) throws TaskNotFoundException {
        List<Tasks> tasks = tasksRepository.findByCompanyName(companyName);

        if(tasks.isEmpty()){
            throw new TaskNotFoundException("The company has no tasks!");
        }
        return tasks.get(0).getCompanyInn();
    }
    @Transactional(readOnly = true)
    public int countTasksToday(String companyName){
        return tasksRepository.findByTasksToday(companyName).size();
    }

}
