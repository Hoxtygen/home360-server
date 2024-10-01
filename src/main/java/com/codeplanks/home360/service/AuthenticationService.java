/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.token.TokenRequest;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;

/**
 * @author Wasiu Idowu
 */
public interface AuthenticationService {
  AppUser register(RegisterRequest request);

  AuthenticationResponse login(AuthenticationRequest request);

  void saveUserVerificationToken(AppUser theUser, String verificationToken);

  VerificationToken generateNewVerificationToken(String oldVerificationToken);

  String validateVerificationToken(String token);

  void createPasswordResetTokenForUser(AppUser user, String passwordResetToken);

  String validatePasswordResetToken(String token);

  AppUser findUserByPasswordToken(String token);

  String resetUserPassword(PasswordResetRequest passwordResetRequest, String token);

  String verifyAccount(String token);

  TokenResponse refreshToken(TokenRequest request);
}
