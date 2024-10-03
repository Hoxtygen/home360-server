/* (C)2024 */
package com.codeplanks.home360.domain.auth;

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

  @Password(message = "Password is required")
  private String newPassword;
}
