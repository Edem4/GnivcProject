package com.service.logistservice.dto;

import com.service.logistservice.model.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetFlightDTO {
    private long id;
    private LocalDateTime create;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status statusNow;
}
