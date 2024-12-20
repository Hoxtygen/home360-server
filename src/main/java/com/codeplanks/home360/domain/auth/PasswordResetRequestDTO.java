/* (C)2024 */
package com.codeplanks.home360.domain.auth;

import com.codeplanks.home360.validation.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequestDTO {
  @NotEmpty(message = "Email is required")
  @ValidEmail(message = "Enter a valid email address")
  private String userEmail;
}
