package com.codeplanks.home360.auth;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.user.AppUser;
import com.codeplanks.home360.user.Role;
import com.codeplanks.home360.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {
  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtService jwtService;
  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  Authentication authentication;

  private RegisterRequest request;

  private AppUser user;


  @BeforeEach
  public void setup() {
    request = RegisterRequest.builder()
            .firstName("Elaeis")
            .lastName("Guineensis")
            .email("elaeis@example.com")
            .address("221B, Baker street, London")
            .phoneNumber("08030123456")
            .password("Int3rnat!onalization")
            .build();

    user = AppUser.builder()
            .id(1)
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
  }

  final String token = "eyJhbGciOiJIUzUxMiJ9" +
          ".eyJzdWIiOiJtaW1vc2FAZXhhbXBsZS5jb20iLCJpYXQiOjE2OTExNjcxNjEsImV4cCI6MTY5MTE2ODYwMX0" +
          ".J8NrbPaIMw8VZWUz5uxZ_aGGTPJnNnn3bn_h0aNXiKGcKQnbMXDha5XpSvlA2WRVzU55jNf_qx9wyc5xH3z7BQ";

  @Test
  @DisplayName("register new user")
  void GivenAppUserObject_WhenRegisterUser_ThenReturnAppUserObject() {
    // Given - precondition or setup
    given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
    given(userRepository.findByPhoneNumber(request.getPhoneNumber())).willReturn(Optional.empty());
    given(userRepository.save(any(AppUser.class))).willReturn(user);
    given(jwtService.generateToken(user)).willReturn(token);
    when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

    // When - action or the behaviour we're testing for
    AuthenticationResponse response = authenticationService.register(request);

    // Then - verify the output
    assertThat(response).isNotNull();
    assertThat(response.getFirstName()).isEqualTo("Elaeis");
    assertThat(response.getFirstName()).isNotEqualTo("Malaysia");
    assertThat(response.getFirstName()).isNotEmpty();

    assertThat(response.getStatus()).isEqualTo(201);

    assertThat(response.getMessage()).isEqualTo("User signup successful");

    assertThat(response.getToken()).isEqualTo(token);

    assertThat(response.getEmail()).isEqualTo("elaeis@example.com");
  }

  @DisplayName("register duplicate email")
  @Test
  public void givenExistingEmail_whenSaveAppUser_thenThrowsException() {
    // Given - precondition or setup
    given(userRepository.findByEmail(request.getEmail())).willReturn(
            Optional.of(user));

    // When - action or the behaviour we're testing for
    assertThrows(UserAlreadyExistsException.class, () -> {
      authenticationService.register(request);
    });

    // Then - verify the output
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @DisplayName("register duplicate phone number")
  @Test
  public void givenExistingPhoneNumber_whenSaveAppUser_thenThrowsException() {
    // Given - precondition or setup
    given(userRepository.findByPhoneNumber(request.getPhoneNumber())).willReturn(
            Optional.of(user));

    // When - action or the behaviour we're testing for
    assertThrows(UserAlreadyExistsException.class, () -> {
      authenticationService.register(request);
    });

    // Then - verify the output
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @DisplayName("user login")
  @Test
  public void givenAppUserCredentials_whenLoginUser_thenReturnAppUser() {
    // Given - precondition or setup
    given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
    given(authentication.isAuthenticated()).willReturn(true);
    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(authentication);
    given(jwtService.generateToken(user)).willReturn(token);

    // When - action or the behaviour we're testing for
    AuthenticationRequest authRequest = new AuthenticationRequest();
    authRequest.setEmail("elaeis@example.com");
    authRequest.setPassword("Int3rnat!onalization");
    AuthenticationResponse response = authenticationService.login(authRequest);

    // Then - verify the output
    assertThat(response).isNotNull();
    assertThat(response.getToken()).isEqualTo(token);
    assertThat(response.getMessage()).isEqualTo("User login successful");
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @DisplayName("incorrect user email login")
  @Test
  public void givenNonExistentAppUser_whenUserLogin_thenThrowsException() {
    AuthenticationRequest authRequest = new AuthenticationRequest();
    authRequest.setEmail("nonexistent@example.com");
    authRequest.setPassword("password123");

    // Given - precondition or setup
    given(userRepository.findByEmail(authRequest.getEmail())).willReturn(Optional.empty());

    // When - action or the behaviour we're testing for
    assertThrows(BadCredentialsException.class, () -> {
      authenticationService.login(authRequest);
    });

    // Then - verify the output
    verify(authenticationManager, never()).authenticate(authentication);
  }

  @DisplayName("incorrect password")
  @Test
  public void givenIncorrectPassword_whenUserLogin_thenThrowsException() {
    AuthenticationRequest authenticationRequest = new AuthenticationRequest();
    authenticationRequest.setEmail(request.getEmail());
    authenticationRequest.setPassword("cardinality");

    // Given - precondition or setup
    given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.ofNullable(user));
    given(authentication.isAuthenticated()).willReturn(false);
    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(authentication);

    // When - action or the behaviour we're testing for
    assertThrows(NotFoundException.class, () -> authenticationService.login(authenticationRequest));

    // Then - verify the output
    verify(authenticationManager, never()).authenticate(authentication);
  }
}