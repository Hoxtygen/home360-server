/* (C)2025 */
package com.codeplanks.home360.event.listener;

import com.codeplanks.home360.event.DuplicateSessionEvent;
import com.codeplanks.home360.service.EmailServiceImpl;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class DuplicateSessionEventListener implements ApplicationListener<DuplicateSessionEvent> {
  private final EmailServiceImpl emailService;

  @Override
  @Async
  public void onApplicationEvent(@NonNull DuplicateSessionEvent event) {
    try {
      sendDuplicateSessionEmail(
          event.getUserEmail(),
          event.getIpAddress(),
          event.getOperatingSystem(),
          event.getBrowserAgent(),
          event.getDeviceType());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void sendDuplicateSessionEmail(
      String userEmail,
      String ipAddress,
      String operatingSystem,
      String browserAgent,
      String deviceType)
      throws MessagingException, UnsupportedEncodingException {
    Context context = new Context(Locale.ENGLISH);

    String subject = "Suspicious login activity";

    String mailContent =
        """
We noticed a suspicious login activity on your account.
The login activity is trying to create a new session while you
currently have an active one.
""";
    context.setVariable("IpAddress", ipAddress);
    context.setVariable("operatingSystem", operatingSystem);
    context.setVariable("deviceType", deviceType);
    context.setVariable("browserAgent", browserAgent);
    context.setVariable("message", mailContent);

    emailService.sendMail(userEmail, subject, "duplicateSession", context);
  }
}
