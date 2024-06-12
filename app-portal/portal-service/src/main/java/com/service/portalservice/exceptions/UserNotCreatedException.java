package com.service.portalservice.exceptions;

import jakarta.ws.rs.core.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
public class UserNotCreatedException extends Exception{
    private HttpStatus status;
    public UserNotCreatedException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}