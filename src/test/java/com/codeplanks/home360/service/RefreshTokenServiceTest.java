package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private RefreshTokenServiceImpl refreshTokenService;

  @Test
  void givenUserWithExistingToken_whenGenerateRefreshToken_thenUpdateExistingToken() {
    // Given
    AppUser user = new AppUser();
    user.setId(1);
    user.setEmail("test@example.com");

    RefreshToken existingToken = RefreshToken.builder()
        .user(user)
        .token("old-token")
        .expiryDate(LocalDateTime.now().minusDays(1))
        .build();

    given(refreshTokenRepository.findByUser(user)).willReturn(Optional.of(existingToken));
    given(refreshTokenRepository.save(any(RefreshToken.class))).willAnswer(invocation -> invocation.getArgument(0));

    // When
    RefreshToken newToken = refreshTokenService.generateRefreshToken(user);

    // Then
    assertThat(newToken).isNotNull();
    assertThat(newToken.getUser()).isEqualTo(user);
    assertThat(newToken.getToken()).isNotEqualTo("old-token"); // Token should be rotated
    assertThat(newToken.getExpiryDate()).isAfter(LocalDateTime.now());
    
    // Verify save was called with the SAME object (updated), not a new one
    verify(refreshTokenRepository).save(existingToken);
  }

  @Test
  void givenUserWithoutToken_whenGenerateRefreshToken_thenCreateNewToken() {
    // Given
    AppUser user = new AppUser();
    user.setId(2);
    user.setEmail("new@example.com");

    given(refreshTokenRepository.findByUser(user)).willReturn(Optional.empty());
    given(refreshTokenRepository.save(any(RefreshToken.class))).willAnswer(invocation -> invocation.getArgument(0));

    // When
    RefreshToken newToken = refreshTokenService.generateRefreshToken(user);

    // Then
    assertThat(newToken).isNotNull();
    assertThat(newToken.getUser()).isEqualTo(user);
    assertThat(newToken.getToken()).isNotNull();
    assertThat(newToken.getExpiryDate()).isAfter(LocalDateTime.now());
    
    verify(refreshTokenRepository).save(any(RefreshToken.class));
  }
}
