package com.codeplanks.home360.token.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Wasiu Idowu
 * */

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
  Optional<RefreshToken> findByToken(String token);
}
