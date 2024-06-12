package com.service.portalservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    @NotEmpty(message = "Пароль не должен быть пустым!")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов!")
    private String newPassword;
}
