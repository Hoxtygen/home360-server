/* (C)2024-2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.event.DuplicateSessionEvent;
import com.codeplanks.home360.event.RegistrationCompleteEvent;
import com.codeplanks.home360.event.listener.RegistrationCompleteEventListener;
import com.codeplanks.home360.exception.DuplicateSessionException;
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
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
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
  private final RedisTemplate<String, Object> redisTemplate;
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
  public AuthenticationResponse login(
      AuthenticationRequest request, SessionUserInfo sessionUserInfo)
      throws BadCredentialsException {
    String email = GeneralUtils.toLowerCase(request.getEmail());

    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email, request.getPassword()));

      AppUser user = userService.getUser(request.getEmail().toLowerCase());

      boolean userHasActiveSession = hasActiveSession(user.getEmail());
      if (userHasActiveSession) {
        logger.error("user trying to create duplicate session {}", user.getEmail());
        publisher.publishEvent(
            new DuplicateSessionEvent(
                user.getEmail(),
                sessionUserInfo.getDeviceType(),
                sessionUserInfo.getRemoteAddress(),
                sessionUserInfo.getBrowserName(),
                sessionUserInfo.getOperatingSystem()));

        throw new DuplicateSessionException(
            "You have an active session. Please logout of that session and login again.");
      }

      String sessionToken = generateSessionToken();

      redisTemplate
          .opsForValue()
          .set("active_session:" + user.getEmail(), sessionToken, 3, TimeUnit.DAYS);
      redisTemplate.opsForHash().put("session:" + sessionToken, "email", user.getEmail());
      redisTemplate
          .opsForHash()
          .put("session:" + sessionToken, "createdAt", System.currentTimeMillis());

      redisTemplate.expire("session:" + sessionToken, 3, TimeUnit.DAYS);

      RefreshToken refreshToken = refreshTokenServiceImpl.generateRefreshToken(user);
      TokenResponse tokenResponse =
          TokenResponse.builder()
              .accessToken(jwtService.generateToken(user))
              .refreshToken(refreshToken.getToken())
              .sessionToken(sessionToken)
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
  public String logout(
      String accessToken,
      HttpServletRequest request,
      HttpServletResponse response,
      String sessionToken) {
    String refreshToken = jwtUtils.extractRefreshTokenFromRequest(request);
    String sessionKey = "session:" + sessionToken;
    String userEmail = jwtUtils.extractSubject(accessToken);
    String activeSession = "active_session:" + userEmail;
    try {
      jwtUtils.invalidateToken(accessToken);

      redisTemplate.delete(sessionKey);
      redisTemplate.delete(activeSession);

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
    logger.info("Session token deleted: user:{} sessionToken:{}", userEmail, sessionToken);
    Cookie refresTokenCookie = new Cookie("refreshToken", null);
    refresTokenCookie.setPath("/");
    refresTokenCookie.setHttpOnly(true);
    refresTokenCookie.setMaxAge(0);
    response.addCookie(refresTokenCookie);

    Cookie sessionTokenCookie = new Cookie("sessionToken", null);
    sessionTokenCookie.setPath("/");
    sessionTokenCookie.setHttpOnly(true);
    sessionTokenCookie.setMaxAge(0);
    response.addCookie(sessionTokenCookie);
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

  private String generateSessionToken() {
    return UUID.randomUUID().toString();
  }

  private boolean hasActiveSession(String userEmail) {
    String activeSessionToken =
        (String) redisTemplate.opsForValue().get("active_session:" + userEmail);

    return activeSessionToken != null; // If found, user has an active session
  }
}
