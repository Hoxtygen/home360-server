/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.token.TokenRequest;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.repository.UserRepository;
import com.codeplanks.home360.repository.VerificationTokenRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Wasiu Idowu
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final VerificationTokenRepository tokenRepository;
  private final RefreshTokenServiceImpl refreshTokenServiceImpl;
  private final PasswordResetTokenServiceImpl passwordResetTokenServiceImpl;
  private final UserServiceImpl userService;
  Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

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

    return userRepository.save(user);
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
    VerificationToken verificationToken = tokenRepository.findByToken(token);
    if (verificationToken == null) {
      throw new BadCredentialsException("Invalid verification token");
    }
    AppUser user = verificationToken.getUser();
    if (user.isEnabled()) {
      return "This account has already been verified. Please login";
    }
    String verificationTokenResult = validateVerificationToken(token);
    if (verificationTokenResult.equalsIgnoreCase("valid")) {
      return "Email verified successfully. Proceed to login to your account";
    } else {
      return "Verification failed";
    }
  }

  @Override
  public TokenResponse refreshToken(TokenRequest request) {
    return refreshTokenServiceImpl
        .findByToken(request.getToken())
        .map(refreshTokenServiceImpl::verifyRefreshTokenExpirationTime)
        .map(RefreshToken::getUser)
        .map(
            userInfo -> {
              String accessToken = jwtService.generateToken(userInfo);
              return TokenResponse.builder()
                  .accessToken(accessToken)
                  .refreshToken(request.getToken())
                  .build();
            })
        .orElseThrow(() -> new NotFoundException("Refresh token not in Database"));
  }

  @Override
  public void saveUserVerificationToken(AppUser theUser, String token) {
    VerificationToken verificationToken = new VerificationToken(token, theUser);
    tokenRepository.save(verificationToken);
  }

  @Override
  public VerificationToken generateNewVerificationToken(String oldVerificationToken) {
    VerificationToken verificationToken = tokenRepository.findByToken(oldVerificationToken);
    if (verificationToken == null) {
      throw new NotFoundException("Invalid User. Please register");
    }
    var verificationTokenTime = new VerificationToken();
    verificationToken.setToken(UUID.randomUUID().toString());
    verificationToken.setExpirationTime(verificationTokenTime.getTokenExpirationTime());
    return tokenRepository.save(verificationToken);
  }

  @Override
  public String validateVerificationToken(String token) {
    VerificationToken verificationToken = tokenRepository.findByToken(token);
    AppUser user = verificationToken.getUser();
    Calendar calendar = Calendar.getInstance();
    if ((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
      throw new BadCredentialsException(
          "Token already expired. Click the  link below to request for a new token");
    }
    user.setEnabled(true);
    userRepository.save(user);
    return "valid";
  }

  @Override
  public String validatePasswordResetToken(String token) {
    return passwordResetTokenServiceImpl.validatePasswordResetToken(token);
  }

  @Override
  public String resetUserPassword(PasswordResetRequest passwordResetRequest, String token) {
    String tokenVerificationResult = validatePasswordResetToken(token);
    if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
      throw new NotFoundException("Invalid password reset token");
    }
    AppUser appUser = findUserByPasswordToken(token);
    userService.updatePassword(appUser, passwordResetRequest.getNewPassword());
    return "Password has been reset successfully";
  }

  @Override
  public void createPasswordResetTokenForUser(AppUser user, String passwordResetToken) {
    passwordResetTokenServiceImpl.createPasswordResetUserToken(user, passwordResetToken);
  }

  @Override
  public AppUser findUserByPasswordToken(String token) {
    return passwordResetTokenServiceImpl
        .findUserByPasswordToken(token)
        .orElseThrow(() -> new NotFoundException("Invalid password reset token"));
  }

  //  private boolean emailExists(String email) {
  //    return userRepository.findByEmail(email).isPresent();
  //  }
  //
  //  private boolean phoneNumberExists(String phoneNumber) {
  //    return userRepository.findByPhoneNumber(phoneNumber).isPresent();
  //  }

}
