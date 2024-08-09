package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.auth.AuthenticationRequest;
import com.codeplanks.home360.domain.auth.AuthenticationResponse;
import com.codeplanks.home360.domain.auth.RegisterRequest;
import com.codeplanks.home360.domain.passwordReset.PasswordResetRequest;
import com.codeplanks.home360.domain.token.TokenRequest;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.domain.user.AppUser;

import java.util.Optional;


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

  Optional<AppUser> findByEmail(String email);

  void changePassword(AppUser appUser, String newPassword);

  boolean oldPasswordIsValid(AppUser appUser, String oldPassword);

  String resetPassword(PasswordResetRequest passwordResetRequest, String token);
  AppUser getUserByUserId(Integer userId);

  Integer extractUserId();

  String verifyAccount(String token);

  TokenResponse refreshToken(TokenRequest request);
}
