package com.codeplanks.home360.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Wasiu Idowu
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
  VerificationToken findByToken(String token);
}
