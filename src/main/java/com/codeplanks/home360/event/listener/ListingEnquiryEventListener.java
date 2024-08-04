package com.codeplanks.home360.event.listener;

import com.codeplanks.home360.service.EmailServiceImpl;
import com.codeplanks.home360.event.ListingEnquiryEvent;
import com.codeplanks.home360.service.ListingService;
import com.codeplanks.home360.domain.listing.ListingWithAgentInfo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListingEnquiryEventListener implements ApplicationListener<ListingEnquiryEvent> {
  private final JavaMailSender mailSender;
  private final ListingService listingService;
  private final EmailServiceImpl emailService;


  @Value("${application.frontend.user-listings-url.url}")
  private String userListingUrl;

  @Override
  @Async
  public void onApplicationEvent(ListingEnquiryEvent event) {
    String enquirerEmail = event.getEnquirerEmail();
    String url = userListingUrl + "/" + event.getListingId();
    ListingWithAgentInfo listing = listingService.getListingById(event.getListingId());
    String agentEmail = listing.getAgentInfo().getEmail();
    String agentFirstName = listing.getAgentInfo().getFirstName();
    try {
      sendEnquiryNotificationEmail(url, agentEmail, agentFirstName);
    } catch (MessagingException | UnsupportedEncodingException exception) {
      throw new RuntimeException(exception.getMessage());
    }
  }

  public void sendEnquiryNotificationEmail(String listingUrl, String agentEmail,
                                           String agentFirstName) throws MessagingException,
          UnsupportedEncodingException {
    Context context = new Context(Locale.ENGLISH);
    String subject = "Listing Enquiry Notification";

    String mailContent = "You have a new enquiry about one of your listings.Login to your " +
            "dashboard to view the content.The listing being enquired about can be found in the " +
            "url below.";

    context.setVariable("name", agentFirstName);
    context.setVariable("subject", subject);
    context.setVariable("message", mailContent);
    context.setVariable("listingLink", listingUrl);
    emailService.sendMail(agentEmail, subject, "listingEnquiries", context);
  }
}
