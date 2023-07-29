package com.codeplanks.home360.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public record AuthenticationException(String message, HttpStatusCode status) {
    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatusCode status() {
        return status;
    }
}
