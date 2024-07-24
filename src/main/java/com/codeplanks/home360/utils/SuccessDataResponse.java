package com.codeplanks.home360.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


/**
 * @author Wasiu Idowu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuccessDataResponse<T> {
  private HttpStatus status;
  private String message;
  private  T data;
}
