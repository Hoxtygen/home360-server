/* (C)2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listingEnquiries.EnquiryMessage;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReplyDTO;
import com.codeplanks.home360.domain.listingEnquiries.PaginatedListingEnquiriesChat;

public interface EnquiryMessageService {
  EnquiryMessage addReplyMessage(String enquiryId, ListingEnquiryMessageReplyDTO reply, int senderId);

  PaginatedListingEnquiriesChat getEnquiryMessages(String enquiryId, int page, int size);
}
