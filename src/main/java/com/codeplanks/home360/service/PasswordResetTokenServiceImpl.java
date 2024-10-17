/* (C)2024 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.auth.PasswordResetToken;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.exception.ExpiredTokenException;
import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.repository.PasswordResetTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Wasiu Idowu
 */
@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

  private final PasswordResetTokenRepository passwordResetTokenRepository;
  Logger logger = LoggerFactory.getLogger(PasswordResetTokenServiceImpl.class);

  @Override
  public void createPasswordResetUserToken(AppUser user, String passwordToken) {
    PasswordResetToken passwordRestToken = new PasswordResetToken(passwordToken, user);
    passwordResetTokenRepository.save(passwordRestToken);
  }

  @Override
  public Optional<AppUser> findUserByPasswordToken(String passwordResetToken) {
    PasswordResetToken resetToken = findPasswordResetToken(passwordResetToken);
    if (resetToken.getUser() == null) {
      throw new NotFoundException("Invalid user");
    }
    return Optional.of(resetToken.getUser());
  }

  @Override
  public PasswordResetToken findPasswordResetToken(String resetToken) {
    PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(resetToken);
    if (passwordResetToken == null) {
      throw new NotFoundException("Token not found");
    }
    return passwordResetToken;
  }

  @Override
  public AppUser validatePasswordResetToken(String passwordResetToken) {
    PasswordResetToken passwordToken = findPasswordResetToken(passwordResetToken);
    if (passwordToken.getExpirationTime().isBefore(LocalDateTime.now())) {
      throw new ExpiredTokenException("Link already expired, request for a new link");
    }
    return passwordToken.getUser();
  }

  @Override
  public void deleteToken(String token) {
    passwordResetTokenRepository.deleteByToken(token);
  }
}
