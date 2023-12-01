package com.codeplanks.home360.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class UserExceptionHandler {

  @ExceptionHandler(value = {UserAlreadyExistsException.class})
  public ResponseEntity<ApiError> handleUserExistsException(UserAlreadyExistsException exception) {
    ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.CONFLICT, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public ResponseEntity<ApiError> handleUserNotFoundException(NotFoundException exception) {
    ApiError apiError = new ApiError(LocalDateTime.now(),HttpStatus.NOT_FOUND, exception.getMessage() );

    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {BadCredentialsException.class, AccessDeniedException.class,
          ExpiredJwtException.class, UsernameNotFoundException.class})
  public ResponseEntity<ApiError> handleBadCredentialsException(RuntimeException exception) {
    ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
  }

}


