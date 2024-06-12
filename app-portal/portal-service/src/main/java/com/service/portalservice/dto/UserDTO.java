package com.service.portalservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class UserDTO {
    @NotEmpty(message = "Имя пользователя не должно быть пустым")
    private String username;
    @NotEmpty(message = "Фамилия не должна быть пустой")
    private String lastname;
    @NotEmpty(message = "Имя не должно быть пустым")
    private String firstname;
    @Email(message = "Не валидная почта")
    private String email;
    @NotEmpty(message = "Название компании не должно быть пустым!")
    private String companyName;
    @NotEmpty(message = "Роль компании не должна быть пустой")
    private String companyRole;
}