package com.service.logistservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.service.logistservice.dto.CarDTO;
import com.service.logistservice.dto.DriverDTO;
import com.service.logistservice.dto.TasksDTO;
import com.service.logistservice.model.Tasks;
import com.service.logistservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.List;

public class Mapper {
    @Autowired
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private static final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class,String.class);

    public static User getUserFromHeaders(HttpHeaders headers) throws JsonProcessingException {
        User  user = new User();
        user.setUserId(headers.get("userId").get(0));
        user.setUsername(headers.get("userName").get(0));
        user.setEmail(headers.get("userEmail").get(0));
        user.setRoles(objectMapper.readValue(headers.get("roles").get(0), collectionType));
        return user;
    }

    public static boolean checkLogistForCompany(User user,String companyInn){

        for(String role: user.getRoles()){
            String str2 = String.format("%.6s",role);
            if(str2.equalsIgnoreCase("logist")){
               String str = role.replace("LOGIST","");
                if(str.equals(companyInn)){
                    return true;
                }
            }
        }
        return false;
    }

    public static Tasks buildToTask(TasksDTO tasksDTO, DriverDTO driverDTO, CarDTO carDTO) {
        Tasks tasks = new Tasks();

        tasks.setCompanyName(tasksDTO.getNameCompany());
        tasks.setCompanyInn(driverDTO.getCompanyInn());
        tasks.setStartPoint(tasksDTO.getStartPoint());
        tasks.setEndPoint(tasksDTO.getEndPoint());
        tasks.setDescriptionCargo(tasksDTO.getDescriptionCargo());
        tasks.setNumberAuto(carDTO.getNumberAuto());
        tasks.setFirstNameDriver(driverDTO.getDriverName());
        tasks.setLastNameDriver(driverDTO.getDriverLastName());

        return tasks;
    }
}
