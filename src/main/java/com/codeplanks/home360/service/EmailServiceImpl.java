/* (C)2025 */
package com.codeplanks.home360.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${application.mail.mailSenderName}")
  private String mailSenderName;

  @Value("${application.mail.mailSenderAddress}")
  private String mailSenderAddress;

  @Override
  @Async
  public void sendMail(String to, String subject, String template, Context context)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    // Process the template with the given context
    String htmlContent = templateEngine.process(template, context);

    // Set email properties
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(htmlContent, true); // Set true for html content
    helper.setFrom(mailSenderAddress, mailSenderName);

    // Send the mail
    mailSender.send(message);
  }
}
