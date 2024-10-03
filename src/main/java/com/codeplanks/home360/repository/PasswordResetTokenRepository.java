/* (C)2024 */
package com.codeplanks.home360.repository;

import com.codeplanks.home360.domain.auth.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Wasiu Idowu
 * */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
  PasswordResetToken findByToken(String token);
}
