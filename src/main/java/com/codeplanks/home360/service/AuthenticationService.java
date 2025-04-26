/* (C)2024-2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.user.AppUser;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @author Wasiu Idowu
 */
public interface AuthenticationService {
  AppUser register(RegisterRequest request);

  AuthenticationResponse login(AuthenticationRequest request, SessionUserInfo sessionUserInfo);

  AppUser findUserByPasswordToken(String token);

  String resetForgottenUserPassword(PasswordUpdateDTO passwordUpdateDTO, String token);

  String verifyAccount(String token);

  String requestPasswordReset(PasswordResetRequestDTO request)
      throws MessagingException, UnsupportedEncodingException;

  String logout(
      String token, HttpServletRequest request, HttpServletResponse response, String sessionToken);
}
