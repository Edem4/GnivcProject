package com.service.portalservice.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Data;


@Data
public class CompanyNotCreatedException extends Exception{
    private HttpStatus status;

    public CompanyNotCreatedException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
