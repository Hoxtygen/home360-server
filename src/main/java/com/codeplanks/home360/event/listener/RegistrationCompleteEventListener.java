package com.codeplanks.home360.event.listener;

import com.codeplanks.home360.auth.AuthenticationServiceImpl;
import com.codeplanks.home360.event.RegistrationCompleteEvent;
import com.codeplanks.home360.user.AppUser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

  @Override
  public void onApplicationEvent(RegistrationCompleteEvent event) {
    appUser = event.getUser();
    String verificationToken = UUID.randomUUID().toString();
    authenticationService.saveUserVerificationToken(appUser, verificationToken);
    String url = event.getApplicationUrl() + "/verifyEmail?token=" + verificationToken;
    try {
      sendVerificationEmail(url);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

  }

  public void sendVerificationEmail(
          String url) throws MessagingException, UnsupportedEncodingException {
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

}
