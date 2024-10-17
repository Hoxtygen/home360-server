/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.auth.PasswordResetToken;
import com.codeplanks.home360.domain.user.AppUser;
import java.util.Optional;

/**
 * @author Wasiu Idowu
 * */
public interface PasswordResetTokenService {
  void createPasswordResetUserToken(AppUser user, String passwordToken);

  AppUser validatePasswordResetToken(String passwordResetToken);

  Optional<AppUser> findUserByPasswordToken(String passwordResetToken);

  PasswordResetToken findPasswordResetToken(String token);

  void deleteToken(String token);
}
