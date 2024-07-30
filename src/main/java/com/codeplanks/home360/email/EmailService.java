package com.codeplanks.home360.email;

import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

public interface EmailService {
  void sendMail(String to, String subject, String template,
                Context context) throws MessagingException, UnsupportedEncodingException;
}
