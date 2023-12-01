package com.codeplanks.home360.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;
  private HttpStatus status;
  private String message;
  private  List<String> errors;

  public ApiError(LocalDateTime timestamp, HttpStatus status, String message) {
    this.timestamp = timestamp;
    this.status = status;
    this.message = message;
  }

  public ApiError(LocalDateTime timestamp, HttpStatus status, List<String> errors) {
    this.timestamp = timestamp;
    this.status = status;
    this.errors = errors;
  }
}
