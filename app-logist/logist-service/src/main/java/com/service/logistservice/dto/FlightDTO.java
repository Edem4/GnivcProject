package com.service.logistservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlightDTO {
        @NotNull
        private long taskId;

}
