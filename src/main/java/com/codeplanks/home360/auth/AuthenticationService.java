package com.codeplanks.home360.auth;

import com.codeplanks.home360.user.AppUser;


/**
 * @author Wasiu Idowu
 */

public interface AuthenticationService {
  AppUser register(RegisterRequest request);
  AuthenticationResponse login(AuthenticationRequest request);
  void saveUserVerificationToken(AppUser theUser, String verificationToken);
  String validateVerificationToken(String token);
}
