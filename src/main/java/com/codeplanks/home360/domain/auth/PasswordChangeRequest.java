/* (C)2024 */
package com.codeplanks.home360.domain.auth;

import com.codeplanks.home360.validation.Password;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordChangeRequest {
  @Password(message = "Enter a valid password")
  @NotNull(message = "Password is required")
  private String oldPassword;

  @Password(message = "Enter a valid password")
  @NotNull(message = "New password is required")
  private String newPassword;
}
