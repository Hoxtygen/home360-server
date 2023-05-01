package com.codeplanks.home360.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleUserSignupValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        List<String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .toList();
        var statusCode = ex.getStatusCode().value();
        body.put("errors", errors);
        body.put("status", statusCode);
        body.put("timestamp", new Date());
        return new ResponseEntity<>(body, new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

}



