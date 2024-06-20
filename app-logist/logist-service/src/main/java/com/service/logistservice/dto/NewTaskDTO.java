package com.service.logistservice.dto;

import com.service.logistservice.model.Points;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class NewTaskDTO {

    private String companyName;

    private Points startPoint;
    private Points endPoint;
    private String firstNameDriver;
    private String lastNameDriver;
    private String descriptionCargo;
    private String numberAuto;
}
