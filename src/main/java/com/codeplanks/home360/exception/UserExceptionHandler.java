package com.codeplanks.home360.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(value = {UserExistsException.class})
    public ResponseEntity<Object> handleUserExistsException(UserExistsException exception) {
        AuthenticationException authenticationException = new AuthenticationException(
                exception.getMessage(),
                exception.getCause(),
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(authenticationException, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        AuthenticationException authenticationException = new AuthenticationException(
                exception.getMessage(),
                exception.getCause(),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(authenticationException, HttpStatus.NOT_FOUND);
    }

}
