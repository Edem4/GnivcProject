package com.service.portalservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GetCompanyDTO {
    @NotEmpty(message = "Название компании не должно быть пустым")
    public String nameCompany;
}