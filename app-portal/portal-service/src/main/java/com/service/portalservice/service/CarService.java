package com.service.portalservice.service;


import com.service.portalservice.exceptions.CarAlreadyExistException;
import com.service.portalservice.exceptions.CarNotFoundExceptions;
import com.service.portalservice.exceptions.ForbiddenException;
import com.service.portalservice.exceptions.UserNotFoundException;
import com.service.portalservice.mappers.Mapper;
import com.service.portalservice.models.Car;
import com.service.portalservice.models.Company;
import com.service.portalservice.models.User;
import com.service.portalservice.models.UserDataBase;
import com.service.portalservice.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CarRepository carRepository;
    public void addCar(Car car, User user) throws UserNotFoundException, ForbiddenException, CarAlreadyExistException{

        UserDataBase userDataBase = userService.getUserFromDateBase(user.getUserId());

            if (!accessCar(userDataBase,companyService.getCompany(car.getCompany()))){
                throw new ForbiddenException("В данной компании вы не можете добавить автомобиль!");
            }
            if (carRepository.findByVin(car.getVin()) != null) {
                throw new CarAlreadyExistException("Автомобиль уже принадлежит компании!");
            }

        carRepository.save(car);
    }

    public Car getCar(User user, String companyName) throws CarNotFoundExceptions, ForbiddenException, UserNotFoundException {

        UserDataBase userDataBase = userService.getUserFromDateBase(user.getUserId());
        Car car = carRepository.findByCompany(companyName);

        if(car == null) {
            throw new CarNotFoundExceptions("Автомобиль не найден!");
        }

        if(accessCar(userDataBase,companyService.getCompany(car.getCompany()))) {
            throw new ForbiddenException("Forbidden");
        }
        return car;
    }

    private static boolean accessCar(UserDataBase userDataBase, Company company){
        return Mapper.checkRoleForCompany(userDataBase, company.getRoleCompany(), "ADMIN") ||
                Mapper.checkRoleForCompany(userDataBase, company.getRoleCompany(), "LOGIST");
    }
}