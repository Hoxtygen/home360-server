/* (C)2024-2025 */
package com.codeplanks.home360.service;

import com.codeplanks.home360.domain.listing.PaginatedResponse;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiry;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryDTO;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReply;
import com.codeplanks.home360.domain.listingEnquiries.ListingEnquiryMessageReplyDTO;
import java.util.List;

public interface ListingEnquiryService {
  ListingEnquiry makeEnquiry(ListingEnquiryDTO enquiryRequest);

  PaginatedResponse<ListingEnquiry> getListingEnquiries(int page, int size, Integer senderId);

  ListingEnquiry getListingEnquiryById(String enquiryMessageId);

  Boolean markMessageAsRead(String enquiryMessageId);

  ListingEnquiryMessageReply addReplyMessage(
      String enquiryId, ListingEnquiryMessageReplyDTO reply, int senderId);

  List<ListingEnquiry> getEnquiriesByListingId(String listingId);
}
