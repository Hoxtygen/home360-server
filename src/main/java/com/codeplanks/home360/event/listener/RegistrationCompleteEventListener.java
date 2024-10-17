/* (C)2024 */
package com.codeplanks.home360.event.listener;

import com.codeplanks.home360.domain.user.AppUser;
import com.codeplanks.home360.domain.verificationToken.VerificationToken;
import com.codeplanks.home360.event.RegistrationCompleteEvent;
import com.codeplanks.home360.repository.VerificationTokenRepository;
import com.codeplanks.home360.service.EmailServiceImpl;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

/**
 * @author Wasiu Idowu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener
    implements ApplicationListener<RegistrationCompleteEvent> {
  private final EmailServiceImpl emailService;
  private final VerificationTokenRepository verificationTokenRepository;
  private AppUser appUser;

  @Value("${application.frontend.verify-email.url}")
  private String emailVerificationUrl;

  @Override
  @Async
  public void onApplicationEvent(RegistrationCompleteEvent event) {
    appUser = event.getUser();
    String verificationToken = UUID.randomUUID().toString();
    saveUserVerificationToken(appUser, verificationToken);
    String url = emailVerificationUrl + "?token=" + verificationToken;
    try {
      sendVerificationEmail(url);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendVerificationEmail(String url)
      throws MessagingException, UnsupportedEncodingException {
    Context context = new Context(Locale.ENGLISH);
    String firstName = appUser.getFirstName();
    String userEmail = appUser.getEmail();

    String subject = "Home360 Email Verification";
    String mailContent =
        "Thank you for registering with us. Please, follow the link below to "
            + "complete your registration.";

    context.setVariable("name", firstName);
    context.setVariable("subject", subject);
    context.setVariable("message", mailContent);
    context.setVariable("verificationLink", url);

    emailService.sendMail(userEmail, subject, "userAuth", context);
  }

  public void sendPasswordResetEmail(String url)
      throws MessagingException, UnsupportedEncodingException {
    Context context = new Context(Locale.ENGLISH);
    String firstName = appUser.getFirstName();

    String subject = "Password Reset";
    String mailContent =
        "You recently requested to reset your password. Please, follow the link "
            + "below to complete the action.";

    context.setVariable("name", firstName);
    context.setVariable("subject", subject);
    context.setVariable("message", mailContent);
    context.setVariable("passwordResetLink", url);

    emailService.sendMail(appUser.getEmail(), subject, "resetPassword", context);
  }

  private void saveUserVerificationToken(AppUser theUser, String token) {
    VerificationToken verificationToken = new VerificationToken(token, theUser);
    verificationTokenRepository.save(verificationToken);
  }
}
