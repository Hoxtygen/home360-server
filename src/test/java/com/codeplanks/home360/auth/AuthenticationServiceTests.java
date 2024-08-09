package com.codeplanks.home360.auth;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.domain.auth.AuthenticationRequest;
import com.codeplanks.home360.domain.auth.AuthenticationResponse;
import com.codeplanks.home360.domain.auth.RegisterRequest;
import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.repository.UserRepository;
import com.codeplanks.home360.service.AuthenticationServiceImpl;
import com.codeplanks.home360.service.RefreshTokenServiceImpl;
import org.hibernate.annotations.NotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {
  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @Mock
  private RefreshTokenServiceImpl refreshTokenService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtService jwtService;
  @Mock
  private PasswordEncoder passwordEncoder;

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

  String token = "eyJhbGciOiJIUzUxMiJ9" +
          ".eyJzdWIiOiJtaW1vc2FAZXhhbXBsZS5jb20iLCJpYXQiOjE2OTExNjcxNjEsImV4cCI6MTY5MTE2ODYwMX0" +
          ".J8NrbPaIMw8VZWUz5uxZ_aGGTPJnNnn3bn_h0aNXiKGcKQnbMXDha5XpSvlA2WRVzU55jNf_qx9wyc5xH3z7BQ";
  LocalDateTime localDateTime = LocalDateTime.now();
  RefreshToken refreshToken = new RefreshToken(1, token, localDateTime.plusMinutes(50), user);

  @Test
  @DisplayName("register new user successfully")
  void GivenAppUserObjectWhenRegisterUserThenRegistrationSuccessful() {
    // Given - precondition or setup
    given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
    given(userRepository.findByPhoneNumber(request.getPhoneNumber())).willReturn(Optional.empty());
    given(userRepository.save(any(AppUser.class))).willReturn(user);
    when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

    // When - action or the behaviour we're testing for
    AppUser response = authenticationService.register(request);

    // Then - verify the output
    assertThat(response).isNotNull();
    assertThat(response.getFirstName()).isEqualTo("Elaeis");
    assertThat(response.getRole().toString()).isEqualTo("USER");
    assertThat(response.getLastName()).isEqualTo("Guineensis");
    assertThat(response.getEmail()).isEqualTo("elaeis@example.com");
    assertThat(response.getPhoneNumber()).isEqualTo("08030123456");
    assertThat(response.getId()).isNotNull();

    assertThat(response.getFirstName()).isNotEqualTo("Malaysia");
    assertThat(response.getFirstName()).isNotEmpty();
    assertThat(response.getEmail()).isNotEqualTo("elaeis@aol.com");
    assertThat(response.getLastName()).isNotEqualTo("Guinensis");
    assertThat(response.getPhoneNumber()).isNotEqualTo("08030123455");
  }

  @DisplayName("register duplicate email")
  @Test
  public void givenExistingEmailWhenSaveAppUserThenThrowsException() {
    // Given - precondition or setup
    given(userRepository.findByEmail(request.getEmail())).willReturn(
            Optional.of(user));

    // When - action or the behaviour we're testing for
    assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(request));

    // Then - verify the output
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @DisplayName("register duplicate phone number")
  @Test
  public void givenExistingPhoneNumberWhenSaveAppUserThenThrowsException() {
    // Given - precondition or setup
    given(userRepository.findByPhoneNumber(request.getPhoneNumber())).willReturn(
            Optional.of(user));

    // When - action or the behaviour we're testing for
    assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(request));

    // Then - verify the output
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @DisplayName("user login successfully")
  @Test
  public void givenAppUserCredentials_whenLoginUser_thenReturnAppUser() {
    // Given - precondition or setup
    given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(authentication);
    given(authentication.isAuthenticated()).willReturn(true);
    given(jwtService.generateToken(user)).willReturn(token);
    given(refreshTokenService.generateRefreshToken(user)).willReturn(refreshToken);

    // When - action or the behaviour we're testing for
    AuthenticationRequest authRequest = new AuthenticationRequest("elaeis@example.com","Int3rnat!onalization");
    AuthenticationResponse response = authenticationService.login(authRequest);

    // Then - verify the output
    assertThat(response).isNotNull();
    assertThat(response.getToken().getAccessToken()).isEqualTo(token);
    assertThat(response.getMessage()).isEqualTo("User login successful");
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @DisplayName("incorrect user email login")
  @Test
  public void givenNonExistentAppUser_whenUserLogin_thenThrowsException() {
    AuthenticationRequest authRequest = new AuthenticationRequest("nonexistent@example.com", "password123");

    // Given - precondition or setup
    given(userRepository.findByEmail(authRequest.getEmail())).willReturn(Optional.empty());

    // When - action or the behaviour we're testing for
    assertThrows(BadCredentialsException.class, () -> authenticationService.login(authRequest));

    // Then - verify the output
    verify(authenticationManager, never()).authenticate(authentication);
  }

  @DisplayName("incorrect password")
  @Test
  public void givenIncorrectPasswordWhenUserLoginThenThrowsException() {
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

  @DisplayName("get user by user ID success")
  @Test
  public  void GivenUserIdWhenCheckedIfExistsThenReturnAppUser(){
    Integer userId = 1;
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    AppUser result = authenticationService.getUserByUserId(userId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);
  }

  @DisplayName("get user by user ID failure")
  @Test
  public void GivenUserIdWhenCheckedIfExistsThenReturnFalse(){
    Integer userId = 1;
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    NotFoundException exception =  assertThrows(NotFoundException.class, ()-> {
      authenticationService.getUserByUserId(userId);
    });
    assertEquals("User does not exist", exception.getMessage());
  }

  @DisplayName("successfully compare user password")
  @Test
  public  void GivenUserOldPasswordItMatchesWhenComparedWithSavedPassword(){
    String oldUserPassword = "Int3rnat!onalization";
    given(passwordEncoder.matches(oldUserPassword, user.getPassword())).willReturn(true);
    boolean result = authenticationService.oldPasswordIsValid(user, oldUserPassword);
    assertTrue(result);
  }

  @DisplayName("failed user password comparison")
  @Test
  public  void GivenUserOldPasswordItDoesNotMatchWhenComparedWithSavedPassword(){
    String oldUserPassword = "Int3rnat!onaliza";
    given(passwordEncoder.matches(oldUserPassword, user.getPassword())).willReturn(false);
    boolean result = authenticationService.oldPasswordIsValid(user, oldUserPassword);
    assertFalse(result);
  }

}