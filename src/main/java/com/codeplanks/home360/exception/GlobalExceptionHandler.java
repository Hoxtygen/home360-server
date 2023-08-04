package com.codeplanks.home360.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;


@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex) {
    logger.error("User registration error: {}", ex.getMessage());
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

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ExceptionResponse> unauthorizedException(UnauthorizedException ex) {
    ExceptionResponse response = new ExceptionResponse();
    response.setErrorCode("UNAUTHORIZED");
    response.setErrorMessage(ex.getMessage());
    response.setTimestamp(LocalDateTime.now());

    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ExceptionResponse> handleAccessDeniedException(
          AccessDeniedException exception) {
    ExceptionResponse response = new ExceptionResponse();
    response.setErrorCode("UNAUTHORIZED");
    response.setErrorMessage(exception.getMessage());
    response.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }


  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(
          IllegalArgumentException exception) {
    ExceptionResponse response = new ExceptionResponse();
    response.setErrorCode("BAD_REQUEST");
    response.setErrorMessage(exception.getMessage());
    response.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ExceptionResponse> handleNoSuchElementException(
          NoSuchElementException exception) {
    ExceptionResponse response = new ExceptionResponse();
    response.setErrorCode("BAD_REQUEST");
    response.setErrorMessage("Item does not exist: " + exception.getMessage());
    response.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ExceptionResponse> handleNoSuchElementException(
          HttpRequestMethodNotSupportedException exception) {
    ExceptionResponse response = new ExceptionResponse();
    response.setErrorCode("METHOD_NOT_ALLOWED");
    response.setErrorMessage(exception.getMessage());
    response.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
  }

}



