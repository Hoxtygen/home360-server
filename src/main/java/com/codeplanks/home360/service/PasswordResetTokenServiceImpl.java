package com.codeplanks.home360.service;

import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UnAuthorizedException;
import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.repository.PasswordResetTokenRepository;
import com.codeplanks.home360.domain.passwordReset.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;


/**
 * @author Wasiu Idowu
 */

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

  private final PasswordResetTokenRepository passwordResetTokenRepository;

  @Override
  public void createPasswordResetUserToken(AppUser user, String passwordToken) {
    PasswordResetToken passwordRestToken = new PasswordResetToken(passwordToken, user);
    passwordResetTokenRepository.save(passwordRestToken);
  }

  @Override
  public Optional<AppUser> findUserByPasswordToken(String passwordResetToken) {
    PasswordResetToken resetToken =
            passwordResetTokenRepository.findByToken(passwordResetToken);
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
  public String validatePasswordResetToken(String passwordResetToken) {
    PasswordResetToken passwordToken =
            Optional.of(passwordResetTokenRepository.findByToken(passwordResetToken))
                    .orElseThrow(()->  new NotFoundException("Invalid token"));
    Calendar calendar = Calendar.getInstance();
    if ((passwordToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
      throw  new UnAuthorizedException("Link already expired, request for a new link");
    }
    return "valid";
  }
}
