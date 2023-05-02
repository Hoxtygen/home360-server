package com.codeplanks.home360.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public record AuthenticationException(String message, HttpStatusCode status) {
}
