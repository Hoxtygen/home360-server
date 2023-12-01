package com.codeplanks.home360.token.passwordReset;

import com.codeplanks.home360.user.AppUser;

import java.util.Optional;


/**
 * @author Wasiu Idowu
 * */
public interface PasswordResetTokenService {
  void createPasswordResetUserToken(AppUser user, String passwordToken);
  String validatePasswordResetToken(String passwordResetToken);

  Optional<AppUser> findUserByPasswordToken(String passwordResetToken);

  PasswordResetToken findPasswordResetToken(String token);
}

