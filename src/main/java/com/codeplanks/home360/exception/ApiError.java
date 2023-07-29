package com.codeplanks.home360.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class ApiError {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;
  private HttpStatus status;
  private String message;
  private  List errors;
//  private String debugMessage;
//  private List<ApiSubError> subErrors;

//  private  ApiError() {
//    timestamp = LocalDateTime.now();
//  }
//
//  ApiError(HttpStatus status) {
//    this();
//    this.status = status;
//  }
//
//  ApiError(HttpStatus status, Throwable ex) {
//    this();
//    this.status = status;
//    this.message = "Unexpected error";
//    this.debugMessage = ex.getLocalizedMessage();
//  }
//
//  ApiError(HttpStatus status, String message, Throwable ex) {
//    this();
//    this.status = status;
//    this.message = message;
//    this.debugMessage = ex.getLocalizedMessage();
//  }
}
