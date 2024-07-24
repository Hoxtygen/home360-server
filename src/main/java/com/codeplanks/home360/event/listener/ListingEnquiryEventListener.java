package com.codeplanks.home360.event.listener;

import com.codeplanks.home360.event.ListingEnquiryEvent;
import com.codeplanks.home360.listing.ListingService;
import com.codeplanks.home360.listing.ListingWithAgentInfo;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class ListingEnquiryEventListener implements ApplicationListener<ListingEnquiryEvent> {
  private final JavaMailSender mailSender;
  private final ListingService listingService;


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
    String subject = "Listing Enquiry Notification";
    String senderName = "Home360";

    String mailContent = "<p> Hi, " + agentFirstName + ", </p>" +
            "<p>You have a new enquiry about one of your listings." + "Login to your dashboard " +
            "to view the content." + "" +
            "The listing being enquired about can be found in the url below.</p>" +
            "<a href=\"" + listingUrl + "\">View Listing</a>" +
            "<p> Home360 Enquiry Team";
    MimeMessage message = mailSender.createMimeMessage();
    var messageHelper = new MimeMessageHelper(message);
    messageHelper.setFrom("udubit@hotmail.com", senderName);
    messageHelper.setTo(agentEmail);
    messageHelper.setSubject(subject);
    messageHelper.setText(mailContent, true);
    mailSender.send(message);
  }
}
