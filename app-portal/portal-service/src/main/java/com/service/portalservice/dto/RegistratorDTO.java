package com.service.portalservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistratorDTO {
    @NotEmpty(message = "Имя пользователя не должно быть пустым")
    @Size(min = 3, max = 30)
    private String username;
    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 3, max = 30)
    private String firstName;
    @NotEmpty(message = "Фамилия не должна быть пустым")
    @Size(min = 3, max = 30)
    private String lastName;
    @Email(message = "Не валидная почта")
    private String email;
}