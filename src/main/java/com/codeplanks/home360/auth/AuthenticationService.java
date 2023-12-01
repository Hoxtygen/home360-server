package com.codeplanks.home360.auth;

import com.codeplanks.home360.token.passwordReset.PasswordResetRequest;
import com.codeplanks.home360.token.verificationToken.VerificationToken;
import com.codeplanks.home360.user.AppUser;

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
}
