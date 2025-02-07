/* (C)2025 */
package com.codeplanks.home360.websocket;

import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReply;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReplyDTO;
import com.codeplanks.home360.service.ListingEnquiryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
  private final ListingEnquiryServiceImpl listingEnquiryService;

  @Autowired
  public ChatController(ListingEnquiryServiceImpl listingEnquiryService) {
    this.listingEnquiryService = listingEnquiryService;
  }

  @MessageMapping("/chat/{enquiryId}/sendMessage")
  @SendTo("/topic/{enquiryId}")
  public ListingEnquiryMessageReply sendMessage(
      @DestinationVariable String enquiryId, @Payload ListingEnquiryMessageReplyDTO chatMessage) {
    return listingEnquiryService.addReplyMessage(enquiryId, chatMessage);
  }
}
