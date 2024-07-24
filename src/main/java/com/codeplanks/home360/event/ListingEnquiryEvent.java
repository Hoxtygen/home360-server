package com.codeplanks.home360.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ListingEnquiryEvent extends ApplicationEvent {
  private String enquirerEmail;
  private String listingId;
  public ListingEnquiryEvent(String enquirerEmail, String listingId) {
    super(enquirerEmail);
    this.enquirerEmail = enquirerEmail;
    this.listingId = listingId;
  }
}
