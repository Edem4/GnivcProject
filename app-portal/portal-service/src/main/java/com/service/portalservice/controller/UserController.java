package com.service.portalservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.service.portalservice.dto.ChangeUserDataDTO;
import com.service.portalservice.dto.RegistratorDTO;
import com.service.portalservice.dto.ResetPasswordDTO;
import com.service.portalservice.exceptions.UserNotCreatedException;
import com.service.portalservice.exceptions.UserNotFoundException;
import com.service.portalservice.mappers.Mapper;
import com.service.portalservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    //СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ С РОЛЬЮ РЕГИСТРАТОР
    @PostMapping("/create")
    public ResponseEntity<String> createRegister(@RequestBody @Valid RegistratorDTO registratorDTO,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(Objects.requireNonNull(bindingResult.getFieldError()).getField(), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.createNewUserRegister(registratorDTO);
            return  new ResponseEntity<>("User "+ registratorDTO.getUsername() +"successfully created!", HttpStatus.CREATED);
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
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(Objects.requireNonNull(bindingResult.getFieldError()).getField(), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.resetPassword(passwordDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>("Password changed successfully!",HttpStatus.OK);
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
            return new ResponseEntity<>("User data has been successfully changed!",HttpStatus.OK);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
