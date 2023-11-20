package com.codeplanks.home360.token.refreshToken;


import com.codeplanks.home360.user.AppUser;

import java.util.Optional;

/**
 * @author Wasiu Idowu
 * */
public interface RefreshTokenService {
  RefreshToken generateRefreshToken(AppUser user);
  Optional<RefreshToken> findByToken(String token);

  RefreshToken verifyRefreshTokenExpirationTime(RefreshToken token);
}
