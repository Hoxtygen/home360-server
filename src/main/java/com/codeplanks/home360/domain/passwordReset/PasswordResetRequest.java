package com.codeplanks.home360.domain.passwordReset;

import com.codeplanks.home360.validation.Password;
import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * @author Wasiu Idowu
 * */
@Data
public class PasswordResetRequest {
  @Email(message = "Enter a valid email address")
  private String email;

  private String oldPassword;

  @Password(message = "Password is required")
  private String newPassword;
}
