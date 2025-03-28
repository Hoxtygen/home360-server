/* (C)2025 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.auth.BlacklistedToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
  boolean existsByToken(String token);

  Optional<BlacklistedToken> findByToken(String token);
}
