package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.event.listener.RegistrationCompleteEventListener;
import com.codeplanks.home360.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
  private final VerificationTokenRepository verificationTokenRepository;
  private final RegistrationCompleteEventListener eventListener;
  private final ApplicationEventPublisher publisher;

  @Value("${application.frontend.verify-email.url}")
  private String emailVerificationUrl;
  @Override
  public VerificationToken generateNewVerificationToken(String oldVerificationToken) {
    AppUser user = extractUserFromToken(oldVerificationToken);
    if (user != null) {
      verificationTokenRepository.deleteByToken(oldVerificationToken);
    }
    VerificationToken newVerificationToken = createNewVerificationToken(user);
    verificationTokenRepository.save(newVerificationToken);
    return newVerificationToken;
  }

  @Override
  public VerificationToken validateVerificationToken(String token) {
    VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
    if (verificationToken == null) {
      throw new BadCredentialsException("Invalid verification token");
    }
    Calendar calendar = Calendar.getInstance();
    if ((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
      throw new BadCredentialsException(
              "Token already expired. Click the  link below to request for a new token");
    }
    return verificationToken;
  }

  public String resendVerificationToken(String OldToken)
          throws MessagingException, UnsupportedEncodingException {
    validateVerificationToken(OldToken);
    VerificationToken verificationToken = generateNewVerificationToken(OldToken);
    AppUser appUser = verificationToken.getUser();
    resendVerificationTokenEmail(appUser, emailVerificationUrl, verificationToken);
    return "A new verification link has been sent to your email. Check your inbox to activate your account";
  }

  private AppUser extractUserFromToken(String token) {
    VerificationToken verificationToken = validateVerificationToken(token);
    return verificationToken.getUser();
  }

  private void resendVerificationTokenEmail(
          AppUser user, String applicationUrl, VerificationToken verificationToken)
          throws MessagingException, UnsupportedEncodingException {
    String url = applicationUrl + "?token=" + verificationToken.getToken();
    eventListener.sendVerificationEmail(url);
  }

  private VerificationToken createNewVerificationToken(AppUser user) {
    VerificationToken newToken = new VerificationToken();
    newToken.setToken(UUID.randomUUID().toString());
    newToken.setExpirationTime(getTokenExpirationTime());
    newToken.setUser(user);
    return newToken;
  }

  private Date getTokenExpirationTime() {
    return new VerificationToken().getTokenExpirationTime();
  }
}
