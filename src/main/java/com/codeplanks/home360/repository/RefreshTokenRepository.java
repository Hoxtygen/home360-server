/* (C)2025 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.refreshToken.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Wasiu Idowu
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
  Optional<RefreshToken> findByToken(String token);

  void deleteByToken(String token);
}
