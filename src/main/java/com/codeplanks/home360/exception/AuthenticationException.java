/* (C)2024 */
package com.codeplanks.home360.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class AuthenticationException extends RuntimeException {

  private final HttpStatusCode status;

  public AuthenticationException(String message, HttpStatusCode status) {
    super(message);
    this.status = status;
  }

  public AuthenticationException(String message) {
    this(message, HttpStatus.UNAUTHORIZED);
  }

  public HttpStatusCode status() {
    return status;
  }
}

