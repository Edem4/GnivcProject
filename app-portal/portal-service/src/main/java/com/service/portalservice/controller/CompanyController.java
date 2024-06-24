package com.service.portalservice.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.portalservice.dto.DaDataDto;
import com.service.portalservice.dto.GetCompanyDTO;
import com.service.portalservice.dto.UserDTO;
import com.service.portalservice.exceptions.*;
import com.service.portalservice.mappers.Mapper;
import com.service.portalservice.models.User;
import com.service.portalservice.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    //СОЗДАНИЕ КОМПАНИИ
    @PostMapping("/create")
    public ResponseEntity<String> createCompany(@RequestBody @Valid DaDataDto daDataDto,
                                        @RequestHeader HttpHeaders headers,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(Objects.requireNonNull(bindingResult.getFieldError()).getField(), HttpStatus.BAD_REQUEST);
        }
        try {
            User user = Mapper.getUserFromHeaders(headers);
            companyService.createCompany(daDataDto, user);
            return new ResponseEntity<>("Company created by user: "+ user.getUsername() , HttpStatus.CREATED);

        } catch (InvalidInnException | UserNotFoundException | JsonProcessingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (KeycloakException e) {
            throw new RuntimeException(e);
        }
    }


    //ДОБАВЛЕНИЕ НОВОГО СОТРУДНИКА В КОМПАНИЮ
    @PostMapping("/add/worker")
    public ResponseEntity<String> addWorker(@RequestBody @Valid UserDTO userDTO,
                                            @RequestHeader HttpHeaders headers, BindingResult bindingResult)  {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(Objects.requireNonNull(bindingResult.getFieldError()).getField(), HttpStatus.BAD_REQUEST);
        }

        try {
            User user = Mapper.getUserFromHeaders(headers);
            if(user.checkAuthority("ADMIN")){
                companyService.addWorkerForCompany(userDTO, user);
            }else {
                companyService.addDriverForCompany(userDTO, user);
            }
            return new ResponseEntity<>("User added to company " + userDTO.getCompanyName(), HttpStatus.OK);

        }catch (JsonProcessingException | ForbiddenException |
                RoleNotSetException | UserNotFoundException | UserNotCreatedException  e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (KeycloakException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/driver/get/{name}")
    public ResponseEntity<?> getDrivers(@PathVariable("name") String name,
                                        @RequestHeader HttpHeaders headers,
                                        @RequestBody GetCompanyDTO getCompanyDTO){
        try {
            return new ResponseEntity<>(
                    companyService.getDriverFromDateBase(name,
                            Mapper.getUserFromHeaders(headers),
                            getCompanyDTO.getNameCompany()),
                    HttpStatus.OK
            );
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }catch (JsonProcessingException | UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getCompany(@RequestBody GetCompanyDTO getCompanyDTO,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(Objects.requireNonNull(bindingResult.getFieldError()).getField(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(companyService.getCompanyInfo(getCompanyDTO), HttpStatus.OK);
    }


    @GetMapping("/get/all")
    public ResponseEntity<?> getAllCompany() {
        return new ResponseEntity<>(companyService.getAllCompany(), HttpStatus.OK);
    }


}
