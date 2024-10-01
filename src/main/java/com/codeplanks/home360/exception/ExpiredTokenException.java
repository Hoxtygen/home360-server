/* (C)2024 */
package com.codeplanks.home360.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExpiredTokenException extends RuntimeException {
  public ExpiredTokenException(String message) {
    super(message);
  }

  public ExpiredTokenException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExpiredTokenException(Throwable cause) {
    super(cause);
  }
}
