package com.service.logistservice.service;

import com.sadikov.myLibrary.dto.StatisticDTO;
import com.sadikov.myLibrary.exceptions.ForbiddenException;
import com.sadikov.myLibrary.exceptions.TaskNotFoundException;
import com.sadikov.myLibrary.mapper.Mappers;
import com.sadikov.myLibrary.model.Status;
import com.sadikov.myLibrary.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    @Autowired
    private EventsService eventsService;

    @Autowired
    private TaskService taskService;

    public StatisticDTO getStatistic(String companyName, User user) throws TaskNotFoundException, ForbiddenException {
        if(!accessToStatistics(companyName,user)){
            throw new ForbiddenException(user.getUsername() + " not workers of the company " + companyName);
        }
        StatisticDTO statistic = new StatisticDTO();

        statistic.setCountTasks(taskService.countTasksToday(companyName));
        statistic.setCompanyName(companyName);
        statistic.setCountStartEvent(eventsService.countEventToday(Status.START,companyName));
        statistic.setCountEndEvent(eventsService.countEventToday(Status.END,companyName));
        statistic.setCountCancelEvent(eventsService.countEventToday(Status.CANCEL,companyName));

        return statistic;
    }

    public boolean accessToStatistics(String companyName, User user) throws TaskNotFoundException {
        return Mappers.checkRoleForCompany(user, taskService.getInnCompany(companyName), "ADMIN") ||
                Mappers.checkRoleForCompany(user,taskService.getInnCompany(companyName),"LOGIST");
    }
}
