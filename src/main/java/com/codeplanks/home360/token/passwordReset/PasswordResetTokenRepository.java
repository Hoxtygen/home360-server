package com.codeplanks.home360.token.passwordReset;

import com.codeplanks.home360.token.passwordReset.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Wasiu Idowu
 * */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
  PasswordResetToken findByToken(String token);
}
