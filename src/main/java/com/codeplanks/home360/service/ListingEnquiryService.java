package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.PaginatedResponse;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryDTO;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReply;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReplyDTO;

public interface ListingEnquiryService {
  ListingEnquiryDTO makeEnquiry(ListingEnquiry enquiryRequest);

  PaginatedResponse<ListingEnquiry> getListingEnquiriesByAgentId(int page, int size);

  ListingEnquiry getListingEnquiryById(String enquiryMessageId);

  Boolean markMessageAsRead(String enquiryMessageId);

  ListingEnquiryMessageReply addReplyMessage(String enquiryId, ListingEnquiryMessageReplyDTO reply);
}
