/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.config.JwtService;
import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.token.TokenRequest;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.exception.ExpiredTokenException;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wasiu Idowu
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private final JwtService jwtService;
  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Override
  public RefreshToken generateRefreshToken(AppUser user) {
    RefreshToken refreshToken =
        RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(LocalDateTime.now().plusSeconds(259200))
            .build();
    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public TokenResponse refreshToken(TokenRequest request) {
    RefreshToken refreshToken = getRefreshToken(request.getToken());
    AppUser user = getUserIfRefreshTokenValid(refreshToken);
    String accessToken = generateAccessToken(user);
    return buildRefreshTokenResponse(accessToken, request.getToken());
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Override
  public RefreshToken verifyRefreshTokenExpiration(RefreshToken refreshToken) {
    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new ExpiredTokenException(
          refreshToken.getToken() + "Refresh token has expired, make a new" + " login request");
    }
    return refreshToken;
  }

  private RefreshToken getRefreshToken(String token) {
    return findByToken(token)
        .orElseThrow(() -> new NotFoundException("Refresh token not in " + "database"));
  }

  private AppUser getUserIfRefreshTokenValid(RefreshToken refreshToken) {
    return verifyRefreshTokenExpiration(refreshToken).getUser();
  }

  private String generateAccessToken(AppUser user) {
    return jwtService.generateToken(user);
  }

  private TokenResponse buildRefreshTokenResponse(String accessToken, String refreshToken) {
    return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }
}
