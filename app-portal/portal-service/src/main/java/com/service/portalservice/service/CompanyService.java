package com.service.portalservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.portalservice.dto.DaDataDto;
import com.service.portalservice.dto.DriverDTO;
import com.service.portalservice.dto.GetCompanyDTO;
import com.service.portalservice.dto.UserDTO;
import com.service.portalservice.exceptions.*;
import com.service.portalservice.mappers.Mapper;
import com.service.portalservice.models.Company;
import com.service.portalservice.models.User;
import com.service.portalservice.models.UserDataBase;
import com.service.portalservice.repository.CompanyRepository;
import com.service.portalservice.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class CompanyService {
    @Autowired
    private KeycloakService keycloakService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyRepository companyRepository;
    private static String ROLE_ADMIN = "ADMIN";
    private static String ROLE_LOGIST = "LOGIST";
    private static String ROLE_DRIVER = "DRIVER";
    @Autowired
    private MailService mailService;
    @Autowired
    private DaDataService daDataService;

    //СОЗДАНИЕ НОВОЙ КОМПАНИИ
    public void createCompany(DaDataDto daDataDto, User user) throws InvalidInnException, UserNotFoundException, KeycloakException {
        Company company = daDataService.getInfo(daDataDto);
        if(company.getName() == null || company.getName().isEmpty()){
            throw new InvalidInnException("Incorrect inn");
        }
            company.setRoleCompany(keycloakService.createRolesForCreatedCompany(company.getInn()));
            companyRepository.save(company);

            userService.addRoleUser(user,ROLE_ADMIN,company.getRoleCompany().get(0));

            mailService.sendSimpleMessage(user.getEmail(),
                    "Create company",
                    "Вы зарегестрировали компанию: " +
                    company.getName());
    }

    // ДОБАВЛЕНИЕ НОВОГО СОТРУДНИКА
    public void addWorkerForCompany(UserDTO userDTO, User user) throws JsonProcessingException,
            ForbiddenException, RoleNotSetException, UserNotFoundException, KeycloakException, UserNotCreatedException {

        UserDataBase userAdmin = userRepository.findById(user.getUserId()).get();

        if(!checkValidRole(userDTO)) {
            throw new ForbiddenException("Такой роли в системе не существует!");
        }
        if(getCompany(userDTO.getCompanyName()) == null) {
            throw new ForbiddenException("Такой компании не существует");
        }

        Company company = getCompany(userDTO.getCompanyName());
        String roleCompany = Mapper.parseRoleOfCompany(company,userDTO.getCompanyRole());

        if(!Mapper.checkRoleForCompany(userAdmin,company.getRoleCompany(),ROLE_ADMIN)) {
            throw new ForbiddenException("У пользователя нет прав добавить нового сотрудника!");
        }

        if(userRepository.findByUsername(userDTO.getUsername()) == null){
            userService.createNewUser(userDTO);
        }

        UserDataBase newWorker = userRepository.findByUsername(userDTO.getUsername());

        if(Mapper.checkRoleForCompany(newWorker,company.getRoleCompany())) {
            throw new RoleNotSetException("Пользователь уже имеет роль в компании!", HttpStatus.FORBIDDEN);
        }

        if(userDTO.getCompanyRole().equals(ROLE_LOGIST)) {
            company.setLogistCount();
            companyRepository.save(company);
        }
        if(userDTO.getCompanyRole().equals(ROLE_DRIVER)) {
            company.setDriverCount();
            companyRepository.save(company);
        }

        company.setWorkersId(newWorker.getUserId());
        newWorker.setRolesToCompany(roleCompany);
        userRepository.save(newWorker);
        companyRepository.save(company);
        keycloakService.setRole(newWorker.getUserId(), userDTO.getCompanyRole(),roleCompany);
    }

    //ДОБАВЛЕНИЕ DRIVER
    public void addDriverForCompany(UserDTO userDTO, User user) throws JsonProcessingException,
            ForbiddenException, RoleNotSetException, UserNotFoundException, KeycloakException, UserNotCreatedException{

        UserDataBase userLogist = userRepository.findById(user.getUserId()).get();

        if(!userDTO.getCompanyRole().equals(ROLE_DRIVER)) {
            throw new ForbiddenException("Вы можете добавить сотрудника только с ролью DRIVER");
        }

        Company company = getCompany(userDTO.getCompanyName());
        String roleCompany = Mapper.parseRoleOfCompany(company,userDTO.getCompanyRole());

        if(!Mapper.checkRoleForCompany(userLogist,company.getRoleCompany(),ROLE_LOGIST)) {
            throw new ForbiddenException("У пользователя нет прав добавить сотрудника в данную компанию!");
        }

        if(userRepository.findByUsername(userDTO.getUsername()) == null){
            userService.createNewUser(userDTO);
        }

        UserDataBase newWorker = userRepository.findByUsername(userDTO.getUsername());

        if(Mapper.checkRoleForCompany(newWorker,company.getRoleCompany())) {
            throw new RoleNotSetException("Пользователь уже имеет роль в компании!", HttpStatus.FORBIDDEN);
        }

        company.setDriverCount();
        company.setWorkersId(newWorker.getUserId());
        companyRepository.save(company);

        newWorker.setRolesToCompany(roleCompany);
        userRepository.save(newWorker);

        keycloakService.setRole(newWorker.getUserId(), userDTO.getCompanyRole(), roleCompany);
    }
    //ПРОВЕРКА ВАЛИДАЦИИ РОЛИ ИЗ ТЕЛА ЗАПРОСА
    public static boolean checkValidRole(UserDTO userDTO){
        String role = userDTO.getCompanyRole();
        return role.equals(ROLE_DRIVER) ||
                role.equals(ROLE_LOGIST) ||
                role.equals(ROLE_ADMIN);
    }
    public Company getCompany(String nameCompany){
        return companyRepository.findByName(nameCompany);
    }

    public DriverDTO getDriverFromDateBase(String userName, User user, String nameCompany) throws UserNotFoundException,
            ForbiddenException {

        UserDataBase userDriver = userRepository.findByUsername(userName);
        UserDataBase userRequest = userRepository.findByUsername(user.getUsername());
        Company company = getCompany(nameCompany);

        if(userDriver == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if(!accessDriver(userRequest,company)) {
            throw new ForbiddenException("У пользователя нет прав в этой компании");
        }
            if(!Mapper.checkRoleForCompany(userDriver,company.getRoleCompany(),"DRIVER")){
            throw new ForbiddenException("Водителя нет в данной компании");
        }
            DriverDTO driverDTO = new DriverDTO();
            driverDTO.setCompanyInn(company.getInn());
            driverDTO.setDriverId(userDriver.getUserId());
            driverDTO.setDriverName(userDriver.getFirstName());
            driverDTO.setDriverLastName(userDriver.getLastName());
        return driverDTO;
    }
    private static boolean accessDriver(UserDataBase userDataBase, Company company){
        return Mapper.checkRoleForCompany(userDataBase, company.getRoleCompany(), "ADMIN") ||
                Mapper.checkRoleForCompany(userDataBase, company.getRoleCompany(), "LOGIST");
    }

    //ИНФОРМАЦИЯ ПО КОМПАНИИ
    public String getCompanyInfo(GetCompanyDTO getCompanyDTO){
      Company company = getCompany(getCompanyDTO.getNameCompany());
        return "Company name: " + company.getName() + ", " +
                "id: " + company.getId() + ", " +
                "INN: " + company.getInn()+ ", " +
                "address: " + company.getAddress() + ", " +
                "kpp: " + company.getKpp() + ", " +
                "ogrn: " + company.getOgrn() + ", " +
                "Drivers: " + company.getDriverCount() + ", " +
                "Logists: " + company.getLogistCount();
    }

    //ИНФОРМАЦИЯ ПО ВСЕМ КОМПАНИЯМ
    public List<String> getAllCompany(){
        List<Company> allCompany = companyRepository.findAll();
        List<String> company = new ArrayList<>();
        for(Company comp: allCompany){
            company.add("Company name: " + comp.getName() + ", " +
                    "id: " + comp.getId() + ", " +
                    "INN: " + comp.getInn());
        }
        return company;
    }


}