/* (C)2026 */
package com.codeplanks.home360.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private ZonedDateTime timestamp;

  private HttpStatus status;
  private String message;
  private List<String> errors;

  public ApiError(ZonedDateTime timestamp, HttpStatus status, String message) {
    this.timestamp = timestamp;
    this.status = status;
    this.message = message;
  }

  public ApiError(ZonedDateTime timestamp, HttpStatus status, List<String> errors) {
    this.timestamp = timestamp;
    this.status = status;
    this.errors = errors;
  }
}
