/* (C)2024-2025 */
package com.codeplanks.home360.service;

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
import com.codeplanks.home360.exception.DuplicateSessionException;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UserAlreadyExistsException;
import com.codeplanks.home360.repository.*;
import com.codeplanks.home360.utils.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
  @Mock Authentication authentication;
  LocalDateTime localDateTime = LocalDateTime.now();
  @Mock VerificationTokenServiceImpl verificationTokenService;
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
  @Mock private JwtUtils jwtUtils;
  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private BlacklistedTokenRepository blacklistedTokenRepository;
  @Mock private HttpServletResponse response;
  @Mock private Cookie cookie;
  @Mock private RedisTemplate<String, Object> redisTemplate;
  @Mock private HashOperations<String, Object, Object> hashOperations;
  @Mock private ValueOperations<String, Object> valueOperations;

  private RegisterRequest request;
  private AppUser user;
  @Mock private VerificationToken verificationToken;
  private PasswordUpdateDTO passwordUpdateDTO;

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

  private SessionUserInfo sessionUserInfo;

  @BeforeEach
  public void setup() {
    sessionUserInfo = new SessionUserInfo("FireFox", "Win10", "Desktop", "192.168.254.5");

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
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("register new user successfully")
  void GivenAppUserObjectWhenRegisterUserThenRegistrationSuccessful() {
    // Given
    when(userRepository.save(any(AppUser.class))).thenReturn(user);
    when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

    // When
    AppUser response = authenticationService.register(request);

    // Then
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
    // Given
    given(userService.userExists(request.getEmail().toLowerCase(), request.getPhoneNumber()))
        .willReturn(true);

    // When
    UserAlreadyExistsException exception =
        assertThrows(
            UserAlreadyExistsException.class, () -> authenticationService.register(request));

    // Then
    verify(userRepository, never()).save(any(AppUser.class));
    assertEquals("User with email or phone number already exists", exception.getMessage());
  }

  @DisplayName("register duplicate phone number throws UserAlreadyExistException")
  @Test
  public void givenExistingPhoneNumberWhenSaveAppUserThenThrowsException() {
    // Given -
    given(userService.userExists(request.getEmail().toLowerCase(), request.getPhoneNumber()))
        .willReturn(true);

    // When
    UserAlreadyExistsException exception =
        assertThrows(
            UserAlreadyExistsException.class, () -> authenticationService.register(request));

    // Then
    verify(userRepository, never()).save(any(AppUser.class));
    assertEquals("User with email or phone number already exists", exception.getMessage());
  }

  @DisplayName("user login successfully")
  @Test
  public void givenAppUserCredentials_whenLoginUser_thenReturnAppUser() {
    // Given

    AuthenticationRequest authRequest =
        new AuthenticationRequest("elaeis@example.com", userPassword);

    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willReturn(authentication);

    given(userService.getUser(authRequest.getEmail().toLowerCase())).willReturn(user);
    given(jwtService.generateToken(user)).willReturn(token);
    given(refreshTokenService.generateRefreshToken(user)).willReturn(refreshToken);

    //      given(redisTemplate.hasKey(startsWith("session:"))).willReturn(false);

    given(redisTemplate.opsForHash()).willReturn(hashOperations);

    given(redisTemplate.opsForValue())
        .willReturn(valueOperations); // Ensure opsForValue() returns mock

    doNothing().when(hashOperations).put(any(), any(), any());

    // When
    AuthenticationResponse response = authenticationService.login(authRequest, sessionUserInfo);

    // Then
    assertAll(
        () -> assertThat(response).isNotNull(),
        () -> assertThat(response.getFirstName()).isEqualTo("Elaeis"),
        () -> assertThat(response.getLastName()).isEqualTo("Guineensis"),
        () -> assertThat(response.getToken().getAccessToken()).isEqualTo(token));

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService, times(1)).generateToken(user);
    verify(refreshTokenService, times(1)).generateRefreshToken(user);
  }

  @DisplayName("user cannot login if an active session exists")
  @Test
  void givenActiveSessionWhenUserLoginThenThrowDuplicateSessionException() {
    // Given
    AuthenticationRequest authRequest =
        new AuthenticationRequest("elaeis@example.com", userPassword);

    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willReturn(authentication);

    given(userService.getUser(authRequest.getEmail().toLowerCase())).willReturn(user);

    // Simulate Redis returning an active session
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    given(valueOperations.get("active_session:" + user.getEmail()))
        .willReturn("existingSessionToken");

    // When & Then
    assertThrows(
        DuplicateSessionException.class,
        () -> authenticationService.login(authRequest, sessionUserInfo));

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userService, times(1)).getUser(authRequest.getEmail().toLowerCase());
    verify(valueOperations, times(1))
        .get("active_session:" + user.getEmail()); // Ensure Redis lookup happens
  }

  @DisplayName("incorrect user email login")
  @Test
  public void givenNonExistentAppUser_whenUserLogin_thenThrowsException() {
    // Given
    AuthenticationRequest authRequest =
        new AuthenticationRequest("nonexistent@example.com", "password123");
    //    given(userService.emailExists(authRequest.getEmail())).willReturn(false);

    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willThrow(new BadCredentialsException("Incorrect username/password"));
    // When
    assertThrows(
        BadCredentialsException.class,
        () -> authenticationService.login(authRequest, sessionUserInfo));

    // Then

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userService, never()).getUser(any());
  }

  @DisplayName("incorrect password")
  @Test
  public void givenIncorrectPasswordWhenUserLoginThenThrowsException() {
    AuthenticationRequest authenticationRequest =
        new AuthenticationRequest("elaeis@example.com", "password123");

    // Given
    given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .willThrow(new BadCredentialsException("Incorrect username/password"));

    // When
    assertThrows(
        BadCredentialsException.class,
        () -> authenticationService.login(authenticationRequest, sessionUserInfo));

    // Then
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userService, never()).getUser(any());
  }

  @DisplayName("verify user successfully")
  @Test
  public void givenCorrectVerificationTokenWhenUserVerifyAccountThenUserIsEnabled() {
    // Given
    String tokenString = UUID.randomUUID().toString();
    VerificationToken verificationToken1 =
        VerificationToken.builder()
            .user(user)
            .id(1)
            .token(tokenString)
            .expirationTime(LocalDateTime.now().plusHours(48))
            .build();
    given(verificationTokenService.validateVerificationToken(tokenString))
        .willReturn(verificationToken1);

    // When
    String result = authenticationService.verifyAccount(tokenString);

    // Then
    assertThat(user.isEnabled()).isTrue();
    assertThat(result).isEqualTo("Email verified successfully. Proceed to login to your account");
    verify(userRepository, times(1)).save(user);
    verify(verificationTokenService, times(1)).validateVerificationToken(tokenString);
  }

  @DisplayName("Reset forgotten user password")
  @Test
  public void givenValidPasswordRequestDataWhenUserRequestPasswordResetThenPasswordIsReset() {
    // Given
    String resetTokenString = UUID.randomUUID().toString();
    passwordUpdateDTO = new PasswordUpdateDTO(newPassword);
    passwordResetToken = new PasswordResetToken(resetTokenString, user);
    given(passwordResetTokenService.validatePasswordResetToken(resetTokenString)).willReturn(user);

    // When
    String result =
        authenticationService.resetForgottenUserPassword(passwordUpdateDTO, resetTokenString);

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
    passwordUpdateDTO = new PasswordUpdateDTO(newPassword);

    given(passwordResetTokenService.validatePasswordResetToken(resetTokenString)).willReturn(null);

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () ->
                authenticationService.resetForgottenUserPassword(
                    passwordUpdateDTO, resetTokenString));

    // Then
    assertThat(exception.getMessage())
        .isEqualTo("User not found for the provided password reset token");
    verifyNoMoreInteractions(passwordResetTokenRepository);
  }

  @Test
  @DisplayName("Send password reset link")
  void givenValidUserEmailWhenRequestForPasswordResetThenSendUserLinkForReset()
      throws MessagingException, UnsupportedEncodingException {
    // Given
    PasswordResetRequestDTO passwordResetRequestDTO =
        new PasswordResetRequestDTO("carica_papaya@example.com");
    AppUser user = new AppUser();
    user.setEmail("carica_papaya@example.com");
    given(userService.findUserByEmail(passwordResetRequestDTO.getUserEmail())).willReturn(user);

    // When
    String response = authenticationService.requestPasswordReset(passwordResetRequestDTO);

    // Then
    assertThat(response).isNotNull();
    assertThat(response).isEqualTo("Password reset link has been sent to your registered email.");
    verify(userService, times(1)).findUserByEmail(passwordResetRequestDTO.getUserEmail());
  }

  @Test
  @DisplayName("Invalid email for password reset")
  void GivenUnregisteredEmailWhenRequestForPasswordResetThenThrowNotFoundException() {
    // Given
    PasswordResetRequestDTO passwordResetRequestDTO =
        new PasswordResetRequestDTO("carica_papaya@example.com");
    given(userService.findUserByEmail(passwordResetRequestDTO.getUserEmail()))
        .willThrow(new NotFoundException("User does not exist"));

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> authenticationService.requestPasswordReset(passwordResetRequestDTO));

    // Then
    assertThat(exception.getMessage()).isEqualTo("User does not exist");
  }

  @Test
  @DisplayName("Successful logout")
  void givenValidTokenWhenLogoutThenInvalidateTokenAndRemoveRefreshTokenAndClearCookie() {
    // Given
    String token = "valid_access_token";
    String refreshToken = "valid_refresh_token";
    LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
    String sessionToken = "kdjfeuieyieroerieuife";

    given(jwtUtils.extractRefreshTokenFromRequest(servletRequest)).willReturn(refreshToken);

    doNothing().when(refreshTokenRepository).deleteByToken(refreshToken);

    // When
    String result = authenticationService.logout(token, servletRequest, response, sessionToken);

    // Then
    assertThat(result).isEqualTo("User successfully logged out");
    verify(refreshTokenRepository, times(1)).deleteByToken(refreshToken);

    // Verify that cookie was cleared
    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
    verify(response, times(2)).addCookie(cookieCaptor.capture());

    List<Cookie> clearedCookies = cookieCaptor.getAllValues();

    assertThat(clearedCookies.size()).isEqualTo(2);

    Cookie refreshTokenCookie = clearedCookies.get(0);
    assertThat(refreshTokenCookie.getName()).isEqualTo("refreshToken");
    assertThat(refreshTokenCookie.getValue()).isNull();
    assertThat(refreshTokenCookie.getMaxAge()).isEqualTo(0);

    Cookie sessionTokenCookie = clearedCookies.get(1);
    assertThat(sessionTokenCookie.getName()).isEqualTo("sessionToken");
    assertThat(sessionTokenCookie.getValue()).isNull();
    assertThat(sessionTokenCookie.getMaxAge()).isEqualTo(0);
  }

  @Test
  @DisplayName("Logout should work even when no refresh token is present")
  void givenNoRefreshToken_whenLogout_thenInvalidateTokenAndClearCookie() {
    // Given
    String token = "valid-access-token";
    String sessionToken = "kdjfeuieyieroerieuife";

    given(jwtUtils.extractRefreshTokenFromRequest(servletRequest)).willReturn(null);

    // When
    String result = authenticationService.logout(token, servletRequest, response, sessionToken);

    // Then
    assertThat(result).isEqualTo("User successfully logged out");
    verify(refreshTokenRepository, never()).deleteByToken(anyString());

    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
    verify(response, times(2)).addCookie(cookieCaptor.capture());

    List<Cookie> clearedCookies = cookieCaptor.getAllValues();
    assertThat(clearedCookies.size()).isEqualTo(2);

    Cookie sessionTokenCookie = clearedCookies.get(1);
    assertThat(sessionTokenCookie.getName()).isEqualTo("sessionToken");
    assertThat(sessionTokenCookie.getValue()).isNull();
    assertThat(sessionTokenCookie.getMaxAge()).isEqualTo(0);
  }
}
