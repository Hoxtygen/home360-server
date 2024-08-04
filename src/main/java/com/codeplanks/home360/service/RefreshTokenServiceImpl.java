package com.codeplanks.home360.service;


import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.repository.RefreshTokenRepository;
import com.codeplanks.home360.repository.UserRepository;
import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Wasiu Idowu
 */

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Override
  public RefreshToken generateRefreshToken(AppUser user) {
    RefreshToken refreshToken = RefreshToken
            .builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(Instant.now().plusSeconds(259200))
            .build();
    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Override
  public RefreshToken verifyRefreshTokenExpirationTime(RefreshToken refreshToken) {
    if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(refreshToken);
      throw new RuntimeException(refreshToken.getToken() + "Refresh token has expired, make a new" +
              " signin request");
    }
    return refreshToken;
  }
}