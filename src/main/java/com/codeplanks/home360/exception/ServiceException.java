/* (C)2025 */
package com.codeplanks.home360.exception;

public class ServiceException extends RuntimeException {
  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
