/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import com.codeplanks.home360.domain.token.TokenRequest;
import com.codeplanks.home360.domain.token.TokenResponse;
import com.codeplanks.home360.domain.user.AppUser;
import java.util.Optional;

/**
 * @author Wasiu Idowu
 * */
public interface RefreshTokenService {
  RefreshToken generateRefreshToken(AppUser user);

  Optional<RefreshToken> findByToken(String token);

  RefreshToken verifyRefreshTokenExpiration(RefreshToken token);

  TokenResponse refreshToken(TokenRequest request);
}
