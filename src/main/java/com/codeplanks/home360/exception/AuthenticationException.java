/* (C)2024-2025 */
package com.codeplanks.home360.exception;

import org.springframework.http.HttpStatusCode;

public class AuthenticationException {
  private final String message;
  private final HttpStatusCode statusCode;

  public AuthenticationException(String message, HttpStatusCode statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public String getMessage() {
    return message;
  }

  public HttpStatusCode getStatusCode() {
    return statusCode;
  }

  @Override
  public String toString() {
    return "AuthenticationException{"
        + "message='"
        + message
        + '\''
        + ", statusCode="
        + statusCode
        + '}';
  }
}
