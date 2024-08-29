package com.codeplanks.home360.exception;

public class RefreshTokenExpiredException extends RuntimeException {
  public RefreshTokenExpiredException(String message) {
    super(message);
  }

  public RefreshTokenExpiredException(String message, Throwable cause) {
    super(message, cause);
  }

  public RefreshTokenExpiredException(Throwable cause) {
    super(cause);
  }
}
