package com.service.logistservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CompanyDTO {
    @NotEmpty
    public String nameCompany;
    public CompanyDTO(String companyName) {
        nameCompany = companyName;
    }
}
