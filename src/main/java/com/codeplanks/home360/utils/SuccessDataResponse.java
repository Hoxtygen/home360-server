package com.codeplanks.home360.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuccessDataResponse<T> {
  private HttpStatusCode status;
  private String message;
  private  T data;
}
