package com.codeplanks.home360.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

  @ExceptionHandler(value = {UserAlreadyExistsException.class})
  public ResponseEntity<Object> handleUserExistsException(UserAlreadyExistsException exception) {
    AuthenticationException authenticationException = new AuthenticationException(
            exception.getMessage(),
            HttpStatus.CONFLICT
    );
    return new ResponseEntity<>(authenticationException, HttpStatus.CONFLICT);
  }


  @ExceptionHandler(value = {UserNotFoundException.class})
  public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
    AuthenticationException authenticationException = new AuthenticationException(
            exception.getMessage(),
            HttpStatus.NOT_FOUND
    );
    return new ResponseEntity<>(authenticationException, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {BadCredentialsException.class})
  public  ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException exception) {
    AuthenticationException authenticationException =
            new AuthenticationException(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    return  new ResponseEntity<>(authenticationException, HttpStatus.UNAUTHORIZED);
  }


}
