package com.codeplanks.home360.exception;

import com.sun.mail.util.MailConnectException;
import jakarta.mail.AuthenticationFailedException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.MethodNotAllowedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;


@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleMethodArgumentNotValid(
          MethodArgumentNotValidException exception) {
    logger.error("Error: {}", exception.getMessage());
    List<String> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList();

    ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, errors);

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiError> unauthorizedException(UnauthorizedException exception) {
    ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED,
            exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(
          IllegalArgumentException exception) {
    ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST,
            exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiError> handleNoSuchElementException(
          NoSuchElementException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Item does not exist: " + exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class,
          MethodNotAllowedException.class})
  public ResponseEntity<ApiError> handleMethodNotAllowedException(
          HttpRequestMethodNotSupportedException exception) {
    ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.METHOD_NOT_ALLOWED,
            exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ApiError> handleUserDisabledException(
          DisabledException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.BAD_REQUEST);
    apiError.setMessage("User is currently disabled: " + exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ResponseEntity<ApiError> handleAuthenticationFailedException(
          AuthenticationFailedException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());

    return new ResponseEntity<>(apiError, HttpStatus.NOT_IMPLEMENTED);
  }

  @ExceptionHandler(value = {MailSendException.class, MailConnectException.class})
  public ResponseEntity<ApiError> handleMailException(MailSendException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {ConstraintViolationException.class,})
  public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);

  }

//  @ExceptionHandler(value = {RuntimeException.class,})
//  public ResponseEntity<ApiError> handleRuntimeException(RuntimeException exception) {
//    ApiError apiError = new ApiError();
//    apiError.setStatus(HttpStatus.BAD_REQUEST);
//    apiError.setMessage(exception.getMessage());
//    apiError.setTimestamp(LocalDateTime.now());
//    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
//
//  }
}
