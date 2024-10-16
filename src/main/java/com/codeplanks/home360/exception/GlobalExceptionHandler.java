/* (C)2024 */
package com.codeplanks.home360.exception;

import com.mongodb.MongoSocketOpenException;
import com.sun.mail.util.MailConnectException;
import jakarta.mail.AuthenticationFailedException;
import jakarta.validation.ConstraintViolationException;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.MethodNotAllowedException;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception) {
    logger.error("Error: {}", exception.getMessage());
    List<String> errors =
        exception.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList();

    ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, errors);

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnAuthorizedException.class)
  public ResponseEntity<ApiError> handleUnAuthorizedException(UnAuthorizedException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(
      IllegalArgumentException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Item does not exist: " + exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(
      value = {HttpRequestMethodNotSupportedException.class, MethodNotAllowedException.class})
  public ResponseEntity<ApiError> handleMethodNotAllowedException(
      HttpRequestMethodNotSupportedException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.METHOD_NOT_ALLOWED, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ApiError> handleUserDisabledException(DisabledException exception) {
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

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {MailSendException.class, MailConnectException.class})
  public ResponseEntity<ApiError> handleMailException(MailSendException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(
      value = {
        ConstraintViolationException.class,
      })
  public ResponseEntity<ApiError> handleConstraintViolationException(
      ConstraintViolationException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ExpiredTokenException.class)
  public ResponseEntity<ApiError> handleRefreshTokenExpiredException(
      ExpiredTokenException exception) {
    ApiError apiError = new ApiError();
    apiError.setStatus(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    apiError.setTimestamp(LocalDateTime.now());
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleForbiddenException(AccessDeniedException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.FORBIDDEN, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MappingException.class)
  public ResponseEntity<ApiError> handleMappingException(MappingException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConversionFailedException.class)
  public ResponseEntity<ApiError> handleConversionFailedException(
      ConversionFailedException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DateTimeParseException.class)
  public ResponseEntity<ApiError> handleDateTimeParseException(DateTimeParseException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataAccessResourceFailureException.class)
  public ResponseEntity<ApiError> handleDataAccessResourceFailureException(
      DataAccessResourceFailureException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MongoSocketOpenException.class)
  public ResponseEntity<ApiError> handleMongoSocketOpenException(
      MongoSocketOpenException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConnectException.class)
  public ResponseEntity<ApiError> handleConnectException(ConnectException exception) {
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, exception.getMessage());

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException exception) {
    String readableMessage = extractEnumErrorMessage(exception.getMessage());
    ApiError apiError =
        new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, readableMessage);

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  private String extractEnumErrorMessage(String message) {
    if (message.contains("Cannot deserialize value of type")) {
      try {
        // Extracting relevant parts of the error message
        String enumType = message.substring(message.indexOf("type `") + 6, message.indexOf("` from String"));
        String invalidValue = message.substring(message.indexOf("from String \"") + 13, message.indexOf("\": not"));
        String acceptedValues = message.substring(message.indexOf("Enum class: [") + 13, message.indexOf("]"));

        // Extract only the simple class name instead of the fully qualified name
        String fieldName = enumType.substring(enumType.lastIndexOf('.') + 1);

        return String.format("Invalid value '%s' for field '%s'. Accepted values are: %s.", invalidValue, fieldName, acceptedValues);
      } catch (Exception e) {
        // Fallback in case of unexpected format
        return "Invalid value provided. Please check your input.";
      }
    }
    return message;  // Fallback to the original message if it's not a deserialization issue
  }


}
