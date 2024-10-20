/* (C)2024 */
package com.codeplanks.home360.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.user.Role;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.event.listener.RegistrationCompleteEventListener;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.UserRepository;
import com.codeplanks.home360.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {
  @InjectMocks private VerificationTokenServiceImpl verificationTokenService;
  @Mock private UserRepository userRepository;
  @Mock private VerificationTokenRepository verificationTokenRepository;
  @Mock private RegistrationCompleteEventListener eventListener;
  private AppUser appUser;
  private VerificationToken verificationToken;

  @Value("${application.security.password}")
  private String userPassword;

  @BeforeEach
  void setUp() {
    appUser =
        AppUser.builder()
            .id(1)
            .firstName("Elaeis")
            .lastName("Guineensis")
            .email("elaeis@example.com")
            .address("221B, Baker street, London")
            .phoneNumber("08030123456")
            .password(userPassword)
            .role(Role.USER)
            .createdAt(new Date())
            .updatedAt(new Date())
            .build();
  }

  @Test
  @DisplayName("Generate verification token")
  void givenValidRefreshTokenWhenAccessTokenHasExpiredThenNewTokenIsGenerated() {
    // Given
    String oldToken = UUID.randomUUID().toString();
    verificationToken = new VerificationToken(oldToken, appUser, 1);
    given(verificationTokenRepository.findByToken(oldToken)).willReturn(verificationToken);

    // When
    VerificationToken newToken = verificationTokenService.generateNewVerificationToken(oldToken);

    // Then
    assertThat(newToken).isNotNull();
    verify(verificationTokenRepository, times(1)).findByToken(oldToken);
    assertThat(newToken.getUser()).isEqualTo(appUser);
    verify(verificationTokenRepository, times(1)).deleteByToken(oldToken);
    verify(verificationTokenRepository, times(1)).save(newToken);
  }

  @Test
  @DisplayName("Failed new verification token generation")
  public void givenInvalidRefreshTokenWhenAccessTokenHasExpiredThenNewThrowError() {
    // Given
    String oldToken = UUID.randomUUID().toString();
    verificationToken = new VerificationToken(oldToken, appUser, 1);
    given(verificationTokenRepository.findByToken(oldToken)).willReturn(null);

    // When
    BadCredentialsException exception =
        assertThrows(
            BadCredentialsException.class,
            () -> verificationTokenService.validateVerificationToken(oldToken));

    // Then
    assertThat(exception.getMessage()).isEqualTo("Invalid verification token");
    verify(verificationTokenRepository, times(1)).findByToken(oldToken);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @Test
  @DisplayName("Validate verification token")
  void validateVerificationToken() {
    // Given
    String token = UUID.randomUUID().toString();
    verificationToken = new VerificationToken(token, appUser, 1);
    given(verificationTokenRepository.findByToken(token)).willReturn(verificationToken);
    // When
    VerificationToken result = verificationTokenService.validateVerificationToken(token);
    // Then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.getUser().getFirstName()).isEqualTo("Elaeis"),
        () -> assertThat(result.getUser().getLastName()).isEqualTo("Guineensis"),
        () -> assertThat(result.getUser().getEmail()).isEqualTo("elaeis@example.com"),
        () -> assertThat(result.getUser().getAddress()).isEqualTo("221B, Baker street, London"));
  }

  @Test
  @DisplayName("Invalid verification token")
  public void
      givenInvalidVerificationTokenWhenUserVerifyAccountThenThrowsBadCredentialsException() {
    // Given - precondition or setup
    String tokenString = UUID.randomUUID().toString();
    given(verificationTokenRepository.findByToken(tokenString)).willReturn(null);

    // When - action or the behaviour we're testing for
    BadCredentialsException exception =
        assertThrows(
            BadCredentialsException.class,
            () -> verificationTokenService.validateVerificationToken(tokenString));

    // Then - verify the output
    assertThat(exception.getMessage()).isEqualTo("Invalid verification token");
    verify(verificationTokenRepository, times(1)).findByToken(tokenString);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @Test
  @DisplayName("resend verification token")
  void GivenValidOldTokenWhenUserRequestForNewTokenThenUserGetNewToken()
      throws MessagingException, UnsupportedEncodingException {
    // Given
    VerificationToken oldToken =
        VerificationToken.builder()
            .token(UUID.randomUUID().toString())
            .user(appUser)
            .expirationTime(LocalDateTime.now().plusHours(1))
            .build();

    VerificationToken newToken =
        VerificationToken.builder()
            .token(UUID.randomUUID().toString())
            .user(appUser)
            .expirationTime(LocalDateTime.now().plusHours(48))
            .build();
    String oldTokenValue = oldToken.getToken();
    String expectedMessage =
        "A new verification link has been sent to your email. Check your inbox to activate your"
            + " account";
    when(verificationTokenRepository.findByToken(oldTokenValue)).thenReturn(oldToken);
    when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(newToken);

    // When
    String result = verificationTokenService.resendVerificationToken(oldTokenValue);

    // Then
    assertThat(result).isEqualTo(expectedMessage);
    verify(eventListener).sendVerificationEmail(any(String.class));
    verify(verificationTokenRepository, times(2)).findByToken(oldTokenValue);
    verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
  }

  @Test
  @DisplayName("Old token not found  for resend verification token ")
  void givenATokenWhenUsedForVerificationThenThrowException() {
    // Given
    String invalidTokenValue = UUID.randomUUID().toString();
    when(verificationTokenRepository.findByToken(invalidTokenValue)).thenReturn(null);

    // When
    BadCredentialsException exception =
        assertThrows(
            BadCredentialsException.class,
            () -> {
              verificationTokenService.resendVerificationToken(invalidTokenValue);
            });
    // Then
    assertThat(exception.getMessage()).isEqualTo("Invalid verification token");
    verify(verificationTokenRepository, never()).save(any(VerificationToken.class));
  }

  @Test
  @DisplayName("Invalid token with no user-attached")
  void GivenTokenWithNoUserWhenRequestForNewTokenThenThrowNotFoundException() {
    // Given
    VerificationToken tokenWithNoUser =
        VerificationToken.builder()
            .token(UUID.randomUUID().toString())
            .user(null) // No user associated
            .expirationTime(LocalDateTime.now().plusHours(1))
            .build();
    String tokenValue = tokenWithNoUser.getToken();
    given(verificationTokenRepository.findByToken(tokenValue)).willReturn(tokenWithNoUser);

    // When
    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> {
              verificationTokenService.resendVerificationToken(tokenValue);
            });
    // Then
    assertThat(exception.getMessage()).isEqualTo("Invalid verification token");
    verify(verificationTokenRepository, never()).save(any(VerificationToken.class));
  }

  @Test
  @DisplayName("New verification email sending failed")
  void GivenValidTokenWhenUserRequestNewTokenThenEmailFails()
      throws MessagingException, UnsupportedEncodingException {
    // Given
    VerificationToken validToken =
        VerificationToken.builder()
            .token(UUID.randomUUID().toString())
            .user(appUser)
            .expirationTime(LocalDateTime.now().plusHours(1))
            .build();
    String tokenValue = validToken.getToken();
    when(verificationTokenRepository.findByToken(tokenValue)).thenReturn(validToken);

    // Simulate email sending failure
    doThrow(new MessagingException("Email sending failed"))
        .when(eventListener)
        .sendVerificationEmail(any(String.class));

    // When
    MessagingException exception =
        assertThrows(
            MessagingException.class,
            () -> {
              verificationTokenService.resendVerificationToken(tokenValue);
            });

    //  Then
    assertThat(exception.getMessage()).isEqualTo("Email sending failed");
  }
}
