package com.service.portalservice.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RoleNotSetException extends Exception{
    private HttpStatus status;
    public RoleNotSetException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
