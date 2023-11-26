package com.codeplanks.home360.token.passwordReset;

import lombok.Data;

/**
 * @author Wasiu Idowu
 * */
@Data
public class PasswordResetRequest {
  private String email;
  private String oldPassword;
  private String newPassword;
}
