package com.codeplanks.home360.auth;


import com.codeplanks.home360.token.TokenRequest;
import com.codeplanks.home360.token.TokenResponse;
import com.codeplanks.home360.token.refreshToken.RefreshToken;
import com.codeplanks.home360.token.refreshToken.RefreshTokenServiceImpl;
import com.codeplanks.home360.token.verificationToken.VerificationToken;
import com.codeplanks.home360.token.verificationToken.VerificationTokenRepository;
import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.user.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;


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

  Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

  public AppUser register(
          RegisterRequest request) throws UserAlreadyExistsException {
    boolean newUserEmail = emailExists(request.getEmail());
    boolean newUserPhoneNumber = phoneNumberExists(request.getPhoneNumber());
    if (newUserEmail) {
      logger.error("User with the email already exists");
      throw new UserAlreadyExistsException("User with email " + request
              .getEmail() + " " +
              "already exists");
    }

    if (newUserPhoneNumber) {
      logger.error("User with the phone number already exists");
      throw new UserAlreadyExistsException("User with phone number " + request
              .getPhoneNumber()
              + " already exists"
      );
    }

    AppUser user = AppUser.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .address(request.getAddress())
            .phoneNumber(request.getPhoneNumber())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .createdAt(new Date())
            .updatedAt(new Date())
            .build();

    AppUser newUser = userRepository.save(user);
//    var jwtToken = jwtService.generateToken(newUser);
    logger.info("User created " + newUser);
    return newUser;
  }

  public AuthenticationResponse login(
          AuthenticationRequest request) throws BadCredentialsException {
    boolean userExist = emailExists(request.getEmail());
    String jwtToken;

    if (!userExist) {
      logger.error("User does not exist: " + request.getEmail());
      throw new BadCredentialsException("Incorrect email/password");
    }
    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );
      if (!authentication.isAuthenticated()) {
        logger.error("Incorrect password: " + request.getEmail());
        throw new NotFoundException("Incorrect email/password");
      }
      AppUser user = getUser(request.getEmail());
//      jwtToken = jwtService.generateToken(user);
      RefreshToken refreshToken = refreshTokenServiceImpl.generateRefreshToken(user);
      TokenResponse tokenResponse = TokenResponse.builder()
              .accessToken(jwtService.generateToken(user))
              .refreshToken(refreshToken.getToken())
              .build();
      logger.info("User authenticated Successfully: " + user);
      return AuthenticationResponse.builder()
              .token(tokenResponse)
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


  public String verify(String token) {
    VerificationToken verificationToken = tokenRepository.findByToken(token);
    if (verificationToken.getUser().isEnabled()) {
      return "This account has already been verified. Please login";
    }
    String verificationTokenResult = validateVerificationToken(token);
    if (verificationTokenResult.equalsIgnoreCase("valid")) {
      return "Email verified successfully. Proceed to login to your account";
    } else {
      return "Verification failed";
    }
  }

  public TokenResponse refreshToken(TokenRequest request) {
    return refreshTokenServiceImpl.findByToken(request.getToken())
            .map(refreshTokenServiceImpl::verifyRefreshTokenExpirationTime)
            .map(RefreshToken::getUser)
            .map(userInfo -> {
              String accessToken = jwtService.generateToken(userInfo);
              return TokenResponse.builder()
                      .accessToken(accessToken)
                      .refreshToken(request.getToken())
                      .build();
            }).orElseThrow(() -> new RuntimeException("Refresh token not in Database"));
  }


  private boolean emailExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  private boolean phoneNumberExists(String phoneNumber) {
    return userRepository.findByPhoneNumber(phoneNumber).isPresent();
  }

  private AppUser getUser(String email) throws NotFoundException {
    return userRepository.findByEmail(email).orElseThrow(
            () -> new NotFoundException("email/password incorrect"));
  }

  @Override
  public void saveUserVerificationToken(AppUser theUser, String token) {
    VerificationToken verificationToken = new VerificationToken(token, theUser);
    tokenRepository.save(verificationToken);
  }

  @Override
  public String validateVerificationToken(String token) {
    VerificationToken verificationToken = tokenRepository.findByToken(token);
    if (verificationToken == null) {
      return "Invalid verification token";
    }
    AppUser user = verificationToken.getUser();
    Calendar calendar = Calendar.getInstance();
    if ((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
      tokenRepository.delete(verificationToken);
      return "Token already expired";
    }
    user.setEnabled(true);
    userRepository.save(user);
    return "valid";
  }
}
