package com.service.logistservice.dto;


import com.service.logistservice.model.Points;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TasksDTO {
    @NotEmpty
    @NotNull
    private String nameCompany;
    @NotNull
    private Points startPoint;
    @NotNull
    private Points endPoint;
    @NotNull
    private String driverName;
    @NotNull
    private String carNumber;
    @NotEmpty
    private String descriptionCargo;
}
