/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Wasiu Idowu
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
  VerificationToken findByToken(String token);

  void deleteByToken(String token);
}
