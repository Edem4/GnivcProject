package com.service.portalservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DaDataDto {
    @NotEmpty(message = "Укажите ИНН")
    private String query;
}
