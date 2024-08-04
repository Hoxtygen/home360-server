package com.codeplanks.home360.service;


import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.refreshToken.RefreshToken;

import java.util.Optional;

/**
 * @author Wasiu Idowu
 * */
public interface RefreshTokenService {
  RefreshToken generateRefreshToken(AppUser user);
  Optional<RefreshToken> findByToken(String token);

  RefreshToken verifyRefreshTokenExpirationTime(RefreshToken token);
}
