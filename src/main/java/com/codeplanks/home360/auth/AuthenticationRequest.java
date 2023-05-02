package com.codeplanks.home360.auth;


import com.codeplanks.home360.validation.Password;
import com.codeplanks.home360.validation.ValidEmail;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Enter a valid email address")
  @ValidEmail(message = "Enter a valid email address")
  private  String email;

  @Password(message = "Password is required")
  private  String password;
}
