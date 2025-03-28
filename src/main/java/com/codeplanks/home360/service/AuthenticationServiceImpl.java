/* (C)2024-2025 */
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
import com.codeplanks.home360.repository.RefreshTokenRepository;
import com.codeplanks.home360.repository.UserRepository;
import com.codeplanks.home360.utils.GeneralUtils;
import com.codeplanks.home360.utils.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
  private final RefreshTokenServiceImpl refreshTokenServiceImpl;
  private final PasswordResetTokenServiceImpl passwordResetTokenServiceImpl;
  private final UserServiceImpl userService;
  private final RegistrationCompleteEventListener eventListener;
  private final ApplicationEventPublisher publisher;
  private final VerificationTokenServiceImpl verificationTokenService;
  private final JwtUtils jwtUtils;
  private final RefreshTokenRepository refreshTokenRepository;
  Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

  @Value("${application.frontend.reset-password.url}")
  private String resetPasswordUrl;

  @Value("${application.frontend.verify-email.url}")
  private String emailVerificationUrl;

  @Override
  public AppUser register(RegisterRequest request) throws UserAlreadyExistsException {
    String email = GeneralUtils.toLowerCase(request.getEmail());
    if (userService.userExists(email, request.getPhoneNumber())) {
      throw new UserAlreadyExistsException("User with email or phone number already exists");
    }

    AppUser user =
        AppUser.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(email)
            .address(request.getAddress())
            .phoneNumber(request.getPhoneNumber())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
    userRepository.save(user);
    publisher.publishEvent(
        new RegistrationCompleteEvent(user, buildApplicationUrl(servletRequest)));
    return user;
  }

  @Override
  public AuthenticationResponse login(AuthenticationRequest request)
      throws BadCredentialsException {
    String email = GeneralUtils.toLowerCase(request.getEmail());
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email, request.getPassword()));

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
          .build();

    } catch (BadCredentialsException exception) {
      logger.error("Authentication failed for user: {}", request.getEmail());
      throw new BadCredentialsException("Incorrect username/password");
    }
  }

  @Override
  public String verifyAccount(String token) {
    VerificationToken verificationToken =
        verificationTokenService.validateVerificationToken(token.trim());
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
  public String resetForgottenUserPassword(PasswordUpdateDTO passwordUpdateDTO, String token) {
    AppUser user = passwordResetTokenServiceImpl.validatePasswordResetToken(token);
    if (user == null) {
      throw new NotFoundException("User not found for the provided password reset token");
    }
    userService.updatePassword(user, passwordUpdateDTO.getNewPassword());
    passwordResetTokenServiceImpl.deleteToken(token);
    return "Password has been reset successfully";
  }

  @Override
  public String requestPasswordReset(PasswordResetRequestDTO request)
      throws MessagingException, UnsupportedEncodingException {
    AppUser user = userService.findUserByEmail(request.getUserEmail());
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

  @Transactional
  @Override
  public String logout(String token, HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = jwtUtils.extractRefreshTokenFromRequest(request);

    String userEmail = jwtUtils.extractSubject(token);

    try {
      jwtUtils.invalidateToken(token);
      if (userEmail != null) {
        logger.info("Access token blacklisted for user: {}", userEmail);
      }
    } catch (DataAccessException exception) {
      logger.warn(
          "Error blacklisting access token for user {}: {}", userEmail, exception.getMessage());
    }

    if (refreshToken != null) {

      try {
        refreshTokenRepository.deleteByToken(refreshToken);
        logger.info("Refresh token deleted: {}", refreshToken);

      } catch (DataAccessException exception) {
        logger.warn("Error deleting refresh token {}: {}", refreshToken, exception.getMessage());
      }
    }

    Cookie cookie = new Cookie("refreshToken", null);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    SecurityContextHolder.clearContext();
    return "User successfully logged out";
  }

  private void createPasswordResetTokenForUser(AppUser user, String passwordResetToken) {
    passwordResetTokenServiceImpl.createPasswordResetUserToken(user, passwordResetToken);
  }

  private void createPasswordResetEmailLink(AppUser user, String passwordToken)
      throws MessagingException, UnsupportedEncodingException {
    String url = resetPasswordUrl + "/?token=" + passwordToken;
    eventListener.sendPasswordResetEmail(url);
  }

  private String buildApplicationUrl(HttpServletRequest request) {
    return ServletUriComponentsBuilder.fromRequestUri(request)
        .replacePath("/api/v1/auth" + request.getContextPath())
        .replaceQuery(null)
        .build()
        .toUriString();
  }
}
