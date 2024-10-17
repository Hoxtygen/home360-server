/* (C)2024 */
package com.codeplanks.home360.auth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.domain.auth.*;
import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.event.listener.RegistrationCompleteEventListener;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.repository.PasswordResetTokenRepository;
import com.codeplanks.home360.repository.UserRepository;
import com.codeplanks.home360.repository.VerificationTokenRepository;
import com.codeplanks.home360.service.AuthenticationServiceImpl;
import com.codeplanks.home360.service.PasswordResetTokenServiceImpl;
import com.codeplanks.home360.service.RefreshTokenServiceImpl;
import com.codeplanks.home360.service.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {
  @Mock Authentication authentication;
  LocalDateTime localDateTime = LocalDateTime.now();
  @InjectMocks private AuthenticationServiceImpl authenticationService;
  @Mock private RefreshTokenServiceImpl refreshTokenService;

  @Mock private PasswordResetTokenServiceImpl passwordResetTokenService;
  @Mock private UserRepository userRepository;
  @Mock private JwtService jwtService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserServiceImpl userService;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private VerificationTokenRepository verificationTokenRepository;
  @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
  @Mock private RegistrationCompleteEventListener eventListener;
  @Mock private HttpServletRequest servletRequest;
  @Mock private ApplicationEventPublisher publisher;


  private RegisterRequest request;
  private AppUser user;
  private VerificationToken verificationToken;
  private PasswordResetRequest passwordResetRequest;

  private PasswordResetToken passwordResetToken;

  @Value("${application.security.token}")
  private String token;

  RefreshToken refreshToken = new RefreshToken(1, token, localDateTime.plusMinutes(50), user);

  @Value("${application.frontend.reset-password.url}")
  private String resetPasswordUrl;

  @Value("${application.security.password}")
  private String userPassword;

  @Value("${application.security.newPassword}")
  private String newPassword;

  AuthenticationServiceTests() {}

  @BeforeEach
  public void setup() {
    request =
        RegisterRequest.builder()
            .firstName("Elaeis")
            .lastName("Guineensis")
            .email("elaeis@example.com")
            .address("221B, Baker street, London")
            .phoneNumber("08030123456")
            .password(userPassword)
            .build();

    user =
        AppUser.builder()
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

  @Test
  @DisplayName("register new user successfully")
  void GivenAppUserObjectWhenRegisterUserThenRegistrationSuccessful() {
    // Given - precondition or setup
    when(userRepository.save(any(AppUser.class))).thenReturn(user);
//    when(userService.emailExists("elaeis@example.com")).thenReturn(false);
//    when(userService.phoneNumberExists("08030123456")).thenReturn(false);
    when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

    // When - action or the behaviour we're testing for
    AppUser response = authenticationService.register(request);

    // Then - verify the output
    assertAll(
        () -> assertThat(response.getFirstName()).isEqualTo("Elaeis"),
        () -> assertThat(response.getRole().toString()).isEqualTo("USER"),
        () -> assertThat(response.getLastName()).isEqualTo("Guineensis"),
        () -> assertThat(response.getEmail()).isEqualTo("elaeis@example.com"),
        () -> assertThat(response.getPhoneNumber()).isEqualTo("08030123456"),
        () -> assertThat(response.getFirstName()).isNotEqualTo("Malaysia"),
        () -> assertThat(response.getFirstName()).isNotEmpty(),
        () -> assertThat(response.getEmail()).isNotEqualTo("elaeis@aol.com"),
        () -> assertThat(response.getLastName()).isNotEqualTo("Guinensis"),
        () -> assertThat(response.getPhoneNumber()).isNotEqualTo("08030123455"));
    verify(userRepository, times(1)).save(any(AppUser.class));
  }

  @DisplayName("register duplicate email throws UserAlreadyExistException")
  @Test
  public void givenExistingEmailWhenSaveAppUserThenThrowsException() {
    // Given - precondition or setup
    given(userService.emailExists(request.getEmail())).willReturn(true);

    // When - action or the behaviour we're testing for
    UserAlreadyExistsException exception =
        assertThrows(
            UserAlreadyExistsException.class, () -> authenticationService.register(request));

    // Then - verify the output
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @DisplayName("register duplicate phone number throws UserAlreadyExistException")
  @Test
  public void givenExistingPhoneNumberWhenSaveAppUserThenThrowsException() {
    // Given - precondition or setup
    given(userService.phoneNumberExists(request.getPhoneNumber())).willReturn(true);

    // When - action or the behaviour we're testing for
    UserAlreadyExistsException exception =
        assertThrows(
            UserAlreadyExistsException.class, () -> authenticationService.register(request));

    // Then - verify the output
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @DisplayName("user login successfully")
  @Test
  public void givenAppUserCredentials_whenLoginUser_thenReturnAppUser() {
    // Given - precondition or setup
    AuthenticationRequest authRequest =
        new AuthenticationRequest("elaeis@example.com", userPassword);
    given(userService.emailExists(authRequest.getEmail())).willReturn(true);
    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willReturn(authentication);
    given(authentication.isAuthenticated()).willReturn(true);
    given(userService.getUser(authRequest.getEmail().toLowerCase())).willReturn(user);
    given(jwtService.generateToken(user)).willReturn(token);
    given(refreshTokenService.generateRefreshToken(user)).willReturn(refreshToken);

    // When - action or the behaviour we're testing for
    AuthenticationResponse response = authenticationService.login(authRequest);

    // Then - verify the output
    assertAll(
        () -> assertThat(response).isNotNull(),
        () -> assertThat(response.getEmail()).isEqualTo("elaeis@example.com"),
        () -> assertThat(response.getFirstName()).isEqualTo("Elaeis"),
        () -> assertThat(response.getLastName()).isEqualTo("Guineensis"),
        () -> assertThat(response.getToken().getAccessToken()).isEqualTo(token),
        () -> assertThat(response.getMessage()).isEqualTo("User login successful"),
        () -> assertThat(response.getStatus()).isEqualTo(200));

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService, times(1)).generateToken(user);
    verify(refreshTokenService, times(1)).generateRefreshToken(user);
  }

  @DisplayName("incorrect user email login")
  @Test
  public void givenNonExistentAppUser_whenUserLogin_thenThrowsException() {
    // Given - precondition or setup
    AuthenticationRequest authRequest =
        new AuthenticationRequest("nonexistent@example.com", "password123");
    given(userService.emailExists(authRequest.getEmail())).willReturn(false);

    // When - action or the behaviour we're testing for
    assertThrows(BadCredentialsException.class, () -> authenticationService.login(authRequest));

    // Then - verify the output
    verify(authenticationManager, never())
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @DisplayName("incorrect password")
  @Test
  public void givenIncorrectPasswordWhenUserLoginThenThrowsException() {
    AuthenticationRequest authenticationRequest =
        new AuthenticationRequest("elaeis@example.com", "password123");

    // Given - precondition or setup
    given(userService.emailExists(authenticationRequest.getEmail())).willReturn(true);
    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willThrow(new BadCredentialsException("Incorrect username/password"));

    // When - action or the behaviour we're testing for
    assertThrows(
        BadCredentialsException.class, () -> authenticationService.login(authenticationRequest));

    // Then - verify the output
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userService, times(1)).emailExists(authenticationRequest.getEmail());
  }

  @DisplayName("verify user successfully")
  @Test
  public void givenCorrectVerificationTokenWhenUserVerifyAccountThenUserIsEnabled() {
    // Given - precondition or setup
    String tokenString = UUID.randomUUID().toString();
    verificationToken = new VerificationToken(tokenString, user);
    given(verificationTokenRepository.findByToken(tokenString)).willReturn(verificationToken);

    // When - action or the behaviour we're testing for
    String result = authenticationService.verifyAccount(tokenString);

    // Then - verify the output
    assertThat(user.isEnabled()).isTrue();
    assertThat(result).isEqualTo("Email verified successfully. Proceed to login to your account");
    verify(verificationTokenRepository, times(1)).findByToken(tokenString);
    verify(userRepository, times(1)).save(user);
  }

  @DisplayName("Invalid verification token")
  @Test
  public void
      givenInvalidVerificationTokenWhenUserVerifyAccountThenThrowsBadCredentialsException() {
    // Given - precondition or setup
    String tokenString = UUID.randomUUID().toString();
    given(verificationTokenRepository.findByToken(tokenString)).willReturn(null);

    // When - action or the behaviour we're testing for
    BadCredentialsException exception =
        assertThrows(
            BadCredentialsException.class,
            () -> authenticationService.validateVerificationToken(tokenString));

    // Then - verify the output
    assertThat(exception.getMessage()).isEqualTo("Invalid verification token");
    verify(verificationTokenRepository, times(1)).findByToken(tokenString);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @DisplayName("Generate new verification token successfully")
  @Test
  public void givenValidRefreshTokenWhenAccessTokenHasExpiredThenNewTokenIsGenerated() {
    // Given
    String oldToken = UUID.randomUUID().toString();
    verificationToken = new VerificationToken(oldToken, user, 1);
    given(verificationTokenRepository.findByToken(oldToken)).willReturn(verificationToken);

    // When
    VerificationToken newToken = authenticationService.generateNewVerificationToken(oldToken);

    // Then
    assertThat(newToken).isNotNull();
    verify(verificationTokenRepository, times(1)).findByToken(oldToken);
    assertThat(newToken.getUser()).isEqualTo(user);
    verify(verificationTokenRepository, times(1)).deleteByToken(oldToken);
    verify(verificationTokenRepository, times(1)).save(newToken);
  }

  @DisplayName("Failed new verification token generation")
  @Test
  public void givenInvalidRefreshTokenWhenAccessTokenHasExpiredThenNewThrowError() {
    // Given
    String oldToken = UUID.randomUUID().toString();
    verificationToken = new VerificationToken(oldToken, user, 1);
    given(verificationTokenRepository.findByToken(oldToken)).willReturn(null);

    // When
    BadCredentialsException exception =
        assertThrows(
            BadCredentialsException.class,
            () -> authenticationService.validateVerificationToken(oldToken));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Invalid verification token");
    verify(verificationTokenRepository, times(1)).findByToken(oldToken);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @DisplayName("Reset forgotten user password")
  @Test
  public void givenValidPasswordRequestDataWhenUserRequestPasswordResetThenPasswordIsReset() {
    // Given
    String resetTokenString = UUID.randomUUID().toString();
    passwordResetRequest = new PasswordResetRequest("elaeis@example.com", newPassword);
    passwordResetToken = new PasswordResetToken(resetTokenString, user);
    given(passwordResetTokenService.validatePasswordResetToken(resetTokenString)).willReturn(user);

    // When
    String result =
        authenticationService.resetForgottenUserPassword(passwordResetRequest, resetTokenString);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("Password has been reset successfully");
    verify(userService).updatePassword(user, newPassword);
    verify(passwordResetTokenService).deleteToken(resetTokenString);
  }

  @DisplayName("Failed forgotten password reset")
  @Test
  public void givenInvalidPasswordRequestDataWhenUserRequestPasswordResetThenThrowException() {
    // Given
    String resetTokenString = UUID.randomUUID().toString();
    passwordResetRequest = new PasswordResetRequest(newPassword);

    given(passwordResetTokenService.validatePasswordResetToken(resetTokenString)).willReturn(null);

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () ->
                authenticationService.resetForgottenUserPassword(
                    passwordResetRequest, resetTokenString));

    // Then
    assertThat(exception.getMessage())
        .isEqualTo("User not found for the provided password reset token");
    verifyNoMoreInteractions(passwordResetTokenRepository);
  }
}
