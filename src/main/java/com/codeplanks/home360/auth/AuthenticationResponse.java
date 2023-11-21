package com.codeplanks.home360.auth;


import com.codeplanks.home360.token.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
  private Integer status;
  private  String message;
  private  String firstName;
  private  String lastName;
  private  String email;
  private TokenResponse token;
}
