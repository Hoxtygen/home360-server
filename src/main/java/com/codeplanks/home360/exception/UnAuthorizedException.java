package com.codeplanks.home360.exception;

public class UnAuthorizedException extends  RuntimeException {
  public UnAuthorizedException(String message) {
    super(message);
  }

  public UnAuthorizedException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnAuthorizedException(Throwable cause) {
    super(cause);
  }
}
