/* (C)2024 */
package com.codeplanks.home360.domain.auth;

import com.codeplanks.home360.validation.Password;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wasiu Idowu
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateDTO {
  @Password(message = "Password is required")
  private String newPassword;
}
