/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.exception.ExpiredTokenException;
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
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Override
  public RefreshToken verifyRefreshTokenExpirationTime(RefreshToken refreshToken) {
    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new ExpiredTokenException(
          refreshToken.getToken() + "Refresh token has expired, make a new" + " login request");
    }
    return refreshToken;
  }
}
