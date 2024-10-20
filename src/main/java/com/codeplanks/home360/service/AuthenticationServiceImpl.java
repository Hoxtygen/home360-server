/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.event.RegistrationCompleteEvent;
import com.codeplanks.home360.event.listener.RegistrationCompleteEventListener;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.repository.UserRepository;
import com.codeplanks.home360.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wasiu Idowu
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
  final HttpServletRequest servletRequest;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final VerificationTokenRepository verificationTokenRepository;
  private final RefreshTokenServiceImpl refreshTokenServiceImpl;
  private final PasswordResetTokenServiceImpl passwordResetTokenServiceImpl;
  private final UserServiceImpl userService;
  private final RegistrationCompleteEventListener eventListener;
  private final ApplicationEventPublisher publisher;
  private final VerificationTokenServiceImpl verificationTokenService;
  Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

  @Value("${application.frontend.reset-password.url}")
  private String resetPasswordUrl;

  @Value("${application.frontend.verify-email.url}")
  private String emailVerificationUrl;

  @Override
  public AppUser register(RegisterRequest request) throws UserAlreadyExistsException {
    boolean newUserEmail = userService.emailExists(request.getEmail());
    boolean newUserPhoneNumber = userService.phoneNumberExists(request.getPhoneNumber());
    if (newUserEmail) {
      throw new UserAlreadyExistsException(
          "User with email " + request.getEmail() + " " + "already exists");
    }

    if (newUserPhoneNumber) {
      throw new UserAlreadyExistsException(
          "User with phone number " + request.getPhoneNumber() + " already exists");
    }

    AppUser user =
        AppUser.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail().toLowerCase())
            .address(request.getAddress())
            .phoneNumber(request.getPhoneNumber())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .createdAt(new Date())
            .updatedAt(new Date())
            .build();
    userRepository.save(user);
    publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(servletRequest)));
    return user;
  }

  @Override
  public AuthenticationResponse login(AuthenticationRequest request)
      throws BadCredentialsException {
    boolean userExist = userService.emailExists(request.getEmail().toLowerCase());

    if (!userExist) {
      logger.error("User does not exist: " + request.getEmail());
      throw new BadCredentialsException("Incorrect email/password");
    }
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.getEmail().toLowerCase(), request.getPassword()));
      if (!authentication.isAuthenticated()) {
        throw new NotFoundException("Incorrect email/password");
      }
      AppUser user = userService.getUser(request.getEmail().toLowerCase());
      RefreshToken refreshToken = refreshTokenServiceImpl.generateRefreshToken(user);
      TokenResponse tokenResponse =
          TokenResponse.builder()
              .accessToken(jwtService.generateToken(user))
              .refreshToken(refreshToken.getToken())
              .build();
      return AuthenticationResponse.builder()
          .token(tokenResponse)
          .id(user.getId())
          .firstName(user.getFirstName())
          .lastName(user.getLastName())
          .email(user.getEmail())
          .message("User login successful")
          .status(200)
          .build();

    } catch (BadCredentialsException exception) {
      throw new BadCredentialsException("Incorrect username/password");
    }
  }

  @Override
  public String verifyAccount(String token) {
    System.out.println("Token verify: "+ token);
    VerificationToken verificationToken = verificationTokenService.validateVerificationToken(token);
    System.out.println("verificationToken verify: "+ verificationToken);
    if (verificationToken.getUser() == null) {
      throw new NotFoundException("Invalid verification token");
    }
    AppUser user = verificationToken.getUser();
    if (user.isEnabled()) {
      return "This account has already been verified. Please login";
    }
    user.setEnabled(true);
    userRepository.save(user);
    return "Email verified successfully. Proceed to login to your account";
  }

  @Override
  public String resetForgottenUserPassword(
      PasswordResetRequest passwordResetRequest, String token) {
    AppUser user = passwordResetTokenServiceImpl.validatePasswordResetToken(token);
    if (user == null) {
      throw new NotFoundException("User not found for the provided password reset token");
    }
    userService.updatePassword(user, passwordResetRequest.getNewPassword());
    passwordResetTokenServiceImpl.deleteToken(token);
    return "Password has been reset successfully";
  }

  public String requestPasswordReset(String email)
      throws MessagingException, UnsupportedEncodingException {
    AppUser user = userService.findUserByEmail(email);
    String passwordResetToken = UUID.randomUUID().toString();
    createPasswordResetTokenForUser(user, passwordResetToken);
    createPasswordResetEmailLink(user, passwordResetToken);
    return "Password reset link has been sent to your registered email.";
  }

  @Override
  public AppUser findUserByPasswordToken(String token) {
    return passwordResetTokenServiceImpl
        .findUserByPasswordToken(token)
        .orElseThrow(() -> new NotFoundException("Invalid password reset token"));
  }
  private void createPasswordResetTokenForUser(AppUser user, String passwordResetToken) {
    passwordResetTokenServiceImpl.createPasswordResetUserToken(user, passwordResetToken);
  }

  private void createPasswordResetEmailLink(AppUser user, String passwordToken)
      throws MessagingException, UnsupportedEncodingException {
    String url = resetPasswordUrl + "/?token=" + passwordToken;
    eventListener.sendPasswordResetEmail(url);
  }

  private String applicationUrl(HttpServletRequest request) {
    return "http://"
        + request.getServerName()
        + ":"
        + request.getServerPort()
        + "/api/v1/auth"
        + request.getContextPath();
  }
}
