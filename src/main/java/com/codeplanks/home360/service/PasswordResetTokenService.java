package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.passwordReset.PasswordResetToken;

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

