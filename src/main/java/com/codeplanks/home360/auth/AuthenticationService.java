package com.codeplanks.home360.auth;


import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.exception.UserNotFoundException;
import com.codeplanks.home360.user.AppUser;
import com.codeplanks.home360.user.IUserService;
import com.codeplanks.home360.user.Role;
import com.codeplanks.home360.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IUserService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

  public AuthenticationResponse register(
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
    var jwtToken = jwtService.generateToken(newUser);
    logger.info("User created " + newUser);
    return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .firstName(newUser.getFirstName())
            .lastName(newUser.getLastName())
            .email(newUser.getUsername())
            .message("User signup successful")
            .status(201)
            .build();
  }

  private boolean emailExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  private boolean phoneNumberExists(String phoneNumber) {
    return userRepository.findByPhoneNumber(phoneNumber).isPresent();
  }

  private AppUser getUser(String email) throws UserNotFoundException {
    return userRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException("email/password incorrect"));
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
        throw new UserNotFoundException("Incorrect email/password");
      }
      AppUser user = getUser(request.getEmail());
      jwtToken = jwtService.generateToken(user);
      logger.info("User authenticated Successfully: " + user);
      return AuthenticationResponse.builder()
              .token(jwtToken)
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
}
