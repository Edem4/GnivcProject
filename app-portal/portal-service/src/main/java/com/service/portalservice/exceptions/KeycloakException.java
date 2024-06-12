package com.service.portalservice.exceptions;

import jakarta.ws.rs.core.Response;
import lombok.Data;

@Data
public class KeycloakException extends Exception{
    Response response;

    public KeycloakException(String message, Response response) {
        super(message);
        this.response = response;
    }
}