package com.service.portalservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.portalservice.dto.*;
import com.service.portalservice.exceptions.*;
import com.service.portalservice.mappers.Mapper;
import com.service.portalservice.service.DaDataService;
import com.service.portalservice.service.KeycloakService;
import com.service.portalservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private KeycloakService keycloakService;
    @Autowired
    private DaDataService daDataService;

    //СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ С РОЛЬЮ РЕГИСТРАТОР
    @PostMapping("/create")
    public ResponseEntity<String> createRegister(@RequestBody @Valid RegistratorDTO registratorDTO,
                                                 BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.createNewUserRegister(registratorDTO);
            return  new ResponseEntity<>("Пользователь "+ registratorDTO.getUsername() +"успешно создан!", HttpStatus.CREATED);
        } catch (UserNotCreatedException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //ИЗМЕНЕНИЕ ПАРОЛЯ ПОЛЬЗОВАТЕЛЯ
    @PostMapping("/reset/password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDTO passwordDTO,
                                                @RequestHeader HttpHeaders headers,
                                                BindingResult bindingResult) throws JsonProcessingException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.resetPassword(passwordDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>("Пароль успешно изменен!",HttpStatus.OK);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //ИЗМЕНЕНИЕ ДАННЫХ ПОЛЬЗОВАТЕЛЯ
    @PostMapping("/change")
    public ResponseEntity<String> changeUserData(@RequestBody ChangeUserDataDTO changeUserDataDTO,
                                             @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        try {
            userService.changeUserData(changeUserDataDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>("Данныйе пользователя успешно изменены!",HttpStatus.OK);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private String getErrorMessage(BindingResult bindingResult){
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
