package com.codeplanks.home360.domain.email;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmailRequest {
  private String recipient;
  private String subject;
  private String message;
  private String name;
}
