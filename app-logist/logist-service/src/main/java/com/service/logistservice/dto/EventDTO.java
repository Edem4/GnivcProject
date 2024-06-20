package com.service.logistservice.dto;

import com.service.logistservice.model.Flight;
import com.service.logistservice.model.Status;
import lombok.Data;

@Data
public class EventDTO {
    private long flightId;
    private Status status;
}
