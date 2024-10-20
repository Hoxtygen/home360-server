/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;

/**
 * @author Wasiu Idowu
 */
public interface AuthenticationService {
  AppUser register(RegisterRequest request);

  AuthenticationResponse login(AuthenticationRequest request);

  AppUser findUserByPasswordToken(String token);

  String resetForgottenUserPassword(PasswordResetRequest passwordResetRequest, String token);

  String verifyAccount(String token);
}
