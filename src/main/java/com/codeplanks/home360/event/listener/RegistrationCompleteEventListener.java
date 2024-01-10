package com.codeplanks.home360.event.listener;

import com.codeplanks.home360.auth.AuthenticationServiceImpl;
import com.codeplanks.home360.event.RegistrationCompleteEvent;
import com.codeplanks.home360.token.verificationToken.VerificationToken;
import com.codeplanks.home360.user.AppUser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;


/**
 * @author Wasiu Idowu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements
        ApplicationListener<RegistrationCompleteEvent> {
  private final AuthenticationServiceImpl authenticationService;
  private final JavaMailSender mailSender;
  private AppUser appUser;

  @Value("${application.frontend.verify-email.url}")
  private  String emailVerificationUrl;

  @Override
  @Async
  public void onApplicationEvent(RegistrationCompleteEvent event) {
    appUser = event.getUser();
    String verificationToken = UUID.randomUUID().toString();
    authenticationService.saveUserVerificationToken(appUser, verificationToken);
    String url = emailVerificationUrl + "?token=" + verificationToken;
    try {
      sendVerificationEmail(url);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

  }

  public void sendVerificationEmail(String url)
          throws MessagingException, UnsupportedEncodingException {
    String subject = "Home360 Email Verification";
    String senderName = "Home360";
    String mailContent = "<p> Hi, " + appUser.getFirstName() + ", </p>" +
            "<p>Thank you for registering with us," + "" +
            "Please, follow the link below to complete your registration.</p>" +
            "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
            "<p> Thank you <br> Users Registration Portal Service";
    MimeMessage message = mailSender.createMimeMessage();
    var messageHelper = new MimeMessageHelper(message);
    messageHelper.setFrom("udubit@hotmail.com", senderName);
    messageHelper.setTo(appUser.getEmail());
    messageHelper.setSubject(subject);
    messageHelper.setText(mailContent, true);
    mailSender.send(message);
  }

  public void sendPasswordResetVerificationEmail(
          String url) throws MessagingException, UnsupportedEncodingException {
    String subject = "Password Reset";
    String senderName = "Home360";
    String mailContent = "<p> Hi, " + appUser.getFirstName() + ", </p>" +
            "<p><b>You recently requested to reset your password,</b>" + "" +
            "Please, follow the link below to complete the action.</p>" +
            "<a href=\"" + url + "\">Reset password</a>" +
            "<p> Users Registration Portal Service";
    MimeMessage message = mailSender.createMimeMessage();
    var messageHelper = new MimeMessageHelper(message);
    messageHelper.setFrom("udubit@hotmail.com", senderName);
    messageHelper.setTo(appUser.getEmail());
    messageHelper.setSubject(subject);
    messageHelper.setText(mailContent, true);
    mailSender.send(message);
  }
}
