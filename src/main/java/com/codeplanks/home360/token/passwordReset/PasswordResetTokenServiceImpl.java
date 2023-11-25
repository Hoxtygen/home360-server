package com.codeplanks.home360.token.passwordReset;

import com.codeplanks.home360.exception.NotFoundException;
import com.codeplanks.home360.exception.UnauthorizedException;
import com.codeplanks.home360.user.AppUser;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.security.core.AuthenticationException;
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

//  @Override
//  public String validatePasswordResetToken(String passwordResetToken) {
//    PasswordResetToken passwordToken =
//            passwordResetTokenRepository.findByToken(passwordResetToken);
//    if (passwordToken == null) {
//      return "Invalid verification token";
//    }
//    Calendar calendar = Calendar.getInstance();
//    if ((passwordToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
//      return "Link already expired, resend link";
//    }
//    return "valid";
//  }

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

  // re-imple
@Override
  public String validatePasswordResetToken(String passwordResetToken) {
    PasswordResetToken passwordToken =
            Optional.of(passwordResetTokenRepository.findByToken(passwordResetToken))
                    .orElseThrow(()->  new NotFoundException("Invalid token"));
    Calendar calendar = Calendar.getInstance();
    if ((passwordToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
      throw  new UnauthorizedException("Link already expired, request for a new link");
    }
    return "valid";
  }
}
