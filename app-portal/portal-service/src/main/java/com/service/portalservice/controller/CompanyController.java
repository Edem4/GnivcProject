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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

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
            return new ResponseEntity<>(getErrorMessageCompany(bindingResult), HttpStatus.BAD_REQUEST);
        }
        try {
            User user = Mapper.getUserFromHeaders(headers);
            companyService.createCompany(daDataDto, user);
            return new ResponseEntity<>("Компания создана пользователем: "+ user.getUsername() , HttpStatus.CREATED);

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
            return new ResponseEntity<>(getErrorMessageCompany(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {
            User user = Mapper.getUserFromHeaders(headers);
            if(user.checkAuthority("ADMIN")){
                companyService.addWorkerForCompany(userDTO, user);
            }else {
                companyService.addDriverForCompany(userDTO, user);
            }
            return new ResponseEntity<>("Пользователь добавлен в компанию " + userDTO.getCompanyName(), HttpStatus.OK);

        }catch (JsonProcessingException | ForbiddenException |
                RoleNotSetException | UserNotFoundException | UserNotCreatedException  e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (KeycloakException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/get")
    public ResponseEntity<?> getCompany(@RequestBody GetCompanyDTO getCompanyDTO) {
        return new ResponseEntity<>(companyService.getCompanyInfo(getCompanyDTO), HttpStatus.OK);
    }


    @GetMapping("/get/all")
    public ResponseEntity<?> getAllCompany() {
        return new ResponseEntity<>(companyService.getAllCompany(), HttpStatus.OK);
    }

    private String getErrorMessageCompany(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getField())
                    .append(" - ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return stringBuilder.toString();
    }

}































//
//    @GetMapping("/get/all/paging")
//    public ResponseEntity<List<MinCompanyDTO>> getCompanies(@RequestBody GetPagingDTO pagingDTO) {
//        return new ResponseEntity<>(companyService.getCompanies(pagingDTO), HttpStatus.OK);
//    }

